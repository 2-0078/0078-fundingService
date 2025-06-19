package com.pieceofcake.fundingservice.batch.application.config;

import com.pieceofcake.fundingservice.batch.dto.FundingRefundDto;
import com.pieceofcake.fundingservice.funding.entity.Funding;
import com.pieceofcake.fundingservice.funding.entity.FundingStatus;
import com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer.FundingEvent;
import com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer.FundingKafkaProducer;
import com.pieceofcake.fundingservice.funding.infrastructure.kafka.producer.RefundEvent;
import com.pieceofcake.fundingservice.funding.infrastructure.repository.FundingRepository;
import com.pieceofcake.fundingservice.participation.application.RedisService;
import com.pieceofcake.fundingservice.participation.entity.FundingParticipation;
import com.pieceofcake.fundingservice.participation.infrastructure.FundingParticipationRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class FundingCloseConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityFactory;
    private final DataSource dataSource;
    private final FundingRepository fundingRepository;
    private final FundingParticipationRepository participationRepository;
    private final FundingKafkaProducer fundingKafkaProducer;
    private final RedisService redisService;

    @Bean
    public Job dailyFundingJob() {
        return new JobBuilder("dailyFundingJob", jobRepository)
                .start(updateFundingRemainPiecesStep())
                .next(fundingCloseStep())
                .next(refundFundingStep())
                .build();
    }

    @Bean
    public Step updateFundingRemainPiecesStep() {
        return new StepBuilder("updateFundingRemainPiecesStep", jobRepository)
                .<Funding, Funding>chunk(500, transactionManager)
                .reader(fundingRemainPiecesReader())
                .processor(fundingRemainPiecesProcessor())
                .writer(fundingRemainPiecesWriter())
                .build();
    }

    @Bean
    public JpaCursorItemReader<Funding> fundingRemainPiecesReader() {

        JpaCursorItemReader<Funding> reader = new JpaCursorItemReader<>();
        reader.setName("FundingCloseItemReader");
        reader.setEntityManagerFactory(entityFactory);
        reader.setQueryString("select f from Funding f where f.fundingStatus = 'FUNDING'");

        return reader;
    }

    @Bean
    public ItemProcessor<Funding, Funding> fundingRemainPiecesProcessor() {
        return item -> {
            item.updateRemainingPieces(redisService.getRemainingPieces(item.getFundingUuid()));
            return item;
        };
    }

    @Bean
    public ItemWriter<Funding> fundingRemainPiecesWriter() {
        return fundingRepository::saveAll;
    }


    /*
     * Step2
     * 공모 완료/취소 처리
     * */
    @Bean
    public Step fundingCloseStep() {
        return new StepBuilder("FundingCloseStep", jobRepository)
                .<Funding, Funding>chunk(500, transactionManager)
                .reader(fundingCloseItemReader(null))
                .processor(fundingCloseItemProcessor())
                .writer(fundingCloseItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JpaCursorItemReader<Funding> fundingCloseItemReader(
            @Value("#{jobParameters['date']}") String dateStr
    ) {
        LocalDate targetDate = LocalDate.parse(dateStr); // 예: "2025-06-18"
        LocalDateTime start = targetDate.atStartOfDay(); // 2025-06-18T00:00:00
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay().minusNanos(1); // 2025-06-18T23:59:59.999999999

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        return new JpaCursorItemReaderBuilder<Funding>()
                .name("fundingCloseItemReader")
                .entityManagerFactory(entityFactory)
                .queryString("select f from Funding f where f.fundingStatus = 'FUNDING' AND f.fundingDeadline BETWEEN :start AND :end")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemProcessor<Funding, Funding> fundingCloseItemProcessor() {
        return item -> {
            if (item.getTotalPieces() * 0.3 >= item.getRemainingPieces()) {
                item.updateFundingStatus(FundingStatus.COMPLETED);
            } else {
                item.updateFundingStatus(FundingStatus.CANCELLED);
            }

            //상태 변경 카프카
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    FundingEvent event = FundingEvent.builder()
                            .fundingUuid(item.getFundingUuid())
                            .productUuid(item.getProductUuid())
                            .totalPieces(item.getTotalPieces())
                            .remainingPieces(item.getRemainingPieces())
                            .piecePrice(item.getPiecePrice())
                            .fundingAmount(item.getFundingAmount())
                            .fundingDeadline(item.getFundingDeadline().toString())
                            .fundingStatus(item.getFundingStatus().toString())
                            .build();
                    fundingKafkaProducer.sendCreateFundingEvent(event);
                }
            });
            return item;
        };
    }

    @Bean
    public ItemWriter<Funding> fundingCloseItemWriter() {
        return fundingRepository::saveAll;
    }


    /*
     * Step3
     * 취소된 공모
     * 공모 참여내역 환불 저장
     * 공모 환불 내역 저장 -> 실제 환불처리 X
     * */
    @Bean
    public Step refundFundingStep() {
        return new StepBuilder("refundFundingStep", jobRepository)
                .<FundingRefundDto, FundingParticipation>chunk(500, transactionManager)
                .reader(refundFundingItemReader(null))
                .processor(refundFundingItemProcessor())
                .writer(refundParticipationItemWriter())
                .build();
    }

    @Bean
    @StepScope
    public JdbcCursorItemReader<FundingRefundDto> refundFundingItemReader(
            @Value("#{jobParameters['date']}") String dateStr
    ) {
        LocalDate targetDate = LocalDate.parse(dateStr); // 예: "2025-06-18"
        LocalDateTime start = targetDate.atStartOfDay(); // 2025-06-18T00:00:00
        LocalDateTime end = targetDate.plusDays(1).atStartOfDay().minusNanos(1); // 2025-06-18T23:59:59.999999999

        JdbcCursorItemReader<FundingRefundDto> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("SELECT " +
                "    f.funding_uuid, " +
                "    fp.member_uuid, " +
                "    fp.total_quantity, " +
                "    fp.total_quantity * f.piece_price as total_refund, " +
                "    ? as cancel_date " +
                "FROM funding f " +
                "LEFT JOIN ( " +
                "    SELECT " +
                "        fp.funding_uuid, " +
                "        fp.member_uuid, " +
                "        SUM(CASE WHEN fp.participate_status = 'JOIN' THEN fp.quantity ELSE 0 END) " +
                "        - SUM(CASE WHEN fp.participate_status = 'CANCEL' THEN fp.quantity ELSE 0 END) AS total_quantity " +
                "    FROM funding_participation fp " +
                "    GROUP BY fp.funding_uuid, fp.member_uuid " +
                ") fp ON f.funding_uuid = fp.funding_uuid " +
                "WHERE f.funding_status = 'CANCELLED' and fp.total_quantity > 0 " +
                "  AND f.funding_deadline BETWEEN ? AND ? ");

        reader.setPreparedStatementSetter(ps -> {
            ps.setObject(1, targetDate);
            ps.setObject(2, start);
            ps.setObject(3, end);
        });

        reader.setRowMapper((rs, rowNum) -> {

            return FundingRefundDto.builder()
                    .fundingUuid(rs.getString("funding_uuid"))
                    .memberUuid(rs.getString("member_uuid"))
                    .totalRefund(rs.getLong("total_refund"))
                    .totalQuantity(rs.getInt("total_quantity"))
                    .cancelDate(rs.getDate("cancel_date").toLocalDate())
                    .build();
        });

        return reader;
    }

    @Bean
    public ItemProcessor<FundingRefundDto, FundingParticipation> refundFundingItemProcessor() {
        return item -> {
            //kafka event -> 예치금 서비스
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    RefundEvent event = RefundEvent.builder()
                            .fundingUuid(item.getFundingUuid())
                            .memberUuid(item.getMemberUuid())
                            .totalRefund(item.getTotalRefund())
                            .cancelDate(item.getCancelDate().toString())
                            .build();
                    fundingKafkaProducer.sendRefundEvent(event);
                }
            });
            return item.toParticipateFundingRequestDto().toEntity();
        };
    }

    @Bean
    public ItemWriter<FundingParticipation> refundParticipationItemWriter() {
        return participationRepository::saveAll;
    }
}
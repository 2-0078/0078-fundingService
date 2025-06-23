package com.pieceofcake.fundingservice.participation.application;

import com.pieceofcake.fundingservice.common.entity.BaseResponseStatus;
import com.pieceofcake.fundingservice.common.exception.BaseException;
import com.pieceofcake.fundingservice.funding.dto.in.SetRedisFundingRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate redisTemplate;

    @Override
    public int getRemainingPieces(String fundingUuid) {
        String keyPrefix = "funding:" + fundingUuid;
        String value = (String) redisTemplate.opsForValue().get(keyPrefix + ":remain");
        return value != null ? Integer.parseInt(value) : 0;
    }

    @Override
    public long getPiecePrice(String fundingUuid) {
        String keyPrefix = "funding:" + fundingUuid;
        String value = (String) redisTemplate.opsForValue().get(keyPrefix + ":price");
        return value != null ? Long.parseLong(value) : 0;
    }

    @Override
    public void setRemainingPieces(SetRedisFundingRequestDto setRedisFundingRequestDto) {
        String keyPrefix = "funding:" + setRedisFundingRequestDto.getFundingUuid();

        try {
            if (setRedisFundingRequestDto.getTotalPieces() != null)
                redisTemplate.opsForValue().set(keyPrefix + ":total", String.valueOf(setRedisFundingRequestDto.getTotalPieces()));

            if (setRedisFundingRequestDto.getRemainingPieces() != null)
                redisTemplate.opsForValue().set(keyPrefix + ":remain", String.valueOf(setRedisFundingRequestDto.getRemainingPieces()));

            if (setRedisFundingRequestDto.getPiecePrice() != null)
                redisTemplate.opsForValue().set(keyPrefix + ":price", String.valueOf(setRedisFundingRequestDto.getPiecePrice()));
        } catch (Exception e) {
            log.error("Redis 저장 중 오류 발생: {}", e.getMessage(), e);
            // 필요 시 예외를 다시 던지거나, 시스템 로깅/알림 처리
            throw new BaseException(BaseResponseStatus.REDIS_ERROR);
        }
    }

    @Override
    public void deleteRemainingPieces(String fundingUuid) {
        String keyPrefix = "funding:" + fundingUuid;
        redisTemplate.delete(keyPrefix + ":remain");
    }

    @Override
    public long decreaseRemainPieces(String fundingUuid, int quantity) {
        try {
            String keyPrefix = "funding:" + fundingUuid;

            // Redis 키와 인자 설정
            List<String> keys = Collections.singletonList(keyPrefix + ":remain");
            List<String> args = Collections.singletonList(String.valueOf(quantity));

            // Lua 스크립트 실행 처리된 수량 반환
            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setLocation(new ClassPathResource("scripts/join.lua"));
            redisScript.setResultType(Long.class);

            return (Long)redisTemplate.execute(redisScript, keys, args.toArray(new String[0]));
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    @Override
    public boolean increaseRemainPieces(String fundingUuid, int quantity) {
        try {
            String keyPrefix = "funding:" + fundingUuid;

            // Redis 키와 인자 설정
            List<String> keys = Arrays.asList(keyPrefix + ":remain", keyPrefix + ":total");
            List<String> args = Collections.singletonList(String.valueOf(quantity));

            DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
            redisScript.setLocation(new ClassPathResource("scripts/cancel.lua"));
            redisScript.setResultType(Long.class);

            return (Long)redisTemplate.execute(redisScript, keys, args.toArray(new String[0])) > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

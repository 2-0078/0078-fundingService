# 🏦 Funding Service

펀딩 서비스는 사용자들이 상품의 조각을 구매하여 공동 소유할 수 있는 플랫폼입니다. PayPal 슈퍼바이저 개발자 기준으로 설계된 고성능, 고가용성의 마이크로서비스입니다.

## 📋 목차

- [기술 스택](#기술-스택)
- [아키텍처](#아키텍처)
- [주요 기능](#주요-기능)
- [설치 및 실행](#설치-및-실행)
- [API 문서](#api-문서)
- [테스트](#테스트)
- [부하 테스트](#부하-테스트)
- [모니터링](#모니터링)
- [개선사항](#개선사항)

## 🛠 기술 스택

### Backend
- **Java 17** - 메인 프로그래밍 언어
- **Spring Boot 3.5.0** - 애플리케이션 프레임워크
- **Spring Cloud 2025.0.0-RC1** - 마이크로서비스 프레임워크
- **Spring Data JPA** - 데이터 접근 계층
- **Spring Batch** - 배치 처리
- **Spring Kafka** - 메시징 시스템

### Database & Cache
- **MySQL** - 메인 데이터베이스
- **Redis** - 캐시 및 세션 저장소
- **Kafka** - 이벤트 스트리밍

### Infrastructure
- **Docker** - 컨테이너화
- **Eureka** - 서비스 디스커버리
- **Feign Client** - 서비스 간 통신

### Testing & Monitoring
- **JUnit 5** - 단위 테스트
- **Mockito** - 모킹 프레임워크
- **JMeter** - 부하 테스트
- **Logback** - 로깅

## 🏗 아키텍처

### 전체 아키텍처
```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Client App    │    │   Admin Panel   │    │   Mobile App    │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │      API Gateway          │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────▼─────────────┐
                    │    Funding Service        │
                    │  (Port: 8084)             │
                    └─────────────┬─────────────┘
                                  │
          ┌───────────────────────┼───────────────────────┐
          │                       │                       │
    ┌─────▼─────┐         ┌───────▼──────┐         ┌─────▼─────┐
    │   MySQL   │         │    Redis     │         │   Kafka   │
    │ Database  │         │    Cache     │         │  Events   │
    └───────────┘         └──────────────┘         └───────────┘
```

### 서비스 아키텍처 (Clean Architecture)
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ FundingController│  │ParticipationCtrl│  │  Swagger UI  │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                    Application Layer                        │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ FundingService  │  │ParticipationSvc │  │ OutboxService│ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                   Infrastructure Layer                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │ FundingRepository│  │ RedisService    │  │ KafkaProducer│ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                                │
┌─────────────────────────────────────────────────────────────┐
│                      Domain Layer                           │
│  ┌─────────────────┐  ┌─────────────────┐  ┌──────────────┐ │
│  │     Funding     │  │ Participation   │  │ OutboxEvent  │ │
│  └─────────────────┘  └─────────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

## 🚀 주요 기능

### 1. 펀딩 관리
- ✅ 펀딩 생성, 수정, 삭제
- ✅ 펀딩 상태 관리 (READY → FUNDING → COMPLETED/CANCELLED)
- ✅ 펀딩 목록 조회 (페이징, 필터링)
- ✅ 찜하기 기능

### 2. 펀딩 참여
- ✅ 실시간 재고 관리 (Redis Lua 스크립트)
- ✅ 동시성 제어 (Optimistic Lock)
- ✅ 결제 연동
- ✅ 조각 분배

### 3. 이벤트 처리
- ✅ Outbox 패턴 기반 이벤트 발행
- ✅ Kafka를 통한 비동기 이벤트 처리
- ✅ 재시도 메커니즘

### 4. 배치 처리
- ✅ 펀딩 마감 자동 처리
- ✅ 환불 처리
- ✅ 데이터 동기화

## 📦 설치 및 실행

### 1. 사전 요구사항
```bash
# Java 17 설치
brew install openjdk@17

# 환경 변수 설정
export JAVA_HOME=/opt/homebrew/opt/openjdk@17
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. 환경 변수 설정
```bash
# 데이터베이스 설정
export EC2_DB=172.31.2.85
export SPRING_DATASOURCE_USERNAME=team_0078
export SPRING_DATASOURCE_PASSWORD=00781234!

# Redis 설정
export REDIS_PASSWORD=00781234

# Kafka 설정
export EC2_HOST2=13.209.170.147

# Eureka 설정
export EC2_HOST=13.125.94.196

# Docker 설정
export DOCKER_USERNAME=eunseo0305
export DOCKER_PASSWORD=Eunseo@8@3!!
```

### 3. 빌드 및 실행
```bash
# 프로젝트 클론
git clone https://github.com/spharos-0078/0078-fundingService.git
cd 0078-fundingService

# 브랜치 체크아웃
git checkout fix/jason#01

# 빌드
./gradlew clean build

# 실행
./gradlew bootRun
```

### 4. Docker 실행
```bash
# Docker 이미지 빌드
docker build -t funding-service .

# Docker 실행
docker run -p 8084:8084 --env-file .env funding-service
```

## 📚 API 문서

### Swagger UI
애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:
```
http://localhost:8084/swagger-ui/index.html
```

### 주요 API 엔드포인트

#### 펀딩 관리
```http
GET    /api/v1/funding/all/{status}     # 펀딩 목록 조회
GET    /api/v1/funding/{fundingUuid}    # 펀딩 상세 조회
POST   /api/v1/funding                  # 펀딩 생성
PUT    /api/v1/funding                  # 펀딩 수정
PUT    /api/v1/funding/status           # 펀딩 상태 변경
DELETE /api/v1/funding/{fundingUuid}    # 펀딩 삭제
```

#### 펀딩 참여
```http
POST   /api/v1/participation            # 펀딩 참여
DELETE /api/v1/participation/{fundingUuid} # 펀딩 참여 취소
GET    /api/v1/participation/{fundingUuid} # 참여 여부 조회
GET    /api/v1/participation/remain/{fundingUuid} # 남은 조각 수 조회
```

#### 찜하기
```http
GET    /api/v1/funding/wish             # 찜한 펀딩 목록
GET    /api/v1/funding/wish/{fundingUuid} # 찜 여부 조회
POST   /api/v1/funding/wish             # 찜하기/취소
```

## 🧪 테스트

### 1. 단위 테스트 실행
```bash
./gradlew test
```

### 2. 동시성 테스트
```bash
./gradlew test --tests "*ConcurrencyTest"
```

### 3. 특정 테스트 실행
```bash
./gradlew test --tests "FundingServiceImplTest"
```

## 📊 부하 테스트

### JMeter를 사용한 부하 테스트
```bash
# JMeter 설치 (macOS)
brew install jmeter

# 부하 테스트 실행
jmeter -n -t load-test/load-test-plan.jmx -l results.jtl -e -o report/
```

### 부하 테스트 시나리오
- **동시 사용자**: 50명
- **테스트 시간**: 10분
- **요청 간격**: 1초
- **테스트 시나리오**:
  1. 펀딩 목록 조회
  2. 펀딩 참여
  3. 남은 조각 수 조회

## 📈 모니터링

### 1. 로그 모니터링
```bash
# 애플리케이션 로그
tail -f logs/funding-service.log

# 에러 로그
tail -f logs/funding-service-error.log

# 성능 로그
tail -f logs/funding-service-performance.log
```

### 2. 데이터베이스 모니터링
```sql
-- 활성 펀딩 수 조회
SELECT COUNT(*) FROM funding WHERE funding_status = 'FUNDING';

-- 참여자 수 조회
SELECT COUNT(DISTINCT member_uuid) FROM funding_participation 
WHERE participate_status = 'JOIN';

-- Outbox 이벤트 상태 조회
SELECT status, COUNT(*) FROM outbox_events GROUP BY status;
```

### 3. Redis 모니터링
```bash
# Redis 연결 확인
redis-cli -h 172.31.2.85 -p 6379 -a 00781234 ping

# 펀딩 재고 조회
redis-cli -h 172.31.2.85 -p 6379 -a 00781234 GET "funding:{fundingUuid}:remain"
```

## 🔧 개선사항

### PayPal 슈퍼바이저 기준 Critical 개선사항

#### 1. ✅ 구조화된 로깅 구현
- Logback 설정으로 로그 레벨별 분리
- 파일 롤링 및 아카이브 정책 적용
- 성능 로그 별도 수집

#### 2. ✅ 동시성 제어 강화
- Optimistic Lock (@Version) 적용
- Redis Lua 스크립트 기반 원자적 연산
- 동시성 테스트 코드 추가

#### 3. ✅ 분산 트랜잭션 패턴 도입
- Outbox 패턴으로 이벤트 저장
- 트랜잭션 내에서 이벤트 보장
- 재시도 메커니즘 구현

#### 4. ✅ 예외 처리 개선
- System.out.println 제거
- 구조화된 에러 로깅
- 적절한 예외 전파

### 향후 개선 계획

#### 1. 🔄 보안 강화
- Spring Security 도입
- JWT 토큰 검증
- API 인증/인가

#### 2. 🔄 모니터링 강화
- Micrometer 메트릭 수집
- Prometheus 연동
- Grafana 대시보드

#### 3. 🔄 성능 최적화
- 캐싱 전략 구현
- 데이터베이스 인덱스 최적화
- 쿼리 성능 개선

#### 4. 🔄 장애 복구
- Circuit Breaker 패턴
- Fallback 메커니즘
- 자동 복구 시스템

## 🤝 기여하기

### 개발 환경 설정
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### 코딩 컨벤션
- Java 코드는 Google Java Style Guide 준수
- 커밋 메시지는 Conventional Commits 형식 사용
- 모든 새로운 기능은 테스트 코드 포함

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 배포됩니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

## 👥 팀

- **팀명**: Team 0078
- **프로젝트**: Funding Service
- **기술 스택**: Spring Boot, Java 17, MySQL, Redis, Kafka

## 📞 문의

프로젝트에 대한 문의사항이 있으시면 이슈를 생성해 주세요.

---

**🏦 PayPal 슈퍼바이저 기준으로 설계된 고성능 펀딩 서비스입니다!** 
# RE:BORN Backend

### 개요

RE:BORN 백엔드는 중고거래 플랫폼의 **API 서버**로, 회원/인증, 상품 관리, 시세 분석, AI 기능, 이미지 도용 판별, 마이페이지 등 핵심 비즈니스 로직을 담당합니다.  
Spring Boot 기반으로 설계되어 프론트엔드(React)와 REST API로 통신하며, 안전하고 신뢰도 높은 거래 환경을 제공하는 것이 목표입니다.

### 주요 기능

- **인증/인가**
  - 회원가입, 로그인, 로그아웃
  - JWT 기반 토큰 발급 및 검증
  - 이메일 인증(인증 코드 발송 및 검증)

- **상품/거래 관리**
  - 상품 등록/수정/삭제, 상세 조회, 목록 조회
  - 카테고리, 가격대, 지역, 판매 상태 등 복합 조건 검색
  - 찜(리본) 기능, 최근 본 상품, 최근 검색어 관리

- **시세 분석**
  - 결제 평가 및 유저 신뢰도를 반영한 **가중평균 시세 계산**
  - 상위 5% / 하위 10% 가격 절사 후 시세 산출
  - 거래 건수, 최저/최고가 등 통계 정보 제공

- **AI/이미지 연동**
  - Google Vision API로 **이미지 유사도/도용 판별**
  - OpenAI API로 **상품 설명·카테고리·지역 제안** (물건 등록 어시스턴트)
  - 찜한 상품 간 **AI 비교/분석/추천 코멘트** 생성

- **마이페이지 및 관리**
  - 판매 내역, 구매 내역, 찜 목록, 최근 본 상품 조회
  - 사용자 점수/경고 내역 기반 신뢰도 표시
  - 관리자용 신고(경고) 관리

### 기술 스택

- **언어**: Java 21  
- **프레임워크**: Spring Boot, Spring Web, Spring Security  
- **데이터 접근**: Spring Data JPA, Querydsl  
- **인증/보안**: JWT, Spring Security  
- **캐시**: Caffeine Cache  
- **DB**: MySQL  
- **외부 연동**: OpenAI API, Google Cloud Vision API, Spring Boot Mail  

### 프로젝트 구조 (요약)

- `src/main/java/com/example/kmjoonggo`
  - `controller` – REST API 엔드포인트
  - `service` – 비즈니스 로직
  - `repository` – JPA/Querydsl 데이터 접근
  - `security` – JWT, Security 설정
  - `dto` – 요청/응답 DTO
  - `entity` – JPA 엔티티
- `src/main/resources`
  - `application.yml` – 환경 설정
  - 기타 설정/정적 리소스

### 로컬 실행 방법

1. **환경 변수 / 설정**
   - `application.yml` 또는 환경 변수로 아래 값들을 설정
     - DB 접속 정보 (MySQL)
     - OpenAI API 키
     - Google Cloud Vision 자격 증명
     - 메일 서버 설정 등

2. **Gradle 빌드 & 실행**

```bash
./gradlew bootRun
# 또는
./gradlew build
java -jar build/libs/<생성된-jar-파일>.jar
```

### 프론트엔드 연동

백엔드는 React 기반 프론트엔드(REBORN-Frontend)와 REST API로 연동됩니다.  
API 베이스 URL, CORS 설정 등은 운영 환경에 맞게 `application.yml`에서 조정할 수 있습니다.


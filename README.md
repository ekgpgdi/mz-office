# 신입 사원을 위한 AI 서비스 “MZ오피스”

![MZ OFFICE](https://github.com/user-attachments/assets/82e35397-c15d-4b00-8839-bb033e327ea1)

**[🔗 서비스 소개 바로가기](https://dahye-backend-developer.my.canva.site/mz-office)** <br/>
**[🔗 서비스 바로가기](https://newbie.mz-office.site)**

<br/>

## 기획 배경
최근 신입 사원이 직장 내에서 문장력과 문해력이 부족하여 메일 및 문자 작성을 어려워한다는 통계 자료를 바탕으로 Pain Point를 도출하였고, 이러한 불편함을 해소하기 위해 서비스를 기획하였습니다.

### 주요 타겟
20~30대 신입 사원

### 핵심 기능
1. AI 비즈니스 문자 작성
2. AI 비즈니스 메일 작성 
3. AI 문구 해석
4. 비즈니스 용어 단어장 : 단어 카드 & 퀴즈

<br/>

## 기술 스택  
📌 **Backend**
- Java 17+
- Spring Boot 3.4.x
- Spring Security
- Spring AOP
- WebSocket

📌 **Database**
- MySQL
- QueryDSL 5.x
- JPA
- MongoDB

📌 **Others**
- Swagger
- JWT

## 백엔드 담당 역할

### 🔹 Spring 기반 아키텍처 설계
- Spring Boot를 활용하여 백엔드 시스템을 구축하고, 전체 아키텍처 설계 및 API 설계를 담당하였습니다.
- 사용자 요청에 따른 비즈니스 로직을 처리하고, 클라이언트와 서버 간 데이터 전송을 관리하였습니다.

### 🔹 소켓 기반 채팅 구현
- WebSocket을 활용하여 실시간 채팅 기능을 구현하였습니다.
- 클라이언트와 서버 간의 양방향 통신을 통해 사용자가 요청한 문구에 대해 즉시 응답을 제공하였습니다.

### 🔹 MongoDB를 활용한 채팅 데이터 관리
- MongoDB를 사용하여 채팅 데이터를 효율적으로 저장 및 관리하였습니다.
- 대화 기록을 저장하고 분석할 수 있는 구조를 설계하였습니다.

### 🔹 AI와의 연동 (Naver Clova Studio)
- Naver Clova Studio를 활용하여 사용자의 요청을 분석하고, 문구 해석 및 답변을 생성하는 AI 모델을 연동하였습니다.
- AI 응답을 고도화하기 위해 추가적인 AI 모델을 활용하여 응답의 품질을 개선하였습니다.
<br/>

<img width="50%" src="https://github.com/user-attachments/assets/9e8f6851-3948-4e0b-ba7c-877cdedc9223"/>
 
### 🔹 배포 및 운영
- NCP (Naver Cloud Platform)을 활용하여 배포 환경을 설정하고 서비스 운영을 담당하였습니다.

<details>
  <summary>프로젝트 구조</summary>

```
├── MzOfficeApplication.java
├── application
│   ├── controller
│   │   ├── AuthController.java
│   │   ├── ChatController.java
│   │   ├── HealthCheckController.java
│   │   └── VocabularyListController.java
│   └── usecase
│       └── UserChatUsecase.java
├── chat
│   ├── domain
│   │   ├── ChatMessage.java
│   │   ├── ChatSession.java
│   │   └── Message.java
│   ├── dto
│   │   ├── request
│   │   └── response
│   ├── enums
│   │   ├── InputMethodType.java
│   │   ├── InquiryType.java
│   │   ├── MailSentenceGenerationType.java
│   │   ├── MessageSenderType.java
│   │   ├── MessageSentenceGenerationType.java
│   │   ├── MessageType.java
│   │   └── RequestType.java
│   ├── handler
│   │   └── ChatWebSocketHandler.java
│   ├── interceptor
│   │   └── WebSocketAuthInterceptor.java
│   ├── repository
│   │   └── ChatMessageRepository.java
│   └── service
│       ├── ChatReadService.java
│       └── ChatWriteService.java
├── clovaAi
│   ├── dto
│   │   ├── request
│   │   └── response
│   ├── enums
│   │   ├── ClovaPrompt.java
│   │   ├── InputTypeClovaPrompt.java
│   │   ├── RequestTypeClovaPrompt.java
│   │   └── SentenceGenerationTypeClovaPrompt.java
│   └── service
│       └── AiService.java
├── common
│   ├── config
│   │   ├── CustomAccessDeniedHandler.java
│   │   ├── CustomAuthenticationEntryPoint.java
│   │   ├── QuerydslConfig.java
│   │   ├── RestTemplateConfig.java
│   │   ├── SecurityConfig.java
│   │   └── WebSocketConfig.java
│   ├── domain
│   │   └── BaseTimeEntity.java
│   ├── enums
│   │   └── ResponseCode.java
│   ├── exception
│   │   ├── GlobalExceptionHandler.java
│   │   └── customException
│   ├── filter
│   │   └── JwtAuthenticationFilter.java
│   ├── response
│   │   ├── AbstractPageResponse.java
│   │   └── ServerResponse.java
│   └── service
│       └── JwtService.java
├── user
│   ├── dto
│   │   └── response
│   ├── enums
│   │   ├── SnsType.java
│   │   └── User.java
│   ├── naver
│   │   ├── NaverProperties.java
│   │   ├── NaverTokenResponse.java
│   │   ├── NaverUnlinkResponse.java
│   │   └── NaverUserResponse.java
│   ├── repository
│   │   └── UserRepository.java
│   └── service
│       ├── AuthService.java
│       ├── NaverAuthService.java
│       └── UserService.java
└── vocabularyList
    ├── domain
    │   ├── UserVocabularyLog.java
    │   └── VocabularyList.java
    ├── dto
    │   └── response
    ├── enums
    │   └── VocabularyLogType.java
    ├── repository
    │   ├── UserVocabularyListRepository.java
    │   ├── VocabularyListRepository.java
    │   ├── VocabularyListRepositoryCustom.java
    │   └── VocabularyListRepositoryImpl.java
    └── service
        └── VocabularyListService.java
```
</details>

## 아키텍처
![MZOFFICE drawio](https://github.com/user-attachments/assets/d73de1b1-4ee4-4eb4-9274-76a2160a1665)

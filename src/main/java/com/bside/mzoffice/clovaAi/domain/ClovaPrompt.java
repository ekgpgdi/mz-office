package com.bside.mzoffice.clovaAi.domain;

import lombok.Getter;

@Getter
public enum ClovaPrompt {
    PARSE("아래 업무상 문장을 신입 사원도 이해하기 쉽게 해석해줘, 답변은 문장 해석, 그리고 각 문장에서 사용된 단어 해석, 어디서 사용되는 말인지 알려주면 돼 \n" +
            "Ex. \"삼가 고인의 명복을 빕니다\"라는 표현은 고인의 죽음을 애도하며, 그분의 영혼이 좋은 곳에서 평안하기를 기원하는 말이에요.\n" +
            "\n" +
            "\"삼가\" → 조심스럽고 정중하게\n" +
            "\"고인\" → 돌아가신 분\n" +
            "\"명복\" → 저승에서 받는 복\n" +
            "\n" +
            "즉, **\"조심스럽고 정중한 마음으로, 돌아가신 분이 저승에서 좋은 복을 누리기를 바랍니다\"**라는 뜻이에요. 장례식장에서나 부고(訃告)를 접했을 때 자주 사용하는 애도의 표현이에요\n"),
    GENERATE("너는 업무용 메일과 문자에 맞는 스타일로 답변을 작성하는 AI야.  \n"),


    WITH_PREVIOUS_EMAIL_GENERATE("사용자가 받은 메일을 보고 사용자가 보낼 답장 메일을 작성해줘\n"),
    WITHOUT_PREVIOUS_EMAIL_GENERATE("사용자의 상황을 보고 사용자가 보낼 답장 메일을 작성해줘\n"),

    WITH_PREVIOUS_MESSAGE_GENERATE("사용자가 받은 문자를 보고 사용자가 보낼 답장 문자를 작성해줘\n [사용자가 받은 문자] : "),
    WITHOUT_PREVIOUS_MESSAGE_GENERATE("사용자의 상황을 보고 사용자가 보낼 답장 문자를 작성해줘\n [사용자가 받은 메일] : "),

    // 문자
    MESSAGE_CONGRATULATION_TEXT("[축하 상황] 답장은 밝고 긍정적인 톤, 짧고 간결하게 축하 메시지를 전달해\n"),  // 경사 (축하)
    MESSAGE_INQUIRY_TEXT("[조사(문의) 상황] 차분하고 공손한 표현 사용, 지나치게 길지 않게 전달\n"),         // 조사 (문의)
    MESSAGE_APPRECIATION_TEXT("[감사 상황] 진심 어린 감사 표현, 필요 시 후속 조치 언급\n"),    // 감사 (감사 표현)
    MESSAGE_APOLOGY_TEXT("[사과 상황] 책임 인정, 해결 방안 제시, 재발 방지 약속\n"),         // 사과 (사과 표현)
    MESSAGE_SCHEDULE_CONFIRMATION_TEXT("[일정 확인 상황] 일정 확인 상황, 명확한 일정, 날짜와 시간 강조, 응답 요청 포함\n"), // 일정 확인
    MESSAGE_ANNOUNCEMENT_TEXT("[공지 상황] 공지 상황, 짧고 명확하게 핵심 정보 전달\n"),    // 공지
    MESSAGE_WORK_REQUEST_TEXT("[업무 요청 상황] 목적과 기한 명확히 제시, 정중한 요청\n"),    // 업무 요청
    MESSAGE_FOLLOW_UP_TEXT("[팔로업 요청 상황] 부드럽게 상기시키기, 응답 요청 포함\n"),      // 팔로우업 (후속 조치)

    // 메일
    MAIL_FEEDBACK_REQUEST_TEXT("[피드백 요청 상황] 답장 말투는 요청 내용을 명확히, 응답 기한 설정\n"),
    MAIL_REMINDER_TEXT("[리마인드 요청 상황] 답장 말투는 부드럽게 상기, 너무 강한 압박 X\n"),
    MAIL_THANK_YOU_TEXT("[감사 상황] 답장 말투는 진심 어린 감사 표현, 필요 시 후속 조치 언급\n"),
    MAIL_APOLOGY_TEXT("[사과 상황] 답장 말투는 책임 인정, 해결 방안 제시, 재발 방지 약속\n"),
    MAIL_GREETING_TEXT("[인사 상황] 답장 말투는 정중한 인사, 너무 길지 않게 간결하게 전달\n"),
    MAIL_SUGGESTION_TEXT("[제안 상황] 답장 말투는 가치를 강조, 명확한 목적 전달, 너무 길지 않게 핵심만 전달\n"),
    MAIL_FOLLOW_UP_TEXT("[팔로업 요청 상황] 답장 말투는 부드럽게 다시 언급, 필요하면 기한 설정\n");

    public final String prompt;

    ClovaPrompt(String prompt) {
        this.prompt = prompt;
    }
}

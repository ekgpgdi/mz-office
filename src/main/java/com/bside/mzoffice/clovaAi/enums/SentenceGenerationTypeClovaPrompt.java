package com.bside.mzoffice.clovaAi.enums;

import com.bside.mzoffice.chat.enums.MailSentenceGenerationType;
import com.bside.mzoffice.chat.enums.MessageSentenceGenerationType;
import com.bside.mzoffice.chat.enums.MessageType;
import com.bside.mzoffice.chat.enums.RequestType;

public enum SentenceGenerationTypeClovaPrompt {
    MESSAGE_CONGRATULATION_TEXT("[축하 상황] 답장은 밝고 긍정적인 톤, 짧고 간결하게 축하 메시지를 전달해\n"),  // 경사 (축하)
    MESSAGE_INQUIRY_TEXT("[조사(문의) 상황] 차분하고 공손한 표현 사용, 지나치게 길지 않게 전달\n"),         // 조사 (문의)
    MESSAGE_APPRECIATION_TEXT("[감사 상황] 진심 어린 감사 표현, 필요 시 후속 조치 언급\n"),    // 감사 (감사 표현)
    MESSAGE_APOLOGY_TEXT("[사과 상황] 책임 인정, 해결 방안 제시, 재발 방지 약속\n"),         // 사과 (사과 표현)
    MESSAGE_SCHEDULE_CONFIRMATION_TEXT("[일정 확인 상황] 일정 확인 상황, 명확한 일정, 날짜와 시간 강조, 응답 요청 포함\n"), // 일정 확인
    MESSAGE_ANNOUNCEMENT_TEXT("[공지 상황] 공지 상황, 짧고 명확하게 핵심 정보 전달\n"),    // 공지
    MESSAGE_WORK_REQUEST_TEXT("[업무 요청 상황] 목적과 기한 명확히 제시, 정중한 요청\n"),    // 업무 요청
    MESSAGE_FOLLOW_UP_TEXT("[팔로업 요청 상황] 부드럽게 상기시키기, 응답 요청 포함\n"),      // 팔로우업 (후속 조치)

    MAIL_FEEDBACK_REQUEST_TEXT("[피드백 요청 상황] 답장 말투는 요청 내용을 명확히, 응답 기한 설정\n"),
    MAIL_REMINDER_TEXT("[리마인드 요청 상황] 답장 말투는 부드럽게 상기, 너무 강한 압박 X\n"),
    MAIL_THANK_YOU_TEXT("[감사 상황] 답장 말투는 진심 어린 감사 표현, 필요 시 후속 조치 언급\n"),
    MAIL_APOLOGY_TEXT("[사과 상황] 답장 말투는 책임 인정, 해결 방안 제시, 재발 방지 약속\n"),
    MAIL_GREETING_TEXT("[인사 상황] 답장 말투는 정중한 인사, 너무 길지 않게 간결하게 전달\n"),
    MAIL_SUGGESTION_TEXT("[제안 상황] 답장 말투는 가치를 강조, 명확한 목적 전달, 너무 길지 않게 핵심만 전달\n"),
    MAIL_FOLLOW_UP_TEXT("[팔로업 요청 상황] 답장 말투는 부드럽게 다시 언급, 필요하면 기한 설정\n");

    public final String prompt;

    SentenceGenerationTypeClovaPrompt(String prompt) {
        this.prompt = prompt;
    }

    public static String handleSentenceGenerationType(MessageSentenceGenerationType type) {
        StringBuilder aiPrompt = new StringBuilder();
        switch (type) {
            case CONGRATULATION -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_CONGRATULATION_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.CONGRATULATION.example);
            }
            case INQUIRY -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_INQUIRY_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.INQUIRY.example);
            }
            case APPRECIATION -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_APPRECIATION_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.APPRECIATION.example);
            }
            case APOLOGY -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_APOLOGY_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.APOLOGY.example);
            }
            case SCHEDULE_CONFIRMATION -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_SCHEDULE_CONFIRMATION_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.SCHEDULE_CONFIRMATION.example);
            }
            case ANNOUNCEMENT -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_ANNOUNCEMENT_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.ANNOUNCEMENT.example);
            }
            case WORK_REQUEST -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_WORK_REQUEST_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.WORK_REQUEST.example);
            }
            case FOLLOW_UP -> {
                aiPrompt.append(ClovaPrompt.MESSAGE_FOLLOW_UP_TEXT.prompt);
                aiPrompt.append(MessageSentenceGenerationType.FOLLOW_UP.example);
            }
            default -> {
            }
        }
        return aiPrompt.toString();
    }

    public static String handleSentenceGenerationType(MailSentenceGenerationType type) {
        StringBuilder aiPrompt = new StringBuilder();
        switch (type) {
            case FEEDBACK_REQUEST -> {
                aiPrompt.append(ClovaPrompt.MAIL_FEEDBACK_REQUEST_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.FEEDBACK_REQUEST.example);
            }
            case REMINDER -> {
                aiPrompt.append(ClovaPrompt.MAIL_REMINDER_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.REMINDER.example);
            }
            case THANK_YOU -> {
                aiPrompt.append(ClovaPrompt.MAIL_THANK_YOU_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.THANK_YOU.example);
            }
            case APOLOGY -> {
                aiPrompt.append(ClovaPrompt.MAIL_APOLOGY_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.APOLOGY.example);
            }
            case GREETING -> {
                aiPrompt.append(ClovaPrompt.MAIL_GREETING_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.GREETING.example);
            }
            case SUGGESTION -> {
                aiPrompt.append(ClovaPrompt.MAIL_SUGGESTION_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.SUGGESTION.example);
            }
            case FOLLOW_UP -> {
                aiPrompt.append(ClovaPrompt.MAIL_FOLLOW_UP_TEXT.prompt);
                aiPrompt.append(MailSentenceGenerationType.FOLLOW_UP.example);
            }
            default -> {
            }
        }
        return aiPrompt.toString();
    }
}

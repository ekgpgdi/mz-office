package com.bside.mzoffice.clovaAi.enums;

import com.bside.mzoffice.chat.enums.RequestType;

public enum RequestTypeClovaPrompt {
    PARSE("아래 업무상 문장을 신입 사원도 이해하기 쉽게 해석해줘, 답변은 문장 해석, 그리고 각 문장에서 사용된 단어 해석, 어디서 사용되는 말인지 알려주면 돼 \n" +
            "Ex. \"삼가 고인의 명복을 빕니다\"라는 표현은 고인의 죽음을 애도하며, 그분의 영혼이 좋은 곳에서 평안하기를 기원하는 말이에요.\n" +
            "\n" +
            "\"삼가\" → 조심스럽고 정중하게\n" +
            "\"고인\" → 돌아가신 분\n" +
            "\"명복\" → 저승에서 받는 복\n" +
            "\n" +
            "즉, **\"조심스럽고 정중한 마음으로, 돌아가신 분이 저승에서 좋은 복을 누리기를 바랍니다\"**라는 뜻이에요. 장례식장에서나 부고(訃告)를 접했을 때 자주 사용하는 애도의 표현이에요\n"),
    GENERATE("너는 업무용 메일과 문자에 맞는 스타일로 답변을 작성하는 AI야.  \n");

    public final String prompt;

    RequestTypeClovaPrompt(String prompt) {
        this.prompt = prompt;
    }

    public static String handleRequestType(String content) {
        RequestType requestType = RequestType.valueOf(content);

        return switch (requestType) {
            case PARSE -> PARSE.prompt;
            case GENERATE -> GENERATE.prompt;
        };
    }
}

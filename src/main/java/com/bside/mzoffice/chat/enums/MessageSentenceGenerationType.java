package com.bside.mzoffice.chat.enums;

public enum MessageSentenceGenerationType {
    CONGRATULATION("예시: 귀사의 창립 10주년을 진심으로 축하드립니다. 앞으로도 더욱 번창하시길 기원합니다.\n"),  // 경사 (축하)
    INQUIRY("예시:삼가 고인의 명복을 빕니다. 깊은 애도의 뜻을 전하며, 위로의 말씀을 드립니다.\n"),         // 조사 (문의)
    APPRECIATION("예시:항상 저희 제품을 신뢰해 주셔서 감사합니다. 앞으로도 최선을 다하겠습니다.\n"),    // 감사 (감사 표현)
    APOLOGY("예시:금일 시스템 점검으로 인해 이용에 불편을 드린 점 사과드립니다. 빠른 복구를 위해 최선을 다하겠습니다.\n"),         // 사과 (사과 표현)
    SCHEDULE_CONFIRMATION("예시:오는 3월 5일(화) 오전 10시 미팅 일정이 확인되었습니다. 변동 사항이 있으면 알려주시기 바랍니다.\n"), // 일정 확인
    ANNOUNCEMENT("예시:오는 3월 10일(일) 오전 2시~6시 서버 점검이 진행될 예정입니다. 이용에 참고 부탁드립니다.\n"),    // 공지
    WORK_REQUEST("예시:지난번 논의한 계약서 초안을 2월 25일까지 공유 부탁드립니다\n"),    // 업무 요청
    FOLLOW_UP("예시:지난주 전달드린 제안서 관련하여 검토가 완료되었을까요? 회신 부탁드립니다.\n");       // 팔로우업 (후속 조치)

    public final String example;

     MessageSentenceGenerationType(String example) {
        this.example = example;
    }
}

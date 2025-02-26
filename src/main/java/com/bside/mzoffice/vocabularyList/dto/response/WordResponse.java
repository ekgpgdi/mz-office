package com.bside.mzoffice.vocabularyList.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WordResponse {
    @Schema(description = "단어")
    private String word;

    @Schema(description = "형태 (명사 / 형용사 / 부사)")
    private String form;

    @Schema(description = "단어 뜻")
    private String explanation;

    @Schema(description = "단어 사용 에시")
    private String exampleSentence;
}

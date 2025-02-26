package com.bside.mzoffice.vocabularyList.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WordResponse {
    private String word;
    private String form;
    private String explanation;
    private String exampleSentence;
}

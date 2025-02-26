package com.bside.mzoffice.vocabularyList.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class QuizResponse extends WordResponse {
    private List<String> wrongWordList;
}

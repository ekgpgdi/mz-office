package com.bside.mzoffice.vocabularyList.service;

import com.bside.mzoffice.common.domain.ResponseCode;
import com.bside.mzoffice.common.exception.customException.NotFoundException;
import com.bside.mzoffice.common.response.ServerResponse;
import com.bside.mzoffice.vocabularyList.domain.VocabularyList;
import com.bside.mzoffice.vocabularyList.domain.VocabularyLogType;
import com.bside.mzoffice.vocabularyList.dto.response.QuizResponse;
import com.bside.mzoffice.vocabularyList.dto.response.WordResponse;
import com.bside.mzoffice.vocabularyList.repository.VocabularyListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VocabularyListService {
    private final VocabularyListRepository vocabularyListRepository;

    @Transactional(readOnly = true)
    public WordResponse getRandomWord(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        List<VocabularyList> vocabularyListList = vocabularyListRepository.findByNotExistTodayUserVocabularyLog(userId, LocalDate.now(), VocabularyLogType.WORD, PageRequest.of(0, 1));
        if (vocabularyListList.size() == 0) {
            throw new NotFoundException(ResponseCode.NOT_FOUND_WORD);
        }
        VocabularyList vocabularyList = vocabularyListList.get(0);
        return WordResponse.builder()
                .word(vocabularyList.getWord())
                .exampleSentence(vocabularyList.getExampleSentence())
                .explanation(vocabularyList.getExplanation())
                .form(vocabularyList.getForm())
                .build();
    }

    @Transactional(readOnly = true)
    public QuizResponse getRandomQuiz(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());

        List<VocabularyList> vocabularyListList = vocabularyListRepository.findByNotExistTodayUserVocabularyLog(userId, LocalDate.now(), VocabularyLogType.QUIZ, PageRequest.of(0, 1));
        if (vocabularyListList.size() == 0) {
            throw new NotFoundException(ResponseCode.NOT_FOUND_WORD);
        }

        VocabularyList vocabularyList = vocabularyListList.get(0);
        List<String> wordList = vocabularyListRepository.findWordByNotEqWord(vocabularyList.getWord(), PageRequest.of(0, 4));
        return QuizResponse.builder()
                .word(vocabularyList.getWord())
                .exampleSentence(vocabularyList.getExampleSentence())
                .explanation(vocabularyList.getExplanation())
                .form(vocabularyList.getForm())
                .wrongWordList(wordList)
                .build();
    }
}

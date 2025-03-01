package com.bside.mzoffice.vocabularyList.repository;

import com.bside.mzoffice.vocabularyList.domain.VocabularyList;
import com.bside.mzoffice.vocabularyList.domain.VocabularyLogType;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface VocabularyListRepositoryCustom {
    List<VocabularyList> findByNotExistTodayUserVocabularyLog( LocalDate now, VocabularyLogType type, Pageable pageable);

    List<String> findWordByNotEqWord(String word, Pageable pageable);
}

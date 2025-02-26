package com.bside.mzoffice.vocabularyList.repository;

import com.bside.mzoffice.vocabularyList.domain.VocabularyList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long>, VocabularyListRepositoryCustom {
}

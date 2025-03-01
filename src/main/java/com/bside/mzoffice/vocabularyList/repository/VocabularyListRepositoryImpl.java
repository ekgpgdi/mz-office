package com.bside.mzoffice.vocabularyList.repository;

import com.bside.mzoffice.vocabularyList.domain.QUserVocabularyLog;
import com.bside.mzoffice.vocabularyList.domain.QVocabularyList;
import com.bside.mzoffice.vocabularyList.domain.VocabularyList;
import com.bside.mzoffice.vocabularyList.domain.VocabularyLogType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.bside.mzoffice.vocabularyList.domain.QVocabularyList.vocabularyList;

@Repository
public class VocabularyListRepositoryImpl implements VocabularyListRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Autowired
    public VocabularyListRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory; // 생성자를 통해 queryFactory 주입받기
    }

    @Override
    public List<VocabularyList> findByNotExistTodayUserVocabularyLog(LocalDate now, VocabularyLogType type, Pageable pageable) {
        QVocabularyList vocabularyList = QVocabularyList.vocabularyList;
//        QUserVocabularyLog userVocabularyLog = QUserVocabularyLog.userVocabularyLog;

//        BooleanExpression existsSubQuery = JPAExpressions
//                .selectOne()
//                .from(userVocabularyLog)
//                .where(
//                        userVocabularyLog.userId.eq(userId)
//                                .and(userVocabularyLog.date.eq(now))
//                                .and(userVocabularyLog.type.eq(type))
//                )
//                .exists();

        return queryFactory
                .selectFrom(vocabularyList)
                .orderBy(
                        Expressions.numberTemplate(Double.class, "RAND()").asc()
                )
                .offset(0)
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<String> findWordByNotEqWord(String word, Pageable pageable) {
        return queryFactory.select(vocabularyList.word)
                .from(vocabularyList)
                .where(vocabularyList.word.notEqualsIgnoreCase(word))
                .orderBy(Expressions.numberTemplate(Double.class, "RAND()").asc())
                .offset(0)
                .limit(pageable.getPageSize())
                .fetch();
    }
}

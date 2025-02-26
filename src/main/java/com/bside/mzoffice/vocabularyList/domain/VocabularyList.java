package com.bside.mzoffice.vocabularyList.domain;

import com.bside.mzoffice.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

/**
 * -- auto-generated definition
 * create table vocabulary_list
 * (
 * id                 bigint auto_increment
 * primary key,
 * word               varchar(50)  null,
 * form               varchar(50)  null,
 * explanation        varchar(512) null,
 * example_sentence varchar(512) null,
 * created_at         datetime     null,
 * updated_at         datetime     null,
 * constraint vocabulary_list_word_uindex
 * unique (word)
 * );
 */
@Entity
@Table(name = "vocabulary_list")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VocabularyList extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, unique = true)
    private String word;

    @Column(length = 50)
    private String form;

    @Column(length = 512)
    private String explanation;

    @Column(length = 512)
    private String exampleSentence;
}

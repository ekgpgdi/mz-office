package com.bside.mzoffice.vocabularyList.domain;

import com.bside.mzoffice.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * -- auto-generated definition
 * create table user_vocabulary_log
 * (
 * id            bigint auto_increment
 * primary key,
 * date          date     null,
 * user_id       bigint   not null,
 * vocabulary_id bigint   not null,
 * created_at    datetime null,
 * updated_at    datetime null
 * );
 */
@Entity
@Table(name = "user_vocabulary_log")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserVocabularyLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private LocalDate date;

    @Column
    private Long userId;

    @Column
    private Long vocabularyId;

    @Enumerated(EnumType.STRING)
    @Column
    private VocabularyLogType type;
}

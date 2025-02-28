package com.bside.mzoffice.chat.repository;

import com.bside.mzoffice.chat.domain.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    @Query("SELECT c FROM ChatMessage c WHERE c.userId = :userId AND c.date < CURRENT_DATE ORDER BY c.date DESC")
    List<ChatMessage> findTop3ByUserIdAndDateBeforeTodayOrderByDateDesc(@Param("userId") Long userId, Pageable pageable);

    List<ChatMessage> findByUserIdAndDate(Long userId, LocalDate now);
}

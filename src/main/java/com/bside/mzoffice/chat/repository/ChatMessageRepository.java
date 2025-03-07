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

    @Query("{ 'userId' : ?0, 'date' : { $lt: ?1 } }")
    List<ChatMessage> findTop3ByUserIdAndDateBeforeTodayOrderByDateDesc(@Param("userId") Long userId, LocalDate today, Pageable pageable);

    List<ChatMessage> findByUserIdAndDate(Long userId, LocalDate now);
}

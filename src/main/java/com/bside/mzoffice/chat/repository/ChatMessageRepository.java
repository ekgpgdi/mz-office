package com.bside.mzoffice.chat.repository;

import com.bside.mzoffice.chat.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findTop3ByUserIdOrderByDateDesc(Long userId);

    Optional<ChatMessage> findByUserIdAndDate(Long userId, LocalDate now);
}

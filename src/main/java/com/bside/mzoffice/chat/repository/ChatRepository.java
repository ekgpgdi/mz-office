package com.bside.mzoffice.chat.repository;

import com.bside.mzoffice.chat.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRepository extends MongoRepository<ChatMessage, String> {
}

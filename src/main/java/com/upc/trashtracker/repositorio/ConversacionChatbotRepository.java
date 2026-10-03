package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.ConversacionChatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConversacionChatbotRepository extends JpaRepository<ConversacionChatbot, Long> {
}

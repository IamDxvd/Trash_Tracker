package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.MensajeChatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeChatbotRepository extends JpaRepository<MensajeChatbot, Long> {
}

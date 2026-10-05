package com.upc.trashtracker.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje_chatbot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChatbot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_mensaje_chatbot")
    private Long idMensajeChatbot;

    @Column(name = "emisor")
    private String emisor;

    @Column(name = "contenido")
    private String contenido;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversacion_id")
    @JsonBackReference
    private ConversacionChatbot conversacion;
}

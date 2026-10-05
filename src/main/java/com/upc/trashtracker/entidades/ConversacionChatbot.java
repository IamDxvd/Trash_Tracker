package com.upc.trashtracker.entidades;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conversacion_chatbot")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversacionChatbot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conversacion_chatbot")
    private Long idConversacionChatbot;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", unique = true)
    @JsonBackReference
    private Usuario usuario;

    @OneToMany(mappedBy = "conversacion", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<MensajeChatbot> mensajes = new ArrayList<>();
}

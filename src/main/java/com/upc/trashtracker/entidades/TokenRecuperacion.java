package com.upc.trashtracker.entidades;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
@Entity @Data @Table(name = "token_recuperacion")
public class TokenRecuperacion {
    @Id @Column(length = 64) private String hash;
    @Column(name = "usuario_id", nullable = false) private Long usuarioId;
    @Column(nullable = false) private Instant expira;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Usuario usuario;
}

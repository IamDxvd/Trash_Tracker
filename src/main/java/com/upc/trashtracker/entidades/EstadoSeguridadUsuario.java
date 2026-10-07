package com.upc.trashtracker.entidades;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
@Entity @Data @Table(name = "estado_seguridad_usuario")
public class EstadoSeguridadUsuario {
    @Id @Column(name = "usuario_id") private Long usuarioId;
    @Column(nullable = false) private Instant tokensInvalidosHasta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Usuario usuario;
}

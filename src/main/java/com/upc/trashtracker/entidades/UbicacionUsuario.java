package com.upc.trashtracker.entidades;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ubicacion_usuario")
@Data
public class UbicacionUsuario {
    @Id @Column(name = "id_usuario") private Long idUsuario;
    @Column(nullable = false) private Double latitud;
    @Column(nullable = false) private Double longitud;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", insertable = false, updatable = false)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Usuario usuario;
}

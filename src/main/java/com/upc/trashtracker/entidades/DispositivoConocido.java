package com.upc.trashtracker.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dispositivo_conocido")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DispositivoConocido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dispositivo_conocido")
    private Long idDispositivoConocido;

    @Column(name = "tipo_dispositivo")
    private String tipoDispositivo;

    @Column(name = "fecha_primer_acceso")
    private LocalDate fechaPrimerAcceso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;

}

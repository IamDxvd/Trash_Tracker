package com.upc.trashtracker.entidades;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "cuenta_vinculada")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChatbot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta_vinculada")
    private Long idCuentaVinculada;

    @Column(name = "proveedor")
    private String proveedor;

    @Column(name = "id_externo")
    private String idExterno;

    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private Usuario usuario;
}

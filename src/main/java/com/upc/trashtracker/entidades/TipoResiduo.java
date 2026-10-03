package com.upc.trashtracker.entidades;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tipo_residuo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoResiduo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_residuo")
    private Long idTipoResiduo;

    @Column(name = "nombre")
    private String nombre;

    @OneToMany(mappedBy = "tipoResiduo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Reporte> reportes = new ArrayList<>();

}

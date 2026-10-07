package com.upc.trashtracker.repositorio;

import java.util.List;
import com.upc.trashtracker.entidades.Reporte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, Long> {

    List<Reporte> findByTipoResiduoNombre(String nombre);
    List<Reporte> findByDescripcionContainingIgnoreCase(String texto);
}

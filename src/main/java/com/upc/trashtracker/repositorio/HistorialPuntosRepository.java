package com.upc.trashtracker.repositorio;

import java.util.List;
import com.upc.trashtracker.entidades.HistorialPuntos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialPuntosRepository extends JpaRepository<HistorialPuntos, Long> {

    List<HistorialPuntos> findByUsuarioIdUsuario(Long idUsuario);
}

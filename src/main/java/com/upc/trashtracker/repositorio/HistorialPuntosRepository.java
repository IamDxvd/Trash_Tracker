package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.HistorialPuntos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialPuntosRepository extends JpaRepository<HistorialPuntos, Long> {
}

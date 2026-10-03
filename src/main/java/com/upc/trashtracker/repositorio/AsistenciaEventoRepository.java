package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.AsistenciaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaEventoRepository extends JpaRepository<AsistenciaEvento, Long> {
}

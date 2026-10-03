package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.PreguntaFrecuente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PreguntaFrecuenteRepository extends JpaRepository<PreguntaFrecuente, Long> {
}

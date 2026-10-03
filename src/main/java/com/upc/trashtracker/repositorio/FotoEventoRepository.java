package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.FotoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoEventoRepository extends JpaRepository<FotoEvento, Long> {
}

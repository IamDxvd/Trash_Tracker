package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.Logro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogroRepository extends JpaRepository<Logro, Long> {
}

package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.DispositivoConocido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispositivoConocidoRepository extends JpaRepository<DispositivoConocido, Long> {
}

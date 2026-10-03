package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.CuentaVinculada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaVinculadaRepository extends JpaRepository<CuentaVinculada, Long> {
}

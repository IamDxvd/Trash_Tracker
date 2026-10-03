package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.MiembroGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, Long> {
}

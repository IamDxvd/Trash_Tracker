package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Grupo e where e.idGrupo = :id")
    java.util.Optional<Grupo> buscarParaActualizar(@org.springframework.data.repository.query.Param("id") Long id);
}

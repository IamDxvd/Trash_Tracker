package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Recompensa e where e.idRecompensa = :id")
    java.util.Optional<Recompensa> buscarParaActualizar(@org.springframework.data.repository.query.Param("id") Long id);
}

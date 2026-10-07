package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Evento e where e.idEvento = :id")
    java.util.Optional<Evento> buscarParaActualizar(@org.springframework.data.repository.query.Param("id") Long id);
}

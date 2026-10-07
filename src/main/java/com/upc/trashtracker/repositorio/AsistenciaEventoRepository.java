package com.upc.trashtracker.repositorio;

import java.util.List;
import java.util.Optional;
import com.upc.trashtracker.entidades.AsistenciaEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsistenciaEventoRepository extends JpaRepository<AsistenciaEvento, Long> {

    Optional<AsistenciaEvento> findByEventoIdEventoAndUsuarioIdUsuario(Long idEvento, Long idUsuario);
    List<AsistenciaEvento> findByEventoIdEvento(Long idEvento);
}

package com.upc.trashtracker.repositorio;

import java.util.List;
import com.upc.trashtracker.entidades.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {

    List<Mensaje> findByGrupoIdGrupoOrderByFechaHoraAsc(Long idGrupo);
}

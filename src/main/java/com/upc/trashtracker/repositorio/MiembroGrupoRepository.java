package com.upc.trashtracker.repositorio;

import java.util.List;
import java.util.Optional;
import com.upc.trashtracker.entidades.MiembroGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, Long> {

    Optional<MiembroGrupo> findByGrupoIdGrupoAndUsuarioIdUsuario(Long idGrupo, Long idUsuario);
    List<MiembroGrupo> findByGrupoIdGrupo(Long idGrupo);
}

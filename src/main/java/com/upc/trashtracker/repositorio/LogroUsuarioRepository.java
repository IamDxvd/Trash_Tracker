package com.upc.trashtracker.repositorio;

import java.util.List;
import java.util.Optional;
import com.upc.trashtracker.entidades.LogroUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogroUsuarioRepository extends JpaRepository<LogroUsuario, Long> {

    List<LogroUsuario> findByUsuarioIdUsuario(Long idUsuario);
    Optional<LogroUsuario> findByUsuarioIdUsuarioAndLogroIdLogro(Long idUsuario, Long idLogro);
}

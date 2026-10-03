package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.LogroUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogroUsuarioRepository extends JpaRepository<LogroUsuario, Long> {
}

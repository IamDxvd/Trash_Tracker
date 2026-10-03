package com.upc.trashtracker.security.repositories;

import com.upc.trashtracker.entidades.Usuario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long> {

    // El rol es LAZY en Usuario: se trae junto con el usuario para poder armar los permisos
    @EntityGraph(attributePaths = "rol")
    Optional<Usuario> findByCorreo(String correo);
}

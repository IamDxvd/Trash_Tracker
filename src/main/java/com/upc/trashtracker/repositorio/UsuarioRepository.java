package com.upc.trashtracker.repositorio;

import java.util.List;
import java.util.Optional;
import com.upc.trashtracker.entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    boolean existsByCorreo(String correo);
    Optional<Usuario> findByCorreo(String correo);
    List<Usuario> findAllByOrderByPuntosTotalesDesc();

    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select e from Usuario e where e.idUsuario = :id")
    java.util.Optional<Usuario> buscarParaActualizar(@org.springframework.data.repository.query.Param("id") Long id);
}

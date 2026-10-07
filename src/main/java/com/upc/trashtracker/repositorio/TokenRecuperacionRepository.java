package com.upc.trashtracker.repositorio;
import com.upc.trashtracker.entidades.TokenRecuperacion;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.Optional;
public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from TokenRecuperacion t where t.hash = :hash")
    Optional<TokenRecuperacion> bloquear(@Param("hash") String hash);
    void deleteByUsuarioId(Long usuarioId);
}

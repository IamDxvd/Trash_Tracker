package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.TipoResiduo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoResiduoRepository extends JpaRepository<TipoResiduo, Long> {
}

package com.upc.trashtracker.repositorio;

import com.upc.trashtracker.entidades.Canje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CanjeRepository extends JpaRepository<Canje, Long> {
}

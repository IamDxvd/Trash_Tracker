package com.upc.trashtracker.entidades;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
@Entity @Data @Table(name = "token_revocado")
public class TokenRevocado {
    @Id @Column(length = 64) private String hash;
    @Column(nullable = false) private Instant expira;
}

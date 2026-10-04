package com.hirecore.hirecore.dominio.evento;

import com.hirecore.hirecore.dominio.CodigoEstado;

import java.time.Instant;
import java.util.Objects;

public record EstadoCambiado(
        String candidatoId,
        CodigoEstado anterior,
        CodigoEstado nuevo,
        String autor,
        Instant ocurridoEn
) implements EventoDeProgreso {

    public EstadoCambiado {
        Objects.requireNonNull(candidatoId, "candidatoId");
        Objects.requireNonNull(anterior, "anterior");
        Objects.requireNonNull(nuevo, "nuevo");
        Objects.requireNonNull(autor, "autor");
        Objects.requireNonNull(ocurridoEn, "ocurridoEn");
    }

    public EstadoCambiado(String candidatoId, CodigoEstado anterior, CodigoEstado nuevo, String autor) {
        this(candidatoId, anterior, nuevo, autor, Instant.now());
    }

    @Override
    public CodigoEstado estadoVisible() {
        return nuevo;
    }
}

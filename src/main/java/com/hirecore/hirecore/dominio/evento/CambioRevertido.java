package com.hirecore.hirecore.dominio.evento;

import com.hirecore.hirecore.dominio.CodigoEstado;

import java.time.Instant;
import java.util.Objects;

public record CambioRevertido(
        String candidatoId,
        CodigoEstado estadoDeshecho,
        CodigoEstado estadoRestaurado,
        String autor,
        Instant ocurridoEn
) implements EventoDeProgreso {

    public CambioRevertido {
        Objects.requireNonNull(candidatoId, "candidatoId");
        Objects.requireNonNull(estadoDeshecho, "estadoDeshecho");
        Objects.requireNonNull(estadoRestaurado, "estadoRestaurado");
        Objects.requireNonNull(autor, "autor");
        Objects.requireNonNull(ocurridoEn, "ocurridoEn");
    }

    public CambioRevertido(String candidatoId, CodigoEstado estadoDeshecho, CodigoEstado estadoRestaurado, String autor) {
        this(candidatoId, estadoDeshecho, estadoRestaurado, autor, Instant.now());
    }

    @Override
    public CodigoEstado estadoVisible() {
        return estadoRestaurado;
    }
}

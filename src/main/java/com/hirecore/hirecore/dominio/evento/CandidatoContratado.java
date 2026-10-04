package com.hirecore.hirecore.dominio.evento;

import java.time.Instant;
import java.util.Objects;

public record CandidatoContratado(String candidatoId, String autor, Instant ocurridoEn) implements HitoDeDecision {

    public CandidatoContratado {
        Objects.requireNonNull(candidatoId, "candidatoId");
        Objects.requireNonNull(autor, "autor");
        Objects.requireNonNull(ocurridoEn, "ocurridoEn");
    }

    public CandidatoContratado(String candidatoId, String autor) {
        this(candidatoId, autor, Instant.now());
    }
}

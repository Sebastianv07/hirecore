package com.hirecore.hirecore.dominio;

import com.hirecore.hirecore.dominio.estado.EstadoCandidato;

import java.time.Instant;
import java.util.Objects;

public final class MementoCandidato {

    private final String candidatoId;
    private final EstadoCandidato estado;
    private final Instant creadoEn;

    MementoCandidato(String candidatoId, EstadoCandidato estado) {
        this.candidatoId = Objects.requireNonNull(candidatoId, "candidatoId");
        this.estado = Objects.requireNonNull(estado, "estado");
        this.creadoEn = Instant.now();
    }

    public String obtenerCandidatoId() {
        return candidatoId;
    }

    public EstadoCandidato obtenerEstado() {
        return estado;
    }

    public Instant obtenerCreadoEn() {
        return creadoEn;
    }
}

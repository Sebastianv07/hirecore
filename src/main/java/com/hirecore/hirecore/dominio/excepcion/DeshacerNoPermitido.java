package com.hirecore.hirecore.dominio.excepcion;

public class DeshacerNoPermitido extends RuntimeException {

    private final String candidatoId;

    public DeshacerNoPermitido(String candidatoId) {
        super("Solo se puede deshacer el último cambio del candidato '%s'".formatted(candidatoId));
        this.candidatoId = candidatoId;
    }

    public String obtenerCandidatoId() {
        return candidatoId;
    }
}

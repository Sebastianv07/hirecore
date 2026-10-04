package com.hirecore.hirecore.dominio.excepcion;

public class HistorialVacio extends RuntimeException {

    private final String candidatoId;

    public HistorialVacio(String candidatoId) {
        super("No hay cambios para deshacer del candidato '%s'".formatted(candidatoId));
        this.candidatoId = candidatoId;
    }

    public String obtenerCandidatoId() {
        return candidatoId;
    }
}

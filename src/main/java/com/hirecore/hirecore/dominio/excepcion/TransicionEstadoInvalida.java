package com.hirecore.hirecore.dominio.excepcion;

import com.hirecore.hirecore.dominio.CodigoEstado;

public class TransicionEstadoInvalida extends RuntimeException {

    private final CodigoEstado origen;
    private final CodigoEstado destino;

    public TransicionEstadoInvalida(CodigoEstado origen, CodigoEstado destino) {
        super("No se puede transicionar de '%s' a '%s'".formatted(origen, destino));
        this.origen = origen;
        this.destino = destino;
    }

    public CodigoEstado obtenerOrigen() {
        return origen;
    }

    public CodigoEstado obtenerDestino() {
        return destino;
    }
}

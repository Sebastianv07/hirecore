package com.hirecore.hirecore.dominio.excepcion;

import com.hirecore.hirecore.dominio.CodigoEstado;

public class EstadoDesconocido extends RuntimeException {

    private final CodigoEstado codigo;

    public EstadoDesconocido(CodigoEstado codigo) {
        super("No existe un estado con el código '%s'".formatted(codigo));
        this.codigo = codigo;
    }

    public CodigoEstado obtenerCodigo() {
        return codigo;
    }
}

package com.hirecore.hirecore.dominio;

import java.util.Locale;

public final class CodigoEstado {

    private final String valor;

    public CodigoEstado(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("El código del estado no puede estar vacío");
        }
        this.valor = valor.trim()
                .toUpperCase(Locale.ROOT)
                .replace(' ', '_')
                .replace('-', '_');
    }

    public String obtenerValor() {
        return valor;
    }

    @Override
    public boolean equals(Object otro) {
        return otro instanceof CodigoEstado codigo && valor.equals(codigo.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}

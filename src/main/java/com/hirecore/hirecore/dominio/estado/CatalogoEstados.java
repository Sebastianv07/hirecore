package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.excepcion.EstadoDesconocido;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class CatalogoEstados {

    private final Map<CodigoEstado, EstadoCandidato> estados = new HashMap<>();

    public CatalogoEstados(List<EstadoCandidato> estados) {
        for (EstadoCandidato estado : estados) {
            EstadoCandidato repetido = this.estados.putIfAbsent(estado.codigo(), estado);
            if (repetido != null) {
                throw new IllegalStateException("Hay dos estados con el código '%s'".formatted(estado.codigo()));
            }
        }
    }

    public EstadoCandidato obtener(CodigoEstado codigo) {
        Objects.requireNonNull(codigo, "codigo");
        EstadoCandidato estado = estados.get(codigo);
        if (estado == null) {
            throw new EstadoDesconocido(codigo);
        }
        return estado;
    }
}

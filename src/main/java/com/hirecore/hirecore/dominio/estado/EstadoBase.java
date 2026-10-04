package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;

import java.util.Objects;

public abstract class EstadoBase implements EstadoCandidato {

    @Override
    public final EstadoCandidato transicionarA(EstadoCandidato destino) {
        Objects.requireNonNull(destino, "destino");
        if (!destinosPermitidos().contains(destino.codigo())) {
            throw new TransicionEstadoInvalida(codigo(), destino.codigo());
        }
        return destino;
    }

    @Override
    public void alEntrar(Candidato candidato, String autor) {
    }
}

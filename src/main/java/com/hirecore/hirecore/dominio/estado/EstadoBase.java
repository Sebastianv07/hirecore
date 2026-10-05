package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;

import java.util.Objects;

public abstract class EstadoBase implements EstadoCandidato {

    private final ReglasTransicion reglas;

    protected EstadoBase(ReglasTransicion reglas) {
        this.reglas = Objects.requireNonNull(reglas, "reglas");
    }

    @Override
    public final EstadoCandidato transicionarA(EstadoCandidato destino) {
        Objects.requireNonNull(destino, "destino");
        if (!reglas.permite(codigo(), destino.codigo())) {
            throw new TransicionEstadoInvalida(codigo(), destino.codigo());
        }
        return destino;
    }

    @Override
    public void alEntrar(Candidato candidato, String autor) {
    }
}

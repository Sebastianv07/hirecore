package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;

import java.util.List;
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
    public List<EventoDominio> alEntrar(Candidato candidato, String autor) {
        return List.of();
    }
}

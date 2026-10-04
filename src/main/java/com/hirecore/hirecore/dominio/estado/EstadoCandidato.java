package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;

import java.util.Set;

public interface EstadoCandidato {

    CodigoEstado codigo();

    Set<CodigoEstado> destinosPermitidos();

    EstadoCandidato transicionarA(EstadoCandidato destino);

    void alEntrar(Candidato candidato, String autor);
}

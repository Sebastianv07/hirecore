package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;

public interface EstadoCandidato {

    CodigoEstado codigo();

    EstadoCandidato transicionarA(EstadoCandidato destino);

    void alEntrar(Candidato candidato, String autor);
}

package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;

import java.util.List;

public interface EstadoCandidato {

    CodigoEstado codigo();

    EstadoCandidato transicionarA(EstadoCandidato destino);

    List<EventoDominio> alEntrar(Candidato candidato, String autor);
}

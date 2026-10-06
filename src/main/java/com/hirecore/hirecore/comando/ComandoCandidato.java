package com.hirecore.hirecore.comando;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.evento.EventoDominio;

import java.util.List;

public interface ComandoCandidato {

    Candidato candidato();

    List<EventoDominio> ejecutar();
}

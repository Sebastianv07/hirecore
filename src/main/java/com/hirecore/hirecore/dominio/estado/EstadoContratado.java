package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EstadoContratado extends EstadoBase {

    public EstadoContratado(ReglasTransicion reglas) {
        super(reglas);
    }

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("CONTRATADO");
    }

    @Override
    public List<EventoDominio> alEntrar(Candidato candidato, String autor) {
        return List.of(new CandidatoContratado(candidato.obtenerId(), autor));
    }
}

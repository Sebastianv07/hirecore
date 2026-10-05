package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import org.springframework.stereotype.Component;

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
    public void alEntrar(Candidato candidato, String autor) {
        candidato.registrarEvento(new CandidatoContratado(candidato.obtenerId(), autor));
    }
}

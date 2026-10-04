package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EstadoOferta extends EstadoBase {

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("OFERTA");
    }

    @Override
    public Set<CodigoEstado> destinosPermitidos() {
        return Set.of(new CodigoEstado("CONTRATADO"), new CodigoEstado("RECHAZADO"));
    }

    @Override
    public void alEntrar(Candidato candidato, String autor) {
        candidato.registrarEvento(new OfertaEmitida(candidato.obtenerId(), autor));
    }
}

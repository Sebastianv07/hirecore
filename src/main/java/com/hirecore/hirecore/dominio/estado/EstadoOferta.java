package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import org.springframework.stereotype.Component;

@Component
public class EstadoOferta extends EstadoBase {

    public EstadoOferta(ReglasTransicion reglas) {
        super(reglas);
    }

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("OFERTA");
    }

    @Override
    public void alEntrar(Candidato candidato, String autor) {
        candidato.registrarEvento(new OfertaEmitida(candidato.obtenerId(), autor));
    }
}

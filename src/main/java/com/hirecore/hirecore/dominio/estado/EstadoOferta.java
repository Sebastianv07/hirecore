package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public List<EventoDominio> alEntrar(Candidato candidato, String autor) {
        return List.of(new OfertaEmitida(candidato.obtenerId(), autor));
    }
}

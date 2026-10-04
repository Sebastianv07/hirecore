package com.hirecore.hirecore.comando;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.MementoCandidato;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.historial.HistorialCambios;
import com.hirecore.hirecore.notificacion.PublicarEventos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class GestorDeCandidato {

    private final HistorialCambios historial;
    private final PublicarEventos publicador;

    public GestorDeCandidato(HistorialCambios historial, PublicarEventos publicador) {
        this.historial = Objects.requireNonNull(historial, "historial");
        this.publicador = Objects.requireNonNull(publicador, "publicador");
    }

    public List<EventoDominio> ejecutar(ComandoCandidato comando) {
        Candidato candidato = comando.candidato();
        MementoCandidato foto = candidato.crearMemento();
        comando.ejecutar();
        historial.guardar(foto);
        return publicarPendientes(candidato);
    }

    public List<EventoDominio> deshacer(Candidato candidato, String autor) {
        MementoCandidato foto = historial.extraerUltimo(candidato.obtenerId());
        candidato.restaurar(foto, autor);
        return publicarPendientes(candidato);
    }

    private List<EventoDominio> publicarPendientes(Candidato candidato) {
        List<EventoDominio> eventos = candidato.extraerEventos();
        publicador.publicar(eventos);
        return eventos;
    }
}

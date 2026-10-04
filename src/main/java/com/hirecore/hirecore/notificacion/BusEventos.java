package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.EventoDominio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class BusEventos implements PublicarEventos {

    private static final Logger log = LoggerFactory.getLogger(BusEventos.class);

    private final CopyOnWriteArrayList<ObservadorEvento> observadores = new CopyOnWriteArrayList<>();

    public BusEventos(List<ObservadorEvento> observadores) {
        observadores.forEach(this::suscribir);
    }

    public void suscribir(ObservadorEvento observador) {
        Objects.requireNonNull(observador, "observador");
        observadores.addIfAbsent(observador);
    }

    public void desuscribir(ObservadorEvento observador) {
        observadores.remove(observador);
    }

    @Override
    public void publicar(List<EventoDominio> eventos) {
        for (EventoDominio evento : eventos) {
            observadores.forEach(observador -> notificar(observador, evento));
        }
    }

    private void notificar(ObservadorEvento observador, EventoDominio evento) {
        try {
            if (observador.leInteresa(evento)) {
                observador.manejar(evento);
            }
        } catch (RuntimeException error) {
            log.error("El observador {} falló con el evento {}", observador.getClass().getSimpleName(), evento, error);
        }
    }
}

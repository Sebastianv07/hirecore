package com.hirecore.hirecore.soporte;

import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.notificacion.PublicarEventos;

import java.util.ArrayList;
import java.util.List;

public class PublicadorEnMemoria implements PublicarEventos {

    private final List<EventoDominio> publicados = new ArrayList<>();

    @Override
    public void publicar(List<EventoDominio> eventos) {
        publicados.addAll(eventos);
    }

    public List<EventoDominio> publicados() {
        return List.copyOf(publicados);
    }
}

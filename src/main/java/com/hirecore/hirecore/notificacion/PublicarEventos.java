package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.EventoDominio;

import java.util.List;

public interface PublicarEventos {

    void publicar(List<EventoDominio> eventos);
}

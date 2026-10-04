package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.EventoDominio;

public interface ObservadorEvento {

    boolean leInteresa(EventoDominio evento);

    void manejar(EventoDominio evento);
}

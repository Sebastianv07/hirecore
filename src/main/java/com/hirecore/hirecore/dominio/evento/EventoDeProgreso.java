package com.hirecore.hirecore.dominio.evento;

import com.hirecore.hirecore.dominio.CodigoEstado;

public interface EventoDeProgreso extends EventoDominio {

    CodigoEstado estadoVisible();
}

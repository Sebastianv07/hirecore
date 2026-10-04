package com.hirecore.hirecore.dominio.evento;

import java.time.Instant;

public interface EventoDominio {

    String candidatoId();

    String autor();

    Instant ocurridoEn();
}

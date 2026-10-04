package com.hirecore.hirecore.notificacion.canal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CanalRegistro implements CanalNotificacion {

    private static final Logger log = LoggerFactory.getLogger(CanalRegistro.class);

    @Override
    public void enviar(String destinatario, String mensaje) {
        log.info("[{}] {}", destinatario, mensaje);
    }
}

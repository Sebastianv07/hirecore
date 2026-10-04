package com.hirecore.hirecore.notificacion.canal;

public interface CanalNotificacion {

    void enviar(String destinatario, String mensaje);
}

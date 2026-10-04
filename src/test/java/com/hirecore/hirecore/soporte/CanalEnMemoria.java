package com.hirecore.hirecore.soporte;

import com.hirecore.hirecore.notificacion.canal.CanalNotificacion;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CanalEnMemoria implements CanalNotificacion {

    public record Mensaje(String destinatario, String texto) {
    }

    private final List<Mensaje> mensajes = new CopyOnWriteArrayList<>();

    @Override
    public void enviar(String destinatario, String mensaje) {
        mensajes.add(new Mensaje(destinatario, mensaje));
    }

    public List<Mensaje> mensajes() {
        return List.copyOf(mensajes);
    }

    public List<Mensaje> mensajesPara(String destinatario) {
        return mensajes.stream().filter(m -> m.destinatario().equals(destinatario)).toList();
    }

    public void limpiar() {
        mensajes.clear();
    }
}

package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.notificacion.canal.CanalNotificacion;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NotificarNomina implements ObservadorEvento {

    private final CanalNotificacion canal;

    public NotificarNomina(CanalNotificacion canal) {
        this.canal = Objects.requireNonNull(canal, "canal");
    }

    @Override
    public boolean leInteresa(EventoDominio evento) {
        return evento instanceof CandidatoContratado;
    }

    @Override
    public void manejar(EventoDominio evento) {
        canal.enviar("nomina", "Dar de alta al candidato %s para iniciar contrato y pagos".formatted(evento.candidatoId()));
    }
}

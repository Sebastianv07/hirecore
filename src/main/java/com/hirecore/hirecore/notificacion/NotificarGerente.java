package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.evento.HitoDeDecision;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import com.hirecore.hirecore.notificacion.canal.CanalNotificacion;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NotificarGerente implements ObservadorEvento {

    private final CanalNotificacion canal;

    public NotificarGerente(CanalNotificacion canal) {
        this.canal = Objects.requireNonNull(canal, "canal");
    }

    @Override
    public boolean leInteresa(EventoDominio evento) {
        return evento instanceof HitoDeDecision;
    }

    @Override
    public void manejar(EventoDominio evento) {
        String mensaje = switch (evento) {
            case OfertaEmitida oferta -> "Hay una oferta para aprobar del candidato %s".formatted(oferta.candidatoId());
            case CandidatoContratado contratado -> "Se confirmó la contratación del candidato %s".formatted(contratado.candidatoId());
            default -> "El candidato %s requiere una decisión".formatted(evento.candidatoId());
        };
        canal.enviar("gerencia", mensaje);
    }
}

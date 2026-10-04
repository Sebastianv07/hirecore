package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.CambioRevertido;
import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EstadoCambiado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import com.hirecore.hirecore.notificacion.canal.CanalNotificacion;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class NotificarReclutador implements ObservadorEvento {

    private final CanalNotificacion canal;

    public NotificarReclutador(CanalNotificacion canal) {
        this.canal = Objects.requireNonNull(canal, "canal");
    }

    @Override
    public boolean leInteresa(EventoDominio evento) {
        return true;
    }

    @Override
    public void manejar(EventoDominio evento) {
        String mensaje = switch (evento) {
            case EstadoCambiado cambiado -> "Candidato %s pasó de %s a %s (autor: %s)".formatted(
                    cambiado.candidatoId(), cambiado.anterior(), cambiado.nuevo(), cambiado.autor());
            case CambioRevertido revertido -> "Se deshizo el cambio de %s: volvió de %s a %s (autor: %s)".formatted(
                    revertido.candidatoId(), revertido.estadoDeshecho(), revertido.estadoRestaurado(), revertido.autor());
            case OfertaEmitida oferta -> "Oferta emitida al candidato %s (autor: %s)".formatted(
                    oferta.candidatoId(), oferta.autor());
            case CandidatoContratado contratado -> "Candidato %s contratado (autor: %s)".formatted(
                    contratado.candidatoId(), contratado.autor());
            default -> "Evento del candidato %s (autor: %s)".formatted(evento.candidatoId(), evento.autor());
        };
        canal.enviar("reclutamiento", mensaje);
    }
}

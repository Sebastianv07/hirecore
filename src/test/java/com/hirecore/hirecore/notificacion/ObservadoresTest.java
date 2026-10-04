package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.CambioRevertido;
import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EstadoCambiado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import com.hirecore.hirecore.soporte.CanalEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hirecore.hirecore.soporte.EstadosDePrueba.codigo;
import static org.assertj.core.api.Assertions.assertThat;

class ObservadoresTest {

    private final EstadoCambiado cambiado = new EstadoCambiado("c-001", codigo("ENTREVISTA"), codigo("OFERTA"), "sofia");
    private final CambioRevertido revertido = new CambioRevertido("c-001", codigo("CONTRATADO"), codigo("OFERTA"), "sofia");
    private final OfertaEmitida oferta = new OfertaEmitida("c-001", "sofia");
    private final CandidatoContratado contratado = new CandidatoContratado("c-001", "sofia");
    private final List<EventoDominio> todos = List.of(cambiado, revertido, oferta, contratado);

    private List<EventoDominio> interesantesPara(ObservadorEvento observador) {
        return todos.stream().filter(observador::leInteresa).toList();
    }

    @Test
    void elReclutadorSeEnteraDeTodo() {
        assertThat(interesantesPara(new NotificarReclutador(new CanalEnMemoria()))).isEqualTo(todos);
    }

    @Test
    void elPortalSoloRecibeElProgresoDelCandidato() {
        assertThat(interesantesPara(new ActualizarPortalCandidato(new CanalEnMemoria())))
                .containsExactly(cambiado, revertido);
    }

    @Test
    void elGerenteSoloRecibeHitosDeDecision() {
        assertThat(interesantesPara(new NotificarGerente(new CanalEnMemoria())))
                .containsExactly(oferta, contratado);
    }

    @Test
    void nominaSoloRecibeLaContratacion() {
        assertThat(interesantesPara(new NotificarNomina(new CanalEnMemoria())))
                .containsExactly(contratado);
    }

    @Test
    void cadaObservadorAvisaPorSuCanal() {
        CanalEnMemoria canal = new CanalEnMemoria();

        new NotificarNomina(canal).manejar(contratado);
        new NotificarGerente(canal).manejar(oferta);
        new ActualizarPortalCandidato(canal).manejar(cambiado);

        assertThat(canal.mensajes())
                .extracting(CanalEnMemoria.Mensaje::destinatario)
                .containsExactly("nomina", "gerencia", "portal:c-001");
    }
}

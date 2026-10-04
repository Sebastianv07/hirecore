package com.hirecore.hirecore.notificacion;

import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BusEventosTest {

    private static class ObservadorQueAnota implements ObservadorEvento {

        private final Class<? extends EventoDominio> tipo;
        private final List<EventoDominio> recibidos = new ArrayList<>();

        ObservadorQueAnota(Class<? extends EventoDominio> tipo) {
            this.tipo = tipo;
        }

        @Override
        public boolean leInteresa(EventoDominio evento) {
            return tipo.isInstance(evento);
        }

        @Override
        public void manejar(EventoDominio evento) {
            recibidos.add(evento);
        }
    }

    private static class ObservadorQueFalla implements ObservadorEvento {

        @Override
        public boolean leInteresa(EventoDominio evento) {
            return true;
        }

        @Override
        public void manejar(EventoDominio evento) {
            throw new IllegalStateException("fallo simulado");
        }
    }

    private final OfertaEmitida oferta = new OfertaEmitida("c-001", "sofia");
    private final CandidatoContratado contratado = new CandidatoContratado("c-001", "sofia");

    @Test
    void soloEntregaElEventoAQuienLeInteresa() {
        ObservadorQueAnota interesado = new ObservadorQueAnota(OfertaEmitida.class);
        ObservadorQueAnota noInteresado = new ObservadorQueAnota(CandidatoContratado.class);
        BusEventos bus = new BusEventos(List.of(interesado, noInteresado));

        bus.publicar(List.of(oferta));

        assertThat(interesado.recibidos).containsExactly(oferta);
        assertThat(noInteresado.recibidos).isEmpty();
    }

    @Test
    void unObservadorQueFallaNoImpideQueLosDemasReciban() {
        ObservadorQueAnota despues = new ObservadorQueAnota(EventoDominio.class);
        BusEventos bus = new BusEventos(List.of(new ObservadorQueFalla(), despues));

        bus.publicar(List.of(oferta, contratado));

        assertThat(despues.recibidos).containsExactly(oferta, contratado);
    }

    @Test
    void desuscribirDejaDeEntregarEventos() {
        ObservadorQueAnota observador = new ObservadorQueAnota(EventoDominio.class);
        BusEventos bus = new BusEventos(List.of(observador));

        bus.desuscribir(observador);
        bus.publicar(List.of(oferta));

        assertThat(observador.recibidos).isEmpty();
    }

    @Test
    void suscribirDosVecesNoDuplicaLaEntrega() {
        ObservadorQueAnota observador = new ObservadorQueAnota(EventoDominio.class);
        BusEventos bus = new BusEventos(List.of(observador));

        bus.suscribir(observador);
        bus.publicar(List.of(oferta));

        assertThat(observador.recibidos).hasSize(1);
    }
}

package com.hirecore.hirecore.dominio;

import com.hirecore.hirecore.dominio.estado.CatalogoEstados;
import com.hirecore.hirecore.dominio.evento.CambioRevertido;
import com.hirecore.hirecore.dominio.evento.CandidatoContratado;
import com.hirecore.hirecore.dominio.evento.EstadoCambiado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.evento.OfertaEmitida;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;
import com.hirecore.hirecore.soporte.EstadosDePrueba;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hirecore.hirecore.soporte.EstadosDePrueba.codigo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CandidatoTest {

    private final CatalogoEstados catalogo = EstadosDePrueba.catalogo();

    private Candidato candidatoEn(String estado) {
        return new Candidato("c-001", "Ana", catalogo.obtener(codigo(estado)));
    }

    private void transicionar(Candidato candidato, String destino) {
        candidato.transicionarA(catalogo.obtener(codigo(destino)), "sofia");
    }

    @Test
    void unaTransicionValidaCambiaElEstadoYRegistraEstadoCambiado() {
        Candidato candidato = candidatoEn("APLICADO");

        transicionar(candidato, "ENTREVISTA");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        EstadoCambiado evento = (EstadoCambiado) candidato.extraerEventos().getFirst();
        assertThat(evento.candidatoId()).isEqualTo("c-001");
        assertThat(evento.anterior()).isEqualTo(codigo("APLICADO"));
        assertThat(evento.nuevo()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(evento.autor()).isEqualTo("sofia");
    }

    @Test
    void unaTransicionInvalidaNoCambiaElEstadoNiRegistraEventos() {
        Candidato candidato = candidatoEn("ENTREVISTA");

        assertThatThrownBy(() -> transicionar(candidato, "CONTRATADO"))
                .isInstanceOf(TransicionEstadoInvalida.class);

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(candidato.extraerEventos()).isEmpty();
    }

    @Test
    void entrarAOfertaEmiteOfertaEmitida() {
        Candidato candidato = candidatoEn("ENTREVISTA");

        transicionar(candidato, "OFERTA");

        assertThat(candidato.extraerEventos())
                .extracting(EventoDominio::getClass)
                .containsExactly(EstadoCambiado.class, OfertaEmitida.class);
    }

    @Test
    void entrarAContratadoEmiteCandidatoContratado() {
        Candidato candidato = candidatoEn("OFERTA");

        transicionar(candidato, "CONTRATADO");

        assertThat(candidato.extraerEventos())
                .extracting(EventoDominio::getClass)
                .containsExactly(EstadoCambiado.class, CandidatoContratado.class);
    }

    @Test
    void restaurarVuelveAlEstadoDeLaFotoYRegistraQuienDeshizo() {
        Candidato candidato = candidatoEn("ENTREVISTA");
        MementoCandidato foto = candidato.crearMemento();
        transicionar(candidato, "RECHAZADO");
        candidato.extraerEventos();

        candidato.restaurar(foto, "supervisora-laura");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        CambioRevertido evento = (CambioRevertido) candidato.extraerEventos().getFirst();
        assertThat(evento.estadoDeshecho()).isEqualTo(codigo("RECHAZADO"));
        assertThat(evento.estadoRestaurado()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(evento.autor()).isEqualTo("supervisora-laura");
    }

    @Test
    void noSePuedeRestaurarConLaFotoDeOtroCandidato() {
        Candidato ana = candidatoEn("APLICADO");
        Candidato luis = new Candidato("c-002", "Luis", catalogo.obtener(codigo("APLICADO")));

        assertThatThrownBy(() -> ana.restaurar(luis.crearMemento(), "sofia"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void extraerEventosEntregaLosPendientesUnaSolaVez() {
        Candidato candidato = candidatoEn("APLICADO");
        transicionar(candidato, "ENTREVISTA");

        List<EventoDominio> primera = candidato.extraerEventos();
        List<EventoDominio> segunda = candidato.extraerEventos();

        assertThat(primera).hasSize(1);
        assertThat(segunda).isEmpty();
    }
}

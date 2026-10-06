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

    private List<EventoDominio> transicionar(Candidato candidato, String destino) {
        return candidato.transicionarA(catalogo.obtener(codigo(destino)), "sofia");
    }

    @Test
    void unaTransicionValidaCambiaElEstadoYDevuelveEstadoCambiado() {
        Candidato candidato = candidatoEn("APLICADO");

        List<EventoDominio> eventos = transicionar(candidato, "ENTREVISTA");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        EstadoCambiado evento = (EstadoCambiado) eventos.getFirst();
        assertThat(evento.candidatoId()).isEqualTo("c-001");
        assertThat(evento.anterior()).isEqualTo(codigo("APLICADO"));
        assertThat(evento.nuevo()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(evento.autor()).isEqualTo("sofia");
    }

    @Test
    void unaTransicionInvalidaNoCambiaElEstadoNiDevuelveEventos() {
        Candidato candidato = candidatoEn("ENTREVISTA");

        assertThatThrownBy(() -> transicionar(candidato, "CONTRATADO"))
                .isInstanceOf(TransicionEstadoInvalida.class);

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
    }

    @Test
    void entrarAOfertaEmiteOfertaEmitida() {
        Candidato candidato = candidatoEn("ENTREVISTA");

        List<EventoDominio> eventos = transicionar(candidato, "OFERTA");

        assertThat(eventos).hasExactlyElementsOfTypes(EstadoCambiado.class, OfertaEmitida.class);
    }

    @Test
    void entrarAContratadoEmiteCandidatoContratado() {
        Candidato candidato = candidatoEn("OFERTA");

        List<EventoDominio> eventos = transicionar(candidato, "CONTRATADO");

        assertThat(eventos).hasExactlyElementsOfTypes(EstadoCambiado.class, CandidatoContratado.class);
    }

    @Test
    void restaurarVuelveAlEstadoDeLaFotoYDevuelveQuienDeshizo() {
        Candidato candidato = candidatoEn("ENTREVISTA");
        MementoCandidato foto = candidato.crearMemento();
        transicionar(candidato, "RECHAZADO");

        List<EventoDominio> eventos = candidato.restaurar(foto, "supervisora-laura");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        CambioRevertido evento = (CambioRevertido) eventos.getFirst();
        assertThat(evento.estadoDeshecho()).isEqualTo(codigo("RECHAZADO"));
        assertThat(evento.estadoRestaurado()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(evento.autor()).isEqualTo("supervisora-laura");
    }

    @Test
    void laFotoGuardaElCandidatoCompleto() {
        Candidato candidato = candidatoEn("APLICADO");

        MementoCandidato foto = candidato.crearMemento();
        transicionar(candidato, "ENTREVISTA");

        assertThat(foto.obtenerCandidatoId()).isEqualTo("c-001");
        assertThat(foto.obtenerNombre()).isEqualTo("Ana");
        assertThat(foto.obtenerEstado().codigo()).isEqualTo(codigo("APLICADO"));
        assertThat(foto.obtenerCreadoEn()).isNotNull();
    }

    @Test
    void restaurarAplicaElCandidatoCompletoGuardadoEnLaFoto() {
        Candidato candidato = candidatoEn("ENTREVISTA");
        MementoCandidato foto = new MementoCandidato("c-001", "Ana Maria", catalogo.obtener(codigo("APLICADO")));
        transicionar(candidato, "RECHAZADO");

        candidato.restaurar(foto, "supervisora-laura");

        assertThat(candidato.obtenerId()).isEqualTo("c-001");
        assertThat(candidato.obtenerNombre()).isEqualTo("Ana Maria");
        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("APLICADO"));
    }

    @Test
    void noSePuedeRestaurarConLaFotoDeOtroCandidato() {
        Candidato ana = candidatoEn("APLICADO");
        Candidato luis = new Candidato("c-002", "Luis", catalogo.obtener(codigo("APLICADO")));

        assertThatThrownBy(() -> ana.restaurar(luis.crearMemento(), "sofia"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cadaTransicionDevuelveSusPropiosEventosSinAcumularlos() {
        Candidato candidato = candidatoEn("APLICADO");

        List<EventoDominio> primera = transicionar(candidato, "ENTREVISTA");
        List<EventoDominio> segunda = transicionar(candidato, "OFERTA");

        assertThat(primera).hasExactlyElementsOfTypes(EstadoCambiado.class);
        assertThat(segunda).hasExactlyElementsOfTypes(EstadoCambiado.class, OfertaEmitida.class);
    }
}

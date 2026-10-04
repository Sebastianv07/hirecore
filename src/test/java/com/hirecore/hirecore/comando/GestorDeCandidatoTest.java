package com.hirecore.hirecore.comando;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.estado.CatalogoEstados;
import com.hirecore.hirecore.dominio.evento.CambioRevertido;
import com.hirecore.hirecore.dominio.evento.EstadoCambiado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;
import com.hirecore.hirecore.dominio.excepcion.HistorialVacio;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;
import com.hirecore.hirecore.historial.HistorialEnMemoria;
import com.hirecore.hirecore.soporte.EstadosDePrueba;
import com.hirecore.hirecore.soporte.PublicadorEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.hirecore.hirecore.soporte.EstadosDePrueba.codigo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GestorDeCandidatoTest {

    private final CatalogoEstados catalogo = EstadosDePrueba.catalogo();
    private final HistorialEnMemoria historial = new HistorialEnMemoria();
    private final PublicadorEnMemoria publicador = new PublicadorEnMemoria();
    private final GestorDeCandidato gestor = new GestorDeCandidato(historial, publicador);

    private Candidato nuevoCandidato(String id) {
        return new Candidato(id, "Candidato " + id, catalogo.obtener(codigo("APLICADO")));
    }

    private List<EventoDominio> cambiar(Candidato candidato, String destino) {
        return gestor.ejecutar(new CambiarEstadoCommand(candidato, codigo(destino), "sofia", catalogo));
    }

    @Test
    void ejecutarGuardaLaFotoYPublicaLosEventos() {
        Candidato candidato = nuevoCandidato("c-001");

        List<EventoDominio> eventos = cambiar(candidato, "ENTREVISTA");

        assertThat(historial.tamanio("c-001")).isEqualTo(1);
        assertThat(eventos).singleElement().isInstanceOf(EstadoCambiado.class);
        assertThat(publicador.publicados()).isEqualTo(eventos);
    }

    @Test
    void unaTransicionInvalidaNoGuardaFotoNiPublica() {
        Candidato candidato = nuevoCandidato("c-001");

        assertThatThrownBy(() -> cambiar(candidato, "CONTRATADO"))
                .isInstanceOf(TransicionEstadoInvalida.class);

        assertThat(historial.tamanio("c-001")).isZero();
        assertThat(publicador.publicados()).isEmpty();
    }

    @Test
    void deshacerUnRechazoPorErrorDevuelveAlCandidatoASuEtapa() {
        Candidato candidato = nuevoCandidato("c-001");
        cambiar(candidato, "ENTREVISTA");
        cambiar(candidato, "RECHAZADO");

        List<EventoDominio> eventos = gestor.deshacer(candidato, "supervisora-laura");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        CambioRevertido revertido = (CambioRevertido) eventos.getFirst();
        assertThat(revertido.estadoDeshecho()).isEqualTo(codigo("RECHAZADO"));
        assertThat(revertido.autor()).isEqualTo("supervisora-laura");
    }

    @Test
    void deshacerSinCambiosLanzaHistorialVacio() {
        Candidato candidato = nuevoCandidato("c-001");

        assertThatThrownBy(() -> gestor.deshacer(candidato, "sofia"))
                .isInstanceOf(HistorialVacio.class);
    }

    @Test
    void deshacerSoloAfectaAlCandidatoIndicado() {
        Candidato ana = nuevoCandidato("c-001");
        Candidato luis = nuevoCandidato("c-002");
        cambiar(ana, "ENTREVISTA");
        cambiar(luis, "ENTREVISTA");
        cambiar(luis, "OFERTA");

        gestor.deshacer(ana, "sofia");

        assertThat(ana.obtenerEstado().codigo()).isEqualTo(codigo("APLICADO"));
        assertThat(luis.obtenerEstado().codigo()).isEqualTo(codigo("OFERTA"));
    }

    @Test
    void sePuedeDeshacerVariosPasosHaciaAtras() {
        Candidato candidato = nuevoCandidato("c-001");
        cambiar(candidato, "ENTREVISTA");
        cambiar(candidato, "PRUEBA_TECNICA");
        cambiar(candidato, "OFERTA");

        gestor.deshacer(candidato, "sofia");
        gestor.deshacer(candidato, "sofia");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(historial.tamanio("c-001")).isEqualTo(1);
    }
}

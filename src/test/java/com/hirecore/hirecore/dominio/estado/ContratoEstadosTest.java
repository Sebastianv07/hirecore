package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatNoException;

@SpringBootTest
class ContratoEstadosTest {

    private static final CodigoEstado INICIAL = new CodigoEstado("APLICADO");

    @Autowired
    private List<EstadoCandidato> estados;

    @Autowired
    private CatalogoEstados catalogo;

    @Test
    void cadaEstadoTieneUnCodigoUnico() {
        List<CodigoEstado> codigos = estados.stream().map(EstadoCandidato::codigo).toList();
        assertThat(codigos).doesNotHaveDuplicates();
    }

    @Test
    void todoDestinoDeclaradoExisteEnElCatalogo() {
        for (EstadoCandidato estado : estados) {
            for (CodigoEstado destino : estado.destinosPermitidos()) {
                assertThatNoException()
                        .as("%s declara el destino %s", estado.codigo(), destino)
                        .isThrownBy(() -> catalogo.obtener(destino));
            }
        }
    }

    @Test
    void todoEstadoEsAlcanzableDesdeElInicial() {
        Set<CodigoEstado> alcanzables = alcanzablesDesde(INICIAL);
        for (EstadoCandidato estado : estados) {
            assertThat(alcanzables).as("%s no es alcanzable", estado.codigo()).contains(estado.codigo());
        }
    }

    @Test
    void desdeTodoEstadoSeLlegaAUnEstadoFinal() {
        for (EstadoCandidato estado : estados) {
            boolean llegaAFinal = alcanzablesDesde(estado.codigo()).stream()
                    .anyMatch(codigo -> catalogo.obtener(codigo).destinosPermitidos().isEmpty());
            assertThat(llegaAFinal).as("%s es un callejón sin salida", estado.codigo()).isTrue();
        }
    }

    @Test
    void unaTransicionPermitidaDevuelveElDestino() {
        for (EstadoCandidato estado : estados) {
            for (CodigoEstado codigoDestino : estado.destinosPermitidos()) {
                EstadoCandidato destino = catalogo.obtener(codigoDestino);
                assertThat(estado.transicionarA(destino)).isSameAs(destino);
            }
        }
    }

    @Test
    void unaTransicionNoPermitidaSiempreLanzaTransicionEstadoInvalida() {
        for (EstadoCandidato estado : estados) {
            for (EstadoCandidato destino : estados) {
                if (!estado.destinosPermitidos().contains(destino.codigo())) {
                    assertThatThrownBy(() -> estado.transicionarA(destino))
                            .isInstanceOf(TransicionEstadoInvalida.class);
                }
            }
        }
    }

    private Set<CodigoEstado> alcanzablesDesde(CodigoEstado origen) {
        Set<CodigoEstado> visitados = new HashSet<>();
        Deque<CodigoEstado> pendientes = new ArrayDeque<>(List.of(origen));
        while (!pendientes.isEmpty()) {
            CodigoEstado actual = pendientes.pop();
            if (visitados.add(actual)) {
                pendientes.addAll(catalogo.obtener(actual).destinosPermitidos());
            }
        }
        return visitados;
    }
}

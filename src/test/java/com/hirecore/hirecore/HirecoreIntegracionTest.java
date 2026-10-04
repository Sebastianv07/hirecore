package com.hirecore.hirecore;

import com.hirecore.hirecore.comando.CambiarEstadoCommand;
import com.hirecore.hirecore.comando.GestorDeCandidato;
import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.estado.CatalogoEstados;
import com.hirecore.hirecore.dominio.excepcion.TransicionEstadoInvalida;
import com.hirecore.hirecore.soporte.CanalEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static com.hirecore.hirecore.soporte.EstadosDePrueba.codigo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class HirecoreIntegracionTest {

    @TestConfiguration
    static class CanalDePrueba {

        @Bean
        @Primary
        CanalEnMemoria canalEnMemoria() {
            return new CanalEnMemoria();
        }
    }

    @Autowired
    private GestorDeCandidato gestor;

    @Autowired
    private CatalogoEstados catalogo;

    @Autowired
    private CanalEnMemoria canal;

    private Candidato candidato;

    @BeforeEach
    void prepararCandidato() {
        canal.limpiar();
        candidato = new Candidato("c-001", "Ana", catalogo.obtener(codigo("APLICADO")));
    }

    private void cambiar(String destino) {
        gestor.ejecutar(new CambiarEstadoCommand(candidato, codigo(destino), "reclutador-sofia", catalogo));
    }

    @Test
    void elRecorridoCompletoAvisaACadaInteresadoSoloLoQueLeCorresponde() {
        cambiar("ENTREVISTA");
        cambiar("PRUEBA_TECNICA");
        cambiar("OFERTA");
        cambiar("CONTRATADO");
        gestor.deshacer(candidato, "supervisora-laura");

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("OFERTA"));
        assertThat(canal.mensajesPara("reclutamiento")).hasSize(7);
        assertThat(canal.mensajesPara("portal:c-001")).hasSize(5);
        assertThat(canal.mensajesPara("gerencia")).hasSize(2);
        assertThat(canal.mensajesPara("nomina")).hasSize(1);
    }

    @Test
    void unSaltoInvalidoNoAvisaANadie() {
        cambiar("ENTREVISTA");
        canal.limpiar();

        assertThatThrownBy(() -> cambiar("CONTRATADO")).isInstanceOf(TransicionEstadoInvalida.class);

        assertThat(candidato.obtenerEstado().codigo()).isEqualTo(codigo("ENTREVISTA"));
        assertThat(canal.mensajes()).isEmpty();
    }
}

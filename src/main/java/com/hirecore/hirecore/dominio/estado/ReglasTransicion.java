package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ReglasTransicion {

    private final Map<CodigoEstado, Set<CodigoEstado>> tabla = Map.of(
            codigo("APLICADO"), destinos("ENTREVISTA", "RECHAZADO"),
            codigo("ENTREVISTA"), destinos("PRUEBA_TECNICA", "REFERENCIA", "OFERTA", "RECHAZADO"),
            codigo("PRUEBA_TECNICA"), destinos("REFERENCIA", "OFERTA", "RECHAZADO"),
            codigo("REFERENCIA"), destinos("PRUEBA_TECNICA", "OFERTA", "RECHAZADO"),
            codigo("OFERTA"), destinos("CONTRATADO", "RECHAZADO"),
            codigo("CONTRATADO"), destinos(),
            codigo("RECHAZADO"), destinos()
    );

    public Set<CodigoEstado> destinosDe(CodigoEstado origen) {
        Objects.requireNonNull(origen, "origen");
        return tabla.getOrDefault(origen, Set.of());
    }

    public boolean permite(CodigoEstado origen, CodigoEstado destino) {
        Objects.requireNonNull(destino, "destino");
        return destinosDe(origen).contains(destino);
    }

    public Set<CodigoEstado> origenesDeclarados() {
        return tabla.keySet();
    }

    private static CodigoEstado codigo(String valor) {
        return new CodigoEstado(valor);
    }

    private static Set<CodigoEstado> destinos(String... codigos) {
        return Arrays.stream(codigos)
                .map(ReglasTransicion::codigo)
                .collect(Collectors.toUnmodifiableSet());
    }
}

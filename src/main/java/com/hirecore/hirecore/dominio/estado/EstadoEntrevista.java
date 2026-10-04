package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EstadoEntrevista extends EstadoBase {

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("ENTREVISTA");
    }

    @Override
    public Set<CodigoEstado> destinosPermitidos() {
        return Set.of(
                new CodigoEstado("PRUEBA_TECNICA"),
                new CodigoEstado("REFERENCIA"),
                new CodigoEstado("OFERTA"),
                new CodigoEstado("RECHAZADO")
        );
    }
}

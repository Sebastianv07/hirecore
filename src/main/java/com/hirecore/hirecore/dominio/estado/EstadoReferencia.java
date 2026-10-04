package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EstadoReferencia extends EstadoBase {

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("REFERENCIA");
    }

    @Override
    public Set<CodigoEstado> destinosPermitidos() {
        return Set.of(new CodigoEstado("PRUEBA_TECNICA"), new CodigoEstado("OFERTA"), new CodigoEstado("RECHAZADO"));
    }
}

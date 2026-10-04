package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EstadoPruebaTecnica extends EstadoBase {

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("PRUEBA_TECNICA");
    }

    @Override
    public Set<CodigoEstado> destinosPermitidos() {
        return Set.of(new CodigoEstado("REFERENCIA"), new CodigoEstado("OFERTA"), new CodigoEstado("RECHAZADO"));
    }
}

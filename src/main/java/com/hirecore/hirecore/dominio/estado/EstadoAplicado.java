package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EstadoAplicado extends EstadoBase {

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("APLICADO");
    }

    @Override
    public Set<CodigoEstado> destinosPermitidos() {
        return Set.of(new CodigoEstado("ENTREVISTA"), new CodigoEstado("RECHAZADO"));
    }
}

package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class EstadoRechazado extends EstadoBase {

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("RECHAZADO");
    }

    @Override
    public Set<CodigoEstado> destinosPermitidos() {
        return Set.of();
    }
}

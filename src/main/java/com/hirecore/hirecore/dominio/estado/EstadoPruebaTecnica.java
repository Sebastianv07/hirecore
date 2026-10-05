package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

@Component
public class EstadoPruebaTecnica extends EstadoBase {

    public EstadoPruebaTecnica(ReglasTransicion reglas) {
        super(reglas);
    }

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("PRUEBA_TECNICA");
    }
}

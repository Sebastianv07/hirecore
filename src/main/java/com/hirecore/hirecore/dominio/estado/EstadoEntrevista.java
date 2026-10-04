package com.hirecore.hirecore.dominio.estado;

import com.hirecore.hirecore.dominio.CodigoEstado;
import org.springframework.stereotype.Component;

@Component
public class EstadoEntrevista extends EstadoBase {

    public EstadoEntrevista(ReglasTransicion reglas) {
        super(reglas);
    }

    @Override
    public CodigoEstado codigo() {
        return new CodigoEstado("ENTREVISTA");
    }
}

package com.hirecore.hirecore.soporte;

import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.estado.CatalogoEstados;
import com.hirecore.hirecore.dominio.estado.EstadoAplicado;
import com.hirecore.hirecore.dominio.estado.EstadoContratado;
import com.hirecore.hirecore.dominio.estado.EstadoEntrevista;
import com.hirecore.hirecore.dominio.estado.EstadoOferta;
import com.hirecore.hirecore.dominio.estado.EstadoPruebaTecnica;
import com.hirecore.hirecore.dominio.estado.EstadoRechazado;
import com.hirecore.hirecore.dominio.estado.EstadoReferencia;
import com.hirecore.hirecore.dominio.estado.ReglasTransicion;

import java.util.List;

public final class EstadosDePrueba {

    private EstadosDePrueba() {
    }

    public static CatalogoEstados catalogo() {
        ReglasTransicion reglas = new ReglasTransicion();
        return new CatalogoEstados(List.of(
                new EstadoAplicado(reglas),
                new EstadoEntrevista(reglas),
                new EstadoPruebaTecnica(reglas),
                new EstadoReferencia(reglas),
                new EstadoOferta(reglas),
                new EstadoContratado(reglas),
                new EstadoRechazado(reglas)
        ));
    }

    public static CodigoEstado codigo(String valor) {
        return new CodigoEstado(valor);
    }
}

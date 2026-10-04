package com.hirecore.hirecore.historial;

import com.hirecore.hirecore.dominio.MementoCandidato;

public interface HistorialCambios {

    void guardar(MementoCandidato memento);

    MementoCandidato extraerUltimo(String candidatoId);

    int tamanio(String candidatoId);
}

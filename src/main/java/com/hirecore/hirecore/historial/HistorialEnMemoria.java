package com.hirecore.hirecore.historial;

import com.hirecore.hirecore.dominio.MementoCandidato;
import com.hirecore.hirecore.dominio.excepcion.HistorialVacio;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HistorialEnMemoria implements HistorialCambios {

    private final Map<String, Deque<MementoCandidato>> pilas = new ConcurrentHashMap<>();

    @Override
    public void guardar(MementoCandidato memento) {
        Objects.requireNonNull(memento, "memento");
        pilas.computeIfAbsent(memento.obtenerCandidatoId(), id -> new ArrayDeque<>()).push(memento);
    }

    @Override
    public MementoCandidato extraerUltimo(String candidatoId) {
        Deque<MementoCandidato> pila = pilas.get(candidatoId);
        if (pila == null || pila.isEmpty()) {
            throw new HistorialVacio(candidatoId);
        }
        return pila.pop();
    }

    @Override
    public int tamanio(String candidatoId) {
        Deque<MementoCandidato> pila = pilas.get(candidatoId);
        return pila == null ? 0 : pila.size();
    }
}

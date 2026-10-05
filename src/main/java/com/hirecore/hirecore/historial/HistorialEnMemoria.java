package com.hirecore.hirecore.historial;

import com.hirecore.hirecore.dominio.MementoCandidato;
import com.hirecore.hirecore.dominio.excepcion.DeshacerNoPermitido;
import com.hirecore.hirecore.dominio.excepcion.HistorialVacio;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class HistorialEnMemoria implements HistorialCambios {

    private final Map<String, Deque<MementoCandidato>> pilas = new ConcurrentHashMap<>();
    private final Set<String> deshechos = ConcurrentHashMap.newKeySet();

    @Override
    public void guardar(MementoCandidato memento) {
        Objects.requireNonNull(memento, "memento");
        String candidatoId = memento.obtenerCandidatoId();
        pilas.computeIfAbsent(candidatoId, id -> new ArrayDeque<>()).push(memento);
        deshechos.remove(candidatoId);
    }

    @Override
    public MementoCandidato extraerUltimo(String candidatoId) {
        Objects.requireNonNull(candidatoId, "candidatoId");
        if (deshechos.contains(candidatoId)) {
            throw new DeshacerNoPermitido(candidatoId);
        }
        Deque<MementoCandidato> pila = pilas.get(candidatoId);
        if (pila == null || pila.isEmpty()) {
            throw new HistorialVacio(candidatoId);
        }
        deshechos.add(candidatoId);
        return pila.pop();
    }

    @Override
    public int tamanio(String candidatoId) {
        Deque<MementoCandidato> pila = pilas.get(candidatoId);
        return pila == null ? 0 : pila.size();
    }
}

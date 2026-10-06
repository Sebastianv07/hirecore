package com.hirecore.hirecore.dominio;

import com.hirecore.hirecore.dominio.estado.EstadoCandidato;
import com.hirecore.hirecore.dominio.evento.CambioRevertido;
import com.hirecore.hirecore.dominio.evento.EstadoCambiado;
import com.hirecore.hirecore.dominio.evento.EventoDominio;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Candidato {

    private final String id;
    private String nombre;
    private EstadoCandidato estado;

    public Candidato(String id, String nombre, EstadoCandidato estadoInicial) {
        this.id = Objects.requireNonNull(id, "id");
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.estado = Objects.requireNonNull(estadoInicial, "estadoInicial");
    }

    public String obtenerId() {
        return id;
    }

    public String obtenerNombre() {
        return nombre;
    }

    public EstadoCandidato obtenerEstado() {
        return estado;
    }

    public List<EventoDominio> transicionarA(EstadoCandidato destino, String autor) {
        Objects.requireNonNull(destino, "destino");
        Objects.requireNonNull(autor, "autor");
        EstadoCandidato anterior = estado;
        estado = anterior.transicionarA(destino);
        List<EventoDominio> eventos = new ArrayList<>();
        eventos.add(new EstadoCambiado(id, anterior.codigo(), estado.codigo(), autor));
        eventos.addAll(estado.alEntrar(this, autor));
        return List.copyOf(eventos);
    }

    public MementoCandidato crearMemento() {
        return new MementoCandidato(id, nombre, estado);
    }

    public List<EventoDominio> restaurar(MementoCandidato memento, String autor) {
        Objects.requireNonNull(memento, "memento");
        Objects.requireNonNull(autor, "autor");
        if (!id.equals(memento.obtenerCandidatoId())) {
            throw new IllegalArgumentException(
                    "El memento pertenece al candidato '%s', no a '%s'".formatted(memento.obtenerCandidatoId(), id)
            );
        }
        EstadoCandidato deshecho = estado;
        nombre = memento.obtenerNombre();
        estado = memento.obtenerEstado();
        return List.of(new CambioRevertido(id, deshecho.codigo(), estado.codigo(), autor));
    }

    @Override
    public String toString() {
        return "Candidato{id='%s', nombre='%s', estado=%s}".formatted(id, nombre, estado.codigo());
    }
}

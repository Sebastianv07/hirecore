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
    private final String nombre;
    private EstadoCandidato estado;
    private final List<EventoDominio> eventos = new ArrayList<>();

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

    public void transicionarA(EstadoCandidato destino, String autor) {
        Objects.requireNonNull(destino, "destino");
        Objects.requireNonNull(autor, "autor");
        EstadoCandidato anterior = estado;
        estado = anterior.transicionarA(destino);
        registrarEvento(new EstadoCambiado(id, anterior.codigo(), estado.codigo(), autor));
        estado.alEntrar(this, autor);
    }

    public MementoCandidato crearMemento() {
        return new MementoCandidato(id, estado);
    }

    public void restaurar(MementoCandidato memento, String autor) {
        Objects.requireNonNull(memento, "memento");
        Objects.requireNonNull(autor, "autor");
        if (!id.equals(memento.obtenerCandidatoId())) {
            throw new IllegalArgumentException(
                    "El memento pertenece al candidato '%s', no a '%s'".formatted(memento.obtenerCandidatoId(), id)
            );
        }
        EstadoCandidato deshecho = estado;
        estado = memento.obtenerEstado();
        registrarEvento(new CambioRevertido(id, deshecho.codigo(), estado.codigo(), autor));
    }

    public void registrarEvento(EventoDominio evento) {
        eventos.add(Objects.requireNonNull(evento, "evento"));
    }

    public List<EventoDominio> extraerEventos() {
        List<EventoDominio> pendientes = List.copyOf(eventos);
        eventos.clear();
        return pendientes;
    }

    @Override
    public String toString() {
        return "Candidato{id='%s', nombre='%s', estado=%s}".formatted(id, nombre, estado.codigo());
    }
}

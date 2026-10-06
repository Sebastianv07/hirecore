package com.hirecore.hirecore.comando;

import com.hirecore.hirecore.dominio.Candidato;
import com.hirecore.hirecore.dominio.CodigoEstado;
import com.hirecore.hirecore.dominio.estado.CatalogoEstados;
import com.hirecore.hirecore.dominio.evento.EventoDominio;

import java.util.List;
import java.util.Objects;

public class CambiarEstadoCommand implements ComandoCandidato {

    private final Candidato candidato;
    private final CodigoEstado destino;
    private final String autor;
    private final CatalogoEstados catalogo;

    public CambiarEstadoCommand(Candidato candidato, CodigoEstado destino, String autor, CatalogoEstados catalogo) {
        this.candidato = Objects.requireNonNull(candidato, "candidato");
        this.destino = Objects.requireNonNull(destino, "destino");
        this.autor = Objects.requireNonNull(autor, "autor");
        this.catalogo = Objects.requireNonNull(catalogo, "catalogo");
    }

    @Override
    public Candidato candidato() {
        return candidato;
    }

    @Override
    public List<EventoDominio> ejecutar() {
        return candidato.transicionarA(catalogo.obtener(destino), autor);
    }
}

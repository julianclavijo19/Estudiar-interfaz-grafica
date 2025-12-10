package com.ejemplo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un equipo dentro del torneo.
 * Adaptado para funcionar sin dependencias de Swing ni consola.
 * Toda la interacción (formularios, alertas, etc.) debe hacerse desde la interfaz JavaFX.
 */
public class Equipo {

    private String nombre;
    private List<Jugador> jugadores;
    private int puntos;
    private int golesFavor;
    private int golesContra;
    private int partidosJugados;
    private int ganados;
    private int empatados;
    private int perdidos;
    private String nombreGrupo;

    // -----------------------------
    // 🏗️ Constructores
    // -----------------------------
    public Equipo(String nombre) {
        setNombre(nombre);
        this.jugadores = new ArrayList<>();
        this.puntos = 0;
        this.golesFavor = 0;
        this.golesContra = 0;
        this.partidosJugados = 0;
        this.ganados = 0;
        this.empatados = 0;
        this.perdidos = 0;
    }

    // -----------------------------
    // ⚙️ Getters / Setters
    // -----------------------------
    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("El nombre del equipo debe contener solo letras y no estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public List<Jugador> getJugadores() { return jugadores; }

    public void setJugadores(List<Jugador> jugadores) {
        if (jugadores == null || jugadores.isEmpty()) {
            throw new IllegalArgumentException("El equipo debe tener al menos un jugador.");
        }
        this.jugadores = jugadores;
    }

    public int getPuntos() { return puntos; }

    public void setPuntos(int puntos) {
        if (puntos < 0) throw new IllegalArgumentException("Los puntos no pueden ser negativos.");
        this.puntos = puntos;
    }

    public int getGolesFavor() { return golesFavor; }

    public void setGolesFavor(int golesFavor) {
        if (golesFavor < 0) throw new IllegalArgumentException("Los goles a favor no pueden ser negativos.");
        this.golesFavor = golesFavor;
    }

    public int getGolesContra() { return golesContra; }

    public void setGolesContra(int golesContra) {
        if (golesContra < 0) throw new IllegalArgumentException("Los goles en contra no pueden ser negativos.");
        this.golesContra = golesContra;
    }

    public int getPartidosJugados() { return partidosJugados; }

    public void setPartidosJugados(int partidosJugados) {
        if (partidosJugados < 0) throw new IllegalArgumentException("El número de partidos no puede ser negativo.");
        this.partidosJugados = partidosJugados;
    }

    public int getGanados() { return ganados; }

    public void setGanados(int ganados) {
        if (ganados < 0) throw new IllegalArgumentException("Los partidos ganados no pueden ser negativos.");
        this.ganados = ganados;
    }

    public int getEmpatados() { return empatados; }

    public void setEmpatados(int empatados) {
        if (empatados < 0) throw new IllegalArgumentException("Los partidos empatados no pueden ser negativos.");
        this.empatados = empatados;
    }

    public int getPerdidos() { return perdidos; }

    public void setPerdidos(int perdidos) {
        if (perdidos < 0) throw new IllegalArgumentException("Los partidos perdidos no pueden ser negativos.");
        this.perdidos = perdidos;
    }
    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    // -----------------------------
    // 👕 Métodos de jugadores
    // -----------------------------
    /**
     * Verifica si ya existe un número de camiseta en el equipo.
     */
    public boolean existeNumeroCamiseta(int numero) {
        for (Jugador j : jugadores) {
            if (j.getNumero() == numero) return true;
        }
        return false;
    }

    /**
     * Agrega un jugador al equipo validando límites y duplicados.
     * @throws IllegalArgumentException si el jugador es inválido o ya existe el número.
     */
    public void agregarJugador(Jugador jugador) {
        if (jugador == null) {
            throw new IllegalArgumentException("Jugador nulo.");
        }

        if (jugadores.size() >= 12) {
            throw new IllegalStateException("El equipo " + nombre + " ya tiene el máximo de 12 jugadores.");
        }

        if (existeNumeroCamiseta(jugador.getNumero())) {
            throw new IllegalArgumentException("El número " + jugador.getNumero() + " ya está en uso en este equipo.");
        }

        jugadores.add(jugador);
    }

    /**
     * Devuelve una lista de cadenas con la información de cada jugador.
     * Ideal para mostrar en la interfaz JavaFX.
     */
    public List<String> obtenerListaJugadores() {
        List<String> lista = new ArrayList<>();
        if (jugadores.isEmpty()) {
            lista.add("(Sin jugadores registrados)");
        } else {
            for (Jugador j : jugadores) {
                lista.add(j.toString());
            }
        }
        return lista;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

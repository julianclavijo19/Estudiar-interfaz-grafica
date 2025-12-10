package com.ejemplo.model;

/**
 * Clase auxiliar para representar una fila en la tabla de posiciones.
 */
public class PosicionTabla {

    // Asumimos que la lista ya está ordenada, el atributo es el número de fila.
    private final int posicion; 
    private final Equipo equipo; 
    private final int partidosJugados;
    private final int ganados;
    private final int empatados;
    private final int perdidos;
    private final int golesFavor;
    private final int golesContra;
    private final int diferenciaGoles;
    private final int puntos;

    public PosicionTabla(int posicion, Equipo equipo, int pj, int pg, int pe, int pp, int gf, int gc, int pts) {
        this.posicion = posicion;
        this.equipo = equipo;
        this.partidosJugados = pj;
        this.ganados = pg;
        this.empatados = pe;
        this.perdidos = pp;
        this.golesFavor = gf;
        this.golesContra = gc;
        this.diferenciaGoles = gf - gc; // GD
        this.puntos = pts;
    }

    // 🚨 GETTERS REQUERIDOS por tu código de tabla:
    public int getPosicion() { return posicion; }
    public String getNombre() { return equipo.getNombre(); } // Llama al nombre del equipo
    public int getPartidosJugados() { return partidosJugados; }
    public int getPartidosGanados() { return ganados; }
    public int getPartidosEmpatados() { return empatados; }
    public int getPartidosPerdidos() { return perdidos; }
    public int getGolesFavor() { return golesFavor; }
    public int getGolesContra() { return golesContra; }
    
    // Y además necesitas estos para que la tabla sea completa:
    public int getDiferenciaGoles() { return diferenciaGoles; }
    public int getPuntos() { return puntos; }
}
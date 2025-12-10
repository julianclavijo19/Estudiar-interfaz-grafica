package com.ejemplo.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Representa un partido entre dos equipos.
 * No depende de Swing ni consola, totalmente compatible con JavaFX.
 */
public class Partido {

    private Equipo equipoLocal;
    private Equipo equipoVisitante;
    private int golesLocal;
    private int golesVisitante;
    private boolean jugado;
    private LocalDateTime fechaHora;
    private Equipo equipo;

    private final Map<String, Integer> golesPorJugador = new HashMap<>();
    private final List<String> eventos = new ArrayList<>();

    private Integer penalesLocal;
    private Integer penalesVisitante;
    private Equipo ganadorPorPenales;

    // -----------------------------
    // 🏗️ Constructores
    // -----------------------------
    public Partido(Equipo equipoLocal, Equipo equipoVisitante, LocalDateTime fechaHora) {
        if (equipoLocal == null || equipoVisitante == null)
            throw new IllegalArgumentException("Los equipos no pueden ser nulos.");
        if (equipoLocal.equals(equipoVisitante))
            throw new IllegalArgumentException("Un equipo no puede jugar contra sí mismo.");

        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fechaHora = fechaHora;
        this.golesLocal = 0;
        this.golesVisitante = 0;
        this.jugado = false;
    }

    public Partido(Equipo local, Equipo visitante) {
        this(local, visitante, null);
    }

    // -----------------------------
    // ⚙️ Getters y Setters
    // -----------------------------
    public Equipo getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(Equipo equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public Equipo getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(Equipo equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public int getGolesLocal() {
        return golesLocal;
    }

    public void setGolesLocal(int golesLocal) {
        this.golesLocal = Math.max(0, golesLocal);
    }

    public int getGolesVisitante() {
        return golesVisitante;
    }

    public void setGolesVisitante(int golesVisitante) {
        this.golesVisitante = Math.max(0, golesVisitante);
    }

    public boolean getJugado() {
        return jugado;
    }

    public void setJugado(boolean jugado) {
        this.jugado = jugado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public Integer getPenalesLocal() {
        return penalesLocal;
    }

    public Integer getPenalesVisitante() {
        return penalesVisitante;
    }

    public Equipo getGanadorPorPenales() {
        return ganadorPorPenales;
    }

    public Map<String, Integer> getGolesPorJugador() {
        return Collections.unmodifiableMap(golesPorJugador);
    }
    
    /**
     * Limpia todos los goles registrados por jugador.
     * Útil cuando se quiere resetear el registro de goles.
     */
    public void limpiarGolesPorJugador() {
        golesPorJugador.clear();
    }

    public List<String> getEventos() {
        return Collections.unmodifiableList(eventos);
    }

 


    // -----------------------------
    // ⚽️ Lógica del partido
    // -----------------------------
    public boolean ganoLocal() {
        return jugado && golesLocal > golesVisitante;
    }

    public boolean ganoVisitante() {
        return jugado && golesVisitante > golesLocal;
    }

    public boolean esEmpate() {
        return jugado && golesLocal == golesVisitante;
    }

    public void registrarResultados(int golesLocal, int golesVisitante) {
        if (golesLocal < 0 || golesVisitante < 0)
            throw new IllegalArgumentException("Los goles no pueden ser negativos.");
        this.golesLocal = golesLocal;
        this.golesVisitante = golesVisitante;
        this.jugado = true;
        eventos.add("Resultado final: " + equipoLocal.getNombre() + " " + golesLocal +
                " - " + golesVisitante + " " + equipoVisitante.getNombre());
    }

    public void setResultadoPenales(int penalesLocal, int penalesVisitante, Equipo ganador) {
        if (ganador == null)
            throw new IllegalArgumentException("Debe especificar el equipo ganador en penales.");
        this.penalesLocal = penalesLocal;
        this.penalesVisitante = penalesVisitante;
        this.ganadorPorPenales = ganador;
        eventos.add("Definición por penales → Ganador: " + ganador.getNombre());
    }

    // -----------------------------
    // ⚽️ Registro de goles y tarjetas
    // -----------------------------
  // Archivo: Partido.java (Método mejorado)

public void agregarGol(Equipo equipo, Jugador jugador) {
    if (jugador == null)
        throw new IllegalArgumentException("El jugador no puede ser nulo.");

    // ⭐ MEJORA: Llama al método del Jugador para incrementar su cuenta de goles.
    jugador.anotarGol(); 

    if (equipo.equals(equipoLocal))
        golesLocal++;
    else if (equipo.equals(equipoVisitante))
        golesVisitante++;
    else
        throw new IllegalArgumentException("El jugador no pertenece a ninguno de los equipos del partido.");

    String clave = jugador.getNombre() + " (" + equipo.getNombre() + ")";
    golesPorJugador.put(clave, golesPorJugador.getOrDefault(clave, 0) + 1);
    eventos.add("⚽ Gol de " + clave);
}

    public void agregarTarjeta(Equipo equipo, Jugador jugador, String tipo) {
        if (jugador == null)
            throw new IllegalArgumentException("Jugador nulo al registrar tarjeta.");
        if (!equipo.equals(equipoLocal) && !equipo.equals(equipoVisitante))
            throw new IllegalArgumentException("El jugador no pertenece a ninguno de los equipos del partido.");

        String mensaje;
        if (tipo.equalsIgnoreCase("Amarilla")) {
            mensaje = jugador.agregarTarjetaAmarilla();
            eventos.add("🟨 Amarilla: " + jugador.getNombre() + " (" + equipo.getNombre() + ")");
        } else if (tipo.equalsIgnoreCase("Roja")) {
            mensaje = jugador.agregarTarjetaRoja();
            eventos.add("🟥 Roja: " + jugador.getNombre() + " (" + equipo.getNombre() + ")");
        } else {
            throw new IllegalArgumentException("Tipo de tarjeta inválido: " + tipo);
        }

        // Puedes mostrar el mensaje devuelto en la UI (DialogosFX)
        System.out.println(mensaje); // ← opcional: eliminar si prefieres no loguear en consola
    }

    // -----------------------------
    // 🏆 Ganador y perdedor
    // -----------------------------
    public Equipo getGanador() {
        if (golesLocal > golesVisitante)
            return equipoLocal;
        if (golesVisitante > golesLocal)
            return equipoVisitante;
        if (ganadorPorPenales != null)
            return ganadorPorPenales;
        return null;
    }

    public Equipo getPerdedor() {
        Equipo ganador = getGanador();
        if (ganador == null)
            return null;
        return (ganador.equals(equipoLocal)) ? equipoVisitante : equipoLocal;
    }

    public void setFecha(String fechaTexto) {
        if (fechaTexto == null || fechaTexto.isEmpty()) {
            throw new IllegalArgumentException("La fecha no puede estar vacía");
        }

        // Normalizar el formato: agregar ceros a la izquierda si faltan
        fechaTexto = fechaTexto.trim();
        
        // Intentar parsear con diferentes formatos
        DateTimeFormatter[] formatos = {
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),      // Formato estándar: 2025-12-09 21:10
            DateTimeFormatter.ofPattern("yyyy-M-d HH:mm"),        // Formato flexible: 2025-12-9 21:10
            DateTimeFormatter.ofPattern("yyyy-MM-d HH:mm"),       // Formato mixto: 2025-12-9 21:10
            DateTimeFormatter.ofPattern("yyyy-M-dd HH:mm")        // Formato mixto: 2025-12-09 21:10
        };
        
        LocalDateTime fecha = null;
        Exception ultimoError = null;
        
        for (DateTimeFormatter formatter : formatos) {
            try {
                fecha = LocalDateTime.parse(fechaTexto, formatter);
                break; // Si se parsea correctamente, salir del bucle
            } catch (Exception e) {
                ultimoError = e;
            }
        }
        
        if (fecha == null) {
            throw new IllegalArgumentException(
                "Formato de fecha inválido. Use: YYYY-MM-DD HH:MM (ej: 2025-12-09 21:10 o 2025-12-9 21:10). " +
                "Error: " + (ultimoError != null ? ultimoError.getMessage() : "Formato no reconocido"));
        }
        
        this.fechaHora = fecha;
    }


    // ✅ Opción para mostrar la fecha si lo necesitas
    public String getFechaFormateada() {
    if (fechaHora == null)
        return "Sin programar";
    return fechaHora.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
}


    // -----------------------------
    // 🧾 Representación textual
    // -----------------------------
    public String resumen() {
        if (!jugado) {
            return String.format("%s vs %s (Pendiente %s)",
                    equipoLocal.getNombre(),
                    equipoVisitante.getNombre(),
                    fechaHora != null ? fechaHora : "sin fecha");
        }

        String marcador = String.format("%s %d - %d %s",
                equipoLocal.getNombre(), golesLocal, golesVisitante, equipoVisitante.getNombre());

        if (penalesLocal != null && penalesVisitante != null) {
            marcador += String.format(" (Penales: %d-%d, Ganador: %s)",
                    penalesLocal, penalesVisitante, ganadorPorPenales != null ? ganadorPorPenales.getNombre() : "?");
        }

        return marcador + " [" + (fechaHora != null ? fechaHora : "sin fecha") + "]";
    }

    @Override
    public String toString() {
       return equipoLocal.getNombre() + " vs " + equipoVisitante.getNombre();
    }

    // -----------------------------
    // 🔁 Equals / HashCode
    // -----------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Partido))
            return false;
        Partido partido = (Partido) o;
        return Objects.equals(equipoLocal, partido.equipoLocal)
                && Objects.equals(equipoVisitante, partido.equipoVisitante)
                && Objects.equals(fechaHora, partido.fechaHora);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equipoLocal, equipoVisitante, fechaHora);
    }

}

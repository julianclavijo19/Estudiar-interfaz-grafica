package com.ejemplo.model;

import java.util.Objects;

/**
 * Representa a un jugador de un equipo dentro del torneo.
 * Esta clase no depende de Swing ni de consola; está lista para usarse con
 * JavaFX.
 */
public class Jugador {

    private String nombre;
    private int edad;
    private Posicion posicion;
    private int numero;
    private int goles;
    private Equipo equipo; // El equipo al que pertenece el jugador

    // 🟨 Tarjetas y suspensión (Acumuladas durante el torneo/fase)
    private int tarjetasAmarillasAcumuladas; // Acumulación para suspensión (ej. 2 = suspensión)
    private int tarjetasRojasAcumuladas; // Total de rojas directas en el torneo
    private boolean suspendido;

    // -----------------------------
    // 🏗️ Constructores
    // -----------------------------
    // Constructor vacío
public Jugador() {
        this.goles = 0;
        this.tarjetasAmarillasAcumuladas = 0;
        this.tarjetasRojasAcumuladas = 0;
        this.suspendido = false;
    }

    // 2. Constructor Completo (Principal y más robusto)
    public Jugador(String nombre, int edad, Posicion posicion, int numero, Equipo equipo) {
        // Llamada a setters con validación
        setNombre(nombre);
        setEdad(edad);
        setPosicion(posicion);
        setNumero(numero);
        setEquipo(equipo); 
        
        // Inicialización de estadísticas
        this.goles = 0;
        this.tarjetasAmarillasAcumuladas = 0;
        this.tarjetasRojasAcumuladas = 0;
        this.suspendido = false;
    }

    // 3. Constructor que acepta String para Posicion (Delegación correcta)
    public Jugador(String nombre, int edad, String posicion, int numero, Equipo equipo) {
        this(nombre, edad, Posicion.valueOf(posicion.toUpperCase()), numero, equipo);
    }
    // -----------------------------
    // ⚙️ Goles
    // -----------------------------
    public int getGoles() {
        return goles;
    }

    public void setGoles(int goles) {
        if (goles < 0)
            throw new IllegalArgumentException("Los goles no pueden ser negativos.");
        this.goles = goles;
    }

    public void anotarGol() {
        this.goles++;
    }

    public void quitarGol() {
        if (this.goles > 0) {
            this.goles--;
        }
    }

    // -----------------------------
    // ⚙️ Equipo
    // -----------------------------
    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    // -----------------------------
    // 🟨 Tarjetas y suspensión
    // -----------------------------
    /**
     * Agrega una tarjeta amarilla y verifica si el jugador debe ser suspendido.
     * 
     * @return Mensaje descriptivo del nuevo estado.
     */
    public String agregarTarjetaAmarilla() {
        tarjetasAmarillasAcumuladas++;

        // Lógica de suspensión: 2 amarillas acumuladas
        if (tarjetasAmarillasAcumuladas >= 2) {
            suspendido = true;
            // Se resetean las amarillas que causaron la suspensión (suspensión por
            // acumulación)
            tarjetasAmarillasAcumuladas = 0;
            return "¡SUSPENSIÓN! " + nombre + " ha sido suspendido por acumulación de amarillas.";
        }
        return nombre + " recibió una tarjeta amarilla (Acumula: " + tarjetasAmarillasAcumuladas + ").";
    }

    /**
     * Agrega una tarjeta roja al jugador (expulsión).
     * 
     * @return Mensaje descriptivo del nuevo estado.
     */
    public String agregarTarjetaRoja() {
        tarjetasRojasAcumuladas++;
        suspendido = true;
        // Las amarillas acumuladas no se resetean a menos que sea el fin de una
        // fase/torneo
        return "¡EXPULSIÓN! " + nombre + " recibió una tarjeta roja directa y está suspendido para el próximo partido.";
    }

    /**
     * Marca que el jugador cumplió su suspensión y vuelve a estar disponible.
     * Esto generalmente ocurre al inicio del siguiente partido.
     * 
     * @return Mensaje descriptivo.
     */
    public String cumplirSuspension() {
        if (suspendido) {
            suspendido = false;
            return nombre + " ha cumplido su suspensión y puede volver a jugar.";
        }
        return nombre + " no estaba suspendido.";
    }

    // -----------------------------
    // ⚙️ Getters de Tarjetas/Suspensión
    // -----------------------------
    public boolean estaSuspendido() {
        return suspendido;
    }

    public int getTarjetasAmarillasAcumuladas() {
        return tarjetasAmarillasAcumuladas;
    }

    // Nota: El método setTarjetasAmarillasAcumuladas es útil para reiniciar el
    // conteo.
    public void setTarjetasAmarillasAcumuladas(int tarjetasAmarillasAcumuladas) {
        this.tarjetasAmarillasAcumuladas = Math.max(0, tarjetasAmarillasAcumuladas);
    }

    public int getTarjetasRojasAcumuladas() {
        return tarjetasRojasAcumuladas;
    }

    // Nota: El método setTarjetasRojasAcumuladas es útil para reiniciar el conteo.
    public void setTarjetasRojasAcumuladas(int tarjetasRojasAcumuladas) {
        this.tarjetasRojasAcumuladas = Math.max(0, tarjetasRojasAcumuladas);
    }

    // -----------------------------
    // 👤 Datos personales
    // -----------------------------
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()
                || !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException("El nombre no puede estar vacío ni contener símbolos o números.");
        }
        this.nombre = nombre.trim();
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < 12 || edad > 60)
            throw new IllegalArgumentException("La edad debe estar entre 12 y 60 años.");
        this.edad = edad;
    }

    public Posicion getPosicion() {
        return posicion;
    }

    public void setPosicion(Posicion posicion) {
        if (posicion == null)
            throw new IllegalArgumentException("La posición no puede ser nula.");
        this.posicion = posicion;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        if (numero <= 0 || numero > 99)
            throw new IllegalArgumentException("El número de camiseta debe estar entre 1 y 99.");
        this.numero = numero;
    }

    // -----------------------------
    // 🧾 Representación textual
    // -----------------------------
    @Override
    public String toString() {
        String estado = suspendido ? "❌ Suspendido" : "✅ Activo";
        return String.format("%s (#%d) - %s | Goles: %d | Amarillas Acum.: %d | Rojas Acum.: %d | %s",
                nombre, numero, posicion, goles, tarjetasAmarillasAcumuladas, tarjetasRojasAcumuladas, estado);
    }

    // -----------------------------
    // 🔁 Equals / HashCode (Opcional, pero recomendado)
    // -----------------------------
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Jugador jugador = (Jugador) o;
        return numero == jugador.numero && edad == jugador.edad && Objects.equals(nombre, jugador.nombre)
                && Objects.equals(equipo, jugador.equipo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, numero, equipo);
    }
}
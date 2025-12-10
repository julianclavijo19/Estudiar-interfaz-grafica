package com.ejemplo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ejemplo.model.Equipo;
import com.ejemplo.model.Jugador;
import com.ejemplo.model.Posicion;
import com.ejemplo.model.Torneo;

public class DatosIniciales {

    private static final String[] NOMBRES_EQUIPOS = {
            "Leones FC", "Tigres Dorados", "Águilas Rojas", "Pumas Verdes",
            "Toros del Norte", "Halcones Azules", "Lobos del Sur", "Cóndores FC",
            "Truenos FC", "Piratas del Mar", "Gladiadores", "Panteras Negras"
    };

    private static final String[] NOMBRES_JUGADORES = {
            "Juan", "Carlos", "Andrés", "Felipe", "Luis", "Diego", "Santiago",
            "Camilo", "Mateo", "Julián", "David", "Sebastián", "Oscar", "Esteban",
            "Mauricio", "Kevin", "Leonardo", "Nicolás", "Daniel", "Pedro"
    };

    private static final String[] APELLIDOS_JUGADORES = {
            "Pérez", "Gómez", "Rodríguez", "Martínez", "Hernández", "López", "García",
            "Morales", "Castro", "Díaz", "Ramírez", "Torres", "Rojas", "Suárez", "Vargas"
    };

    private static final Random random = new Random();

    /**
     * Genera 12 equipos con 5 jugadores cada uno y los agrega al torneo.
     */
    public static void cargarEquiposYJugadores(Torneo torneo) {
        // Obtenemos un array de todas las posiciones disponibles
        Posicion[] posiciones = Posicion.values(); 
        
        for (String nombreEquipo : NOMBRES_EQUIPOS) {
            Equipo equipo = new Equipo(nombreEquipo);

            // Generar jugadores aleatorios
            List<Jugador> jugadores = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                String nombre = NOMBRES_JUGADORES[random.nextInt(NOMBRES_JUGADORES.length)];
                String apellido = APELLIDOS_JUGADORES[random.nextInt(APELLIDOS_JUGADORES.length)];
                int edad = 18 + random.nextInt(15); // entre 18 y 32 años
                
                // 🛑 CORRECCIÓN: Usamos el constructor vacío y setters para asignar
                // todos los campos obligatorios.
                Jugador j = new Jugador();
                j.setNombre(nombre + " " + apellido);
                j.setEdad(edad);
                
                // Asignación aleatoria de Posición y Número (ahora obligatorio)
                j.setPosicion(posiciones[random.nextInt(posiciones.length)]);
                j.setNumero(1 + random.nextInt(99)); 
                
                // ¡CRUCIAL!: Asignar el equipo. Esto hace que getEquipo() != null
                // y resuelve la validación en VistaPartidos.java
                j.setEquipo(equipo); 

                jugadores.add(j);
            }

            equipo.setJugadores(jugadores);
            torneo.agregarEquipo(equipo);
        }
    }
}
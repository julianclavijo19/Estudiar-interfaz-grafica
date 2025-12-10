package com.ejemplo.controller;

import java.util.List;
import java.util.Map;

import com.ejemplo.model.Equipo;
import com.ejemplo.model.Partido;
import com.ejemplo.model.Torneo;

public class PartidosController {

    private final Torneo torneo;

    public PartidosController(Torneo torneo) {
        this.torneo = torneo;
    }

    /**
     * Llama a la lógica de sorteo de la Fase de Grupos en la clase Torneo.
     * Modificado a 'void' para coincidir con la llamada en VistaPartidos.java.
     */
    public void sortearFaseGrupos() { // ⭐ CORRECCIÓN: Cambiado de Map a void
        torneo.crearGrupos(); 
        // Se elimina el 'return torneo.getGrupos();'
    }

    /**
     * Programa la fecha y hora de un partido EXISTENTE.
     * @param partido El objeto Partido seleccionado del ComboBox.
     * @param fecha La fecha y hora a asignar.
     */
    public void programarPartido(Partido partido, String fecha) {
        torneo.programarPartido(partido, fecha); 
    }

    /**
     * Registra el resultado de un partido y actualiza las estadísticas de los equipos.
     * @param partido El objeto Partido seleccionado.
     * @param golesLocal Goles del equipo local.
     * @param golesVisitante Goles del equipo visitante.
     */
    public void registrarResultado(Partido partido, int golesLocal, int golesVisitante) {
        // NOTA: Asegúrate de que este método en Torneo.java maneje el registro de
        // goles en el partido Y la actualización de estadísticas en los equipos.
        torneo.registrarResultado(partido, golesLocal, golesVisitante);
    }

    /**
     * Obtiene la lista de todos los partidos generados en el torneo.
     * @return Lista de partidos.
     */
    public List<Partido> obtenerPartidos() {
        return torneo.getPartidos();
    }
    
    /**
     * Obtiene el mapa de grupos (para la vista de partidos por grupo).
     * @return Mapa con el nombre del grupo y la lista de equipos.
     */
    public Map<String, List<Equipo>> obtenerGrupos() {
        return torneo.getGrupos();
    }
}
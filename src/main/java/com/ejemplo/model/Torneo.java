package com.ejemplo.model;

import java.util.*;
import javax.swing.JOptionPane;

public class Torneo {

    private String nombre;
    private final List<Equipo> equipos = new ArrayList<>();
    private final List<Partido> partidos = new ArrayList<>();
    private Map<String, List<Equipo>> grupos = new LinkedHashMap<>();
    private Eliminatoria eliminatoria = new Eliminatoria();

    // ❌ ELIMINADO: private String nombreGrupo; (Incorrecto aquí)

    private boolean sorteoRealizado = false;
    private boolean partidosGenerados = false;
    private boolean gruposGenerados = false;
    private boolean cuartosGenerados = false;

    public Torneo(String nombre) {
        setNombre(nombre);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty() ||
                !nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            throw new IllegalArgumentException(
                    "El nombre del torneo debe contener solo letras y no puede estar vacío.");
        }
        this.nombre = nombre.trim();
    }

    public List<Equipo> getEquipos() {
        return equipos;
    }

    public List<Partido> getPartidos() {
        return partidos;
    }

    public Map<String, List<Equipo>> getGrupos() {
        return grupos;
    }

    public void setGrupos(Map<String, List<Equipo>> grupos) {
        this.grupos = grupos;
    }

    // ❌ ELIMINADOS: getNombreGrupo() y setNombreGrupo() (Pertenecen a la clase Equipo)

    public boolean puedeIniciar() {
        return equipos.size() >= 8;
    }

    // ---------------- EQUIPOS ----------------
    public void agregarEquipo(Equipo equipo) {
        if (equipos.stream().anyMatch(e -> e.getNombre().equalsIgnoreCase(equipo.getNombre()))) {
            throw new IllegalArgumentException("Ya existe un equipo con ese nombre");
        }
        equipos.add(equipo);
    }

    public void agregarEquipo(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacío");
        }
        agregarEquipo(new Equipo(nombre));
    }

    public void agregarJugadorEquipo(Equipo equipo, Jugador jugador) {
        if (equipo != null) {
            equipo.agregarJugador(jugador);
        } else {
            throw new IllegalArgumentException("El equipo no puede ser nulo");
        }
    }

    // ---------------- GRUPOS ----------------
    public void crearGrupos() {
        if (sorteoRealizado) {
            throw new IllegalStateException("⚠️ El sorteo ya fue realizado. No se puede repetir.");
        }

        if (equipos.size() < 12) {
            throw new IllegalStateException("Se necesitan mínimo 12 equipos para crear grupos.");
        }

        // 1. Limpiar estructuras para un nuevo sorteo
        grupos.clear();
        partidos.clear();
        eliminatoria = new Eliminatoria();
        cuartosGenerados = false;

        // 2. Copiar y mezclar los equipos para el sorteo aleatorio
        List<Equipo> copia = new ArrayList<>(equipos);
        Collections.shuffle(copia);

        int totalEquipos = copia.size();
        
        // 3. Calcular número óptimo de grupos (idealmente 3 o 4 equipos por grupo)
        // Estrategia: intentar tener grupos de 3 o 4 equipos, distribuyendo el resto
        int cantidadGrupos;
        int equiposPorGrupo;
        int equiposRestantes;
        
        // Calcular cuántos grupos necesitamos
        if (totalEquipos % 4 == 0) {
            // Divisible por 4: grupos de 4
            cantidadGrupos = totalEquipos / 4;
            equiposPorGrupo = 4;
            equiposRestantes = 0;
        } else if (totalEquipos % 3 == 0) {
            // Divisible por 3: grupos de 3
            cantidadGrupos = totalEquipos / 3;
            equiposPorGrupo = 3;
            equiposRestantes = 0;
        } else {
            // No es divisible exactamente: calcular distribución óptima
            // Intentar grupos de 4 primero
            cantidadGrupos = (totalEquipos + 3) / 4; // Redondeo hacia arriba
            equiposPorGrupo = totalEquipos / cantidadGrupos; // División base
            equiposRestantes = totalEquipos % cantidadGrupos; // Equipos que sobran
        }
        
        // 4. Crear los grupos y asignar equipos
        for (int i = 0; i < cantidadGrupos; i++) {
            String nombreGrupo = "Grupo " + (char) ('A' + i);
            List<Equipo> equiposGrupo = new ArrayList<>();
            
            // Calcular cuántos equipos van en este grupo
            // Los primeros grupos reciben un equipo extra si hay restantes
            int equiposEnEsteGrupo = equiposPorGrupo;
            if (i < equiposRestantes) {
                equiposEnEsteGrupo++; // Este grupo recibe un equipo extra
            }
            
            // Asignar equipos al grupo
            for (int j = 0; j < equiposEnEsteGrupo && !copia.isEmpty(); j++) {
                Equipo equipoAsignado = copia.remove(0);
                equipoAsignado.setNombreGrupo(nombreGrupo);
                equiposGrupo.add(equipoAsignado);
            }
            
            grupos.put(nombreGrupo, equiposGrupo);
            
            // 5. Generar partidos internos del grupo
            generarPartidosDelGrupo(equiposGrupo);
        }
        
        // 6. Verificar que todos los equipos fueron asignados
        if (!copia.isEmpty()) {
            throw new IllegalStateException("Error: Quedaron " + copia.size() + " equipos sin asignar. Esto no debería ocurrir.");
        }

        gruposGenerados = true;
        partidosGenerados = true;
        sorteoRealizado = true; // ✅ marcar que el sorteo fue hecho
    }

    // ---------------- PARTIDOS ----------------
    private void generarPartidosDelGrupo(List<Equipo> equiposGrupo) {
        for (int i = 0; i < equiposGrupo.size(); i++) {
            for (int j = i + 1; j < equiposGrupo.size(); j++) {
                Equipo local = equiposGrupo.get(i);
                Equipo visitante = equiposGrupo.get(j);

                // 👇 esto es clave: se agrega a la lista general de partidos del torneo
                partidos.add(new Partido(local, visitante));
            }
        }
    }

    public void programarPartido(Partido partido, String fechaTexto) {
        if (partido == null)
            throw new IllegalArgumentException("El partido no puede ser nulo");
        partido.setFecha(fechaTexto);
    }

    public void agregarPartido(Partido partido) {
        if (partido == null) {
            throw new IllegalArgumentException("El partido no puede ser nulo");
        }
        this.partidos.add(partido);
    }

    public void registrarResultado(Partido partido, int golesLocal, int golesVisitante) {
        if (partido == null)
            throw new IllegalArgumentException("El partido no puede ser nulo");
        
        // Registrar resultado en el partido
        partido.registrarResultados(golesLocal, golesVisitante);
        
        // Actualizar estadísticas de los equipos
        actualizarEstadisticasEquipos(partido, golesLocal, golesVisitante);
        
        // Verificar si terminó la fase de grupos y generar cuartos automáticamente
        verificarYGenerarCuartos();
    }
    
    /**
     * Actualiza las estadísticas de los equipos después de un partido.
     */
    private void actualizarEstadisticasEquipos(Partido partido, int golesLocal, int golesVisitante) {
        Equipo local = partido.getEquipoLocal();
        Equipo visitante = partido.getEquipoVisitante();
        
        // Actualizar goles
        local.setGolesFavor(local.getGolesFavor() + golesLocal);
        local.setGolesContra(local.getGolesContra() + golesVisitante);
        visitante.setGolesFavor(visitante.getGolesFavor() + golesVisitante);
        visitante.setGolesContra(visitante.getGolesContra() + golesLocal);
        
        // Actualizar partidos jugados
        local.setPartidosJugados(local.getPartidosJugados() + 1);
        visitante.setPartidosJugados(visitante.getPartidosJugados() + 1);
        
        // Actualizar puntos y resultados
        if (golesLocal > golesVisitante) {
            // Gana local
            local.setPuntos(local.getPuntos() + 3);
            local.setGanados(local.getGanados() + 1);
            visitante.setPerdidos(visitante.getPerdidos() + 1);
        } else if (golesVisitante > golesLocal) {
            // Gana visitante
            visitante.setPuntos(visitante.getPuntos() + 3);
            visitante.setGanados(visitante.getGanados() + 1);
            local.setPerdidos(local.getPerdidos() + 1);
        } else {
            // Empate
            local.setPuntos(local.getPuntos() + 1);
            visitante.setPuntos(visitante.getPuntos() + 1);
            local.setEmpatados(local.getEmpatados() + 1);
            visitante.setEmpatados(visitante.getEmpatados() + 1);
        }
    }
    
    /**
     * Verifica si todos los partidos de grupos están jugados y genera automáticamente los cuartos de final.
     */
    private void verificarYGenerarCuartos() {
        // Solo verificar si hay grupos generados y aún no se generaron los cuartos
        if (gruposGenerados && !cuartosGenerados && todosPartidosDeGruposJugados()) {
            try {
                List<Equipo> clasificados = obtenerClasificados();
                if (clasificados.size() >= 8) {
                    eliminatoria.sortearCuartos(clasificados);
                    cuartosGenerados = true;
                    // Agregar los partidos de cuartos a la lista general de partidos
                    partidos.addAll(eliminatoria.getPartidosCuartos());
                }
            } catch (Exception e) {
                // Si hay error, no lanzar excepción para no interrumpir el flujo
                System.err.println("Error al generar cuartos automáticamente: " + e.getMessage());
            }
        }
    }

    // ---------------- TABLAS ----------------
   

    public boolean todosPartidosDeGruposJugados() {
        if (grupos.isEmpty() || partidos.isEmpty())
            return false;
        
        // Verificar solo los partidos que pertenecen a grupos (no los de cuartos)
        // Un partido pertenece a grupos si ambos equipos están en algún grupo
        return partidos.stream()
            .filter(p -> perteneceAGrupo(p.getEquipoLocal()) && perteneceAGrupo(p.getEquipoVisitante()))
            .allMatch(Partido::getJugado);
    }
    
    /**
     * Verifica si un equipo pertenece a algún grupo.
     */
    private boolean perteneceAGrupo(Equipo equipo) {
        for (List<Equipo> grupo : grupos.values()) {
            if (grupo.contains(equipo)) {
                return true;
            }
        }
        return false;
    }

    public List<Equipo> obtenerClasificados() {
        List<Equipo> clasificados = new ArrayList<>();
        if (grupos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "⚠ No hay grupos generados.");
            return clasificados;
        }

        // Reunir todos los equipos de todos los grupos
        List<Equipo> todosEquipos = new ArrayList<>();
        for (List<Equipo> grupo : grupos.values()) {
            todosEquipos.addAll(grupo);
        }

        // Ordenar todos los equipos globalmente por: Puntos > Diferencia de Gol > Goles a Favor
        todosEquipos.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                                (e1.getGolesFavor() - e1.getGolesContra()));
            if (cmp == 0)
                cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
            return cmp;
        });

        // Tomar los mejores 8 equipos
        int cantidad = Math.min(8, todosEquipos.size());
        for (int i = 0; i < cantidad; i++) {
            clasificados.add(todosEquipos.get(i));
        }

        return clasificados;
    }

    public List<Equipo> clasificarOchoMejores() {
        return obtenerClasificados();
    }

    public String mostrarPartidos() {
        if (partidos.isEmpty()) {
            return "⚠️ No hay partidos generados aún.";
        }

        StringBuilder sb = new StringBuilder("📅 PARTIDOS GENERADOS:\n\n");
        for (Partido p : partidos) {
            sb.append("⚽ ")
              .append(p.getEquipoLocal().getNombre())
              .append(" vs ")
              .append(p.getEquipoVisitante().getNombre());
            if (p.getFechaHora() != null) {
                sb.append(" - Fecha: ").append(p.getFechaHora());
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public String mostrarGrupos() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Equipo>> entry : grupos.entrySet()) {
            sb.append(entry.getKey()).append(":\n");
            for (Equipo e : entry.getValue()) {
                sb.append("   - ").append(e.getNombre()).append("\n");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private Equipo buscarEquipoPorNombre(String nombre) {
        for (Equipo e : equipos) {
            if (e.getNombre().equalsIgnoreCase(nombre)) {
                return e;
            }
        }
        return null;
    }

    public List<Jugador> mostrarGoleadores() {
        List<Jugador> goleadores = new ArrayList<>();
        
        // Reunir todos los jugadores de todos los equipos
        for (Equipo e : equipos) {
            goleadores.addAll(e.getJugadores());
        }

        // Ordenar por cantidad de goles (mayor a menor)
        goleadores.sort((a, b) -> Integer.compare(b.getGoles(), a.getGoles()));

        return goleadores;
    }

    public void generarPartidosDeGrupos() {
        for (List<Equipo> grupo : grupos.values()) {
            generarPartidosDelGrupo(grupo);
        }
    }

    public boolean isSorteoRealizado() {
        return sorteoRealizado;
    }

    public void setSorteoRealizado(boolean sorteoRealizado) {
        this.sorteoRealizado = sorteoRealizado;
    }

    public Eliminatoria getEliminatoria() {
        return eliminatoria;
    }

    public void setEliminatoria(Eliminatoria eliminatoria) {
        this.eliminatoria = eliminatoria;
    }

    public boolean isCuartosGenerados() {
        return cuartosGenerados;
    }

   public Map<String, List<Equipo>> obtenerTablaGeneralPorGrupo() {
    Map<String, List<Equipo>> tablaOrdenadaPorGrupo = new LinkedHashMap<>();

    // 1. Iterar sobre cada grupo del torneo
    for (Map.Entry<String, List<Equipo>> entry : grupos.entrySet()) {
        // Creamos una copia del grupo para ordenarlo sin afectar la lista original
        List<Equipo> grupo = new ArrayList<>(entry.getValue()); 

        // 2. Lógica de ordenamiento: Puntos > Diferencia de Gol > Goles a Favor
        grupo.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
            if (cmp == 0)
                cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                                      (e1.getGolesFavor() - e1.getGolesContra()));
            if (cmp == 0)
                cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
            return cmp;
        });

        tablaOrdenadaPorGrupo.put(entry.getKey(), grupo);
    }
    return tablaOrdenadaPorGrupo;
}
}
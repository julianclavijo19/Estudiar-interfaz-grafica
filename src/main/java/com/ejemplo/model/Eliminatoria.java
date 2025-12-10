package com.ejemplo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que gestiona las fases eliminatorias de un torneo (cuartos, semifinales, final, tercer puesto).
 * Adaptada para funcionar sin dependencias de Swing ni consola.
 * Toda la interacción con el usuario se debe hacer desde la interfaz JavaFX.
 */
public class Eliminatoria {

    private final List<Partido> partidosCuartos = new ArrayList<>();
    private final List<Partido> partidosSemifinal = new ArrayList<>();
    private Partido partidoFinal;
    private Partido partidoTercerPuesto;
    private List<Partido> partidosEliminatoria;

    public Eliminatoria() {
    this.partidosEliminatoria = new ArrayList<>();
}


    public Eliminatoria(List<Partido> partidos) {
        this.partidosEliminatoria = partidos;
    }

    public List<Partido> getPartidosEliminatoria() {
        return partidosEliminatoria;
    }

    public void mostrarEliminatoria() {
        for (Partido p : partidosEliminatoria) {
            System.out.println(p.resumen());
        }
    }

    // Getters públicos
    public List<Partido> getPartidosCuartos() { return partidosCuartos; }
    public List<Partido> getPartidosSemifinal() { return partidosSemifinal; }
    public Partido getPartidoFinal() { return partidoFinal; }
    public Partido getPartidoTercerPuesto() { return partidoTercerPuesto; }

    // -----------------------------
    // 📌 Verificar si se debe iniciar la fase eliminatoria
    // -----------------------------
    public List<String> verificarYSortearCuartos(Torneo torneo) {
        if (torneo.todosPartidosDeGruposJugados() && partidosCuartos.isEmpty()) {
            List<Equipo> clasificados = torneo.obtenerClasificados();
            return sortearCuartos(clasificados);
        }
        return List.of("No se cumplen las condiciones para iniciar cuartos de final.");
    }

    // -----------------------------
    // 🔁 Avance automático de fases
    // -----------------------------
    public List<String> verificarAvanceAutomatico(Torneo torneo) {
        List<String> mensajes = new ArrayList<>();

        // 1️⃣ Si todos los partidos de grupos se jugaron y no hay cuartos
        if (torneo.todosPartidosDeGruposJugados() && partidosCuartos.isEmpty()) {
            List<Equipo> clasificados = torneo.clasificarOchoMejores();
            sortearCuartos(clasificados);
            mensajes.add("🏆 Fase de grupos finalizada. Se sortearon los cuartos de final.");
        }

        // 2️⃣ Si todos los cuartos se jugaron → semifinales
        if (!partidosCuartos.isEmpty()
                && partidosCuartos.stream().allMatch(Partido::getJugado)
                && partidosSemifinal.isEmpty()) {

            List<Equipo> ganadores = obtenerGanadoresCuartos();
            sortearSemifinales(ganadores);
            mensajes.add("✅ Cuartos finalizados. Se generaron las semifinales.");
        }

        // 3️⃣ Si todas las semis se jugaron → final y tercer puesto
        if (!partidosSemifinal.isEmpty()
                && partidosSemifinal.stream().allMatch(Partido::getJugado)
                && partidoFinal == null) {

            generarFinalYtercerPuesto();
            mensajes.add("🔥 Semifinales finalizadas. Se generaron la Final y el 3er Puesto.");
        }

        return mensajes;
    }

    // -----------------------------
    // 📌 Sorteo de Cuartos
    // -----------------------------
    public List<String> sortearCuartos(List<Equipo> clasificados) {
        if (clasificados == null || clasificados.size() < 8) {
            throw new IllegalArgumentException("Debe haber 8 equipos para el sorteo de cuartos.");
        }
        if (!partidosCuartos.isEmpty()) {
            throw new IllegalStateException("Los cuartos ya han sido sorteados.");
        }

        List<Equipo> copia = new ArrayList<>(clasificados);
        Collections.shuffle(copia);

        List<String> cruces = new ArrayList<>();

        for (int i = 0; i < 8; i += 2) {
            Equipo e1 = copia.get(i);
            Equipo e2 = copia.get(i + 1);

            Partido partido = new Partido(e1, e2, null);
            partidosCuartos.add(partido);

            cruces.add(e1.getNombre() + " vs " + e2.getNombre());
        }
        return cruces;
    }

    // -----------------------------
    // 📌 Ganadores de Cuartos
    // -----------------------------
    public List<Equipo> obtenerGanadoresCuartos() {
        List<Equipo> ganadores = new ArrayList<>();

        for (Partido partido : partidosCuartos) {
            if (!partido.getJugado()) {
                throw new IllegalStateException("Aún hay partidos de cuartos sin resultados.");
            }

            if (partido.getGolesLocal() > partido.getGolesVisitante()) {
                ganadores.add(partido.getEquipoLocal());
            } else if (partido.getGolesVisitante() > partido.getGolesLocal()) {
                ganadores.add(partido.getEquipoVisitante());
            } else if (partido.getPenalesLocal() != partido.getPenalesVisitante()) {
                ganadores.add(partido.getPenalesLocal() > partido.getPenalesVisitante()
                        ? partido.getEquipoLocal() : partido.getEquipoVisitante());
            } else {
                throw new IllegalStateException("Partido empatado sin definición por penales: " + partido);
            }
        }

        if (ganadores.size() != 4) {
            throw new IllegalStateException("No hay exactamente 4 ganadores de cuartos.");
        }

        return ganadores;
    }

    // -----------------------------
    // 📌 Actualizar resultado
    // -----------------------------
    public void registrarResultadoYAvanzar(Partido partido, int golesLocal, int golesVisitante) {
        actualizarPartidoEnListas(partido, golesLocal, golesVisitante);

        if (partidosCuartos.size() == 4
                && partidosCuartos.stream().allMatch(Partido::getJugado)
                && partidosSemifinal.isEmpty()) {
            sortearSemifinales(obtenerGanadoresCuartos());
        }

        if (partidosSemifinal.size() == 2
                && partidosSemifinal.stream().allMatch(Partido::getJugado)
                && partidoFinal == null) {
            generarFinalYtercerPuesto();
        }
    }

    private void actualizarPartidoEnListas(Partido partidoExterno, int golesLocal, int golesVisitante) {
        List<List<Partido>> fases = List.of(partidosCuartos, partidosSemifinal);
        for (List<Partido> lista : fases) {
            for (Partido p : lista) {
                if (p.getEquipoLocal().getNombre().equals(partidoExterno.getEquipoLocal().getNombre())
                        && p.getEquipoVisitante().getNombre().equals(partidoExterno.getEquipoVisitante().getNombre())) {
                    p.setGolesLocal(golesLocal);
                    p.setGolesVisitante(golesVisitante);
                    p.setJugado(true);
                    return;
                }
            }
        }

        if (partidoFinal != null && mismoPartido(partidoFinal, partidoExterno)) {
            partidoFinal.setGolesLocal(golesLocal);
            partidoFinal.setGolesVisitante(golesVisitante);
            partidoFinal.setJugado(true);
        } else if (partidoTercerPuesto != null && mismoPartido(partidoTercerPuesto, partidoExterno)) {
            partidoTercerPuesto.setGolesLocal(golesLocal);
            partidoTercerPuesto.setGolesVisitante(golesVisitante);
            partidoTercerPuesto.setJugado(true);
        }
    }

    private boolean mismoPartido(Partido p1, Partido p2) {
        return p1.getEquipoLocal().getNombre().equals(p2.getEquipoLocal().getNombre())
                && p1.getEquipoVisitante().getNombre().equals(p2.getEquipoVisitante().getNombre());
    }

    // -----------------------------
    // 📌 Sorteo de Semifinales
    // -----------------------------
    public List<String> sortearSemifinales(List<Equipo> ganadoresCuartos) {
        if (ganadoresCuartos == null || ganadoresCuartos.size() != 4) {
            throw new IllegalArgumentException("Debe haber exactamente 4 equipos para las semifinales.");
        }
        if (!partidosSemifinal.isEmpty()) {
            throw new IllegalStateException("Las semifinales ya han sido generadas.");
        }

        Collections.shuffle(ganadoresCuartos);
        partidosSemifinal.clear();

        Partido semi1 = new Partido(ganadoresCuartos.get(0), ganadoresCuartos.get(1), null);
        Partido semi2 = new Partido(ganadoresCuartos.get(2), ganadoresCuartos.get(3), null);

        partidosSemifinal.add(semi1);
        partidosSemifinal.add(semi2);

        return List.of(
                "Semifinal 1: " + semi1.getEquipoLocal().getNombre() + " vs " + semi1.getEquipoVisitante().getNombre(),
                "Semifinal 2: " + semi2.getEquipoLocal().getNombre() + " vs " + semi2.getEquipoVisitante().getNombre()
        );
    }

    // -----------------------------
    // 📌 Final y tercer puesto
    // -----------------------------
    public void generarFinalYtercerPuesto() {
        if (partidosSemifinal.size() < 2) return;

        Partido semi1 = partidosSemifinal.get(0);
        Partido semi2 = partidosSemifinal.get(1);

        partidoFinal = new Partido(semi1.getGanador(), semi2.getGanador());
        partidoTercerPuesto = new Partido(semi1.getPerdedor(), semi2.getPerdedor());
    }

    // ✅ Retorna todos los partidos de la eliminatoria
public List<Partido> getTodosLosPartidos() {
    List<Partido> todos = new ArrayList<>();
    if (partidosCuartos != null) todos.addAll(partidosCuartos);
    if (partidosSemifinal != null) todos.addAll(partidosSemifinal);
    if (partidoFinal != null) todos.add(partidoFinal);
    if (partidoTercerPuesto != null) todos.add(partidoTercerPuesto);
    return todos;
}

    // -----------------------------
    // 📊 Obtener resumen textual
    // -----------------------------
    public String obtenerResumen() {
        StringBuilder sb = new StringBuilder();

        sb.append("==== CUARTOS ====\n");
        if (partidosCuartos.isEmpty()) sb.append("(Aún no se han generado los partidos de cuartos)\n");
        else partidosCuartos.forEach(p -> sb.append(p).append("\n"));

        sb.append("\n==== SEMIFINALES ====\n");
        if (partidosSemifinal.isEmpty()) sb.append("(Aún no se han generado las semifinales)\n");
        else partidosSemifinal.forEach(p -> sb.append(p).append("\n"));

        sb.append("\n==== TERCER PUESTO ====\n");
        sb.append(partidoTercerPuesto == null ? "(Aún no definido)\n" : partidoTercerPuesto + "\n");

        sb.append("\n==== FINAL ====\n");
        sb.append(partidoFinal == null ? "(Aún no definido)\n" : partidoFinal + "\n");

        return sb.toString();
    }
    


}

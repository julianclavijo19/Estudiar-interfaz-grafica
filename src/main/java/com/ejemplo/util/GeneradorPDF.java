package com.ejemplo.util;

import com.ejemplo.model.Eliminatoria;
import com.ejemplo.model.Equipo;
import com.ejemplo.model.Jugador;
import com.ejemplo.model.Partido;
import com.ejemplo.model.Torneo;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeneradorPDF {

    public static String generarPDFPartidos(Torneo torneo, String rutaCompleta) throws IOException {
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Título principal
        Paragraph titulo = new Paragraph(torneo.getNombre().toUpperCase())
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("FASE DE GRUPOS - CALENDARIO DE PARTIDOS")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(subtitulo);

        // Información del torneo
        Paragraph info = new Paragraph("Fecha de generación: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(15)
                .setFontColor(ColorConstants.GRAY);
        document.add(info);

        // Estadísticas del torneo
        int totalPartidos = torneo.getPartidos().size();
        int totalGrupos = torneo.getGrupos().size();
        int totalEquipos = torneo.getEquipos().size();
        
        // Crear color azul claro personalizado
        DeviceRgb lightBlue = new DeviceRgb(173, 216, 230);
        
        Table tablaEstadisticas = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}))
                .useAllAvailableWidth()
                .setMarginBottom(25);
        
        Cell stat1 = new Cell().add(new Paragraph("Total de Equipos\n" + totalEquipos)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(lightBlue)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        
        Cell stat2 = new Cell().add(new Paragraph("Total de Grupos\n" + totalGrupos)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(lightBlue)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        
        Cell stat3 = new Cell().add(new Paragraph("Total de Partidos\n" + totalPartidos)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER))
                .setBackgroundColor(lightBlue)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        
        tablaEstadisticas.addCell(stat1);
        tablaEstadisticas.addCell(stat2);
        tablaEstadisticas.addCell(stat3);
        document.add(tablaEstadisticas);

        // Agrupar partidos por grupo
        Map<String, List<com.ejemplo.model.Equipo>> grupos = torneo.getGrupos();
        List<Partido> partidos = torneo.getPartidos();

        for (Map.Entry<String, List<com.ejemplo.model.Equipo>> entry : grupos.entrySet()) {
            String nombreGrupo = entry.getKey();
            
            // Título del grupo
            Paragraph tituloGrupo = new Paragraph(nombreGrupo)
                    .setFontSize(18)
                    .setBold()
                    .setMarginTop(20)
                    .setMarginBottom(10)
                    .setFontColor(ColorConstants.WHITE)
                    .setBackgroundColor(ColorConstants.BLUE)
                    .setPadding(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(tituloGrupo);

            // Tabla de partidos del grupo
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 1, 3, 2}))
                    .useAllAvailableWidth()
                    .setMarginBottom(20);

            // Encabezados de la tabla
            Cell headerLocal = new Cell().add(new Paragraph("EQUIPO LOCAL")
                    .setBold()
                    .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            Cell headerVs = new Cell().add(new Paragraph("VS")
                    .setBold()
                    .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            Cell headerVisitante = new Cell().add(new Paragraph("EQUIPO VISITANTE")
                    .setBold()
                    .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);
            Cell headerFecha = new Cell().add(new Paragraph("FECHA/HORA")
                    .setBold()
                    .setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.DARK_GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(8);

            tabla.addHeaderCell(headerLocal);
            tabla.addHeaderCell(headerVs);
            tabla.addHeaderCell(headerVisitante);
            tabla.addHeaderCell(headerFecha);

            // Filas de partidos
            int contador = 0;
            for (Partido partido : partidos) {
                // Solo incluir partidos de este grupo
                if (partido.getEquipoLocal().getNombreGrupo().equals(nombreGrupo)) {
                    contador++;
                    
                    // Alternar colores de fila
                    boolean esPar = contador % 2 == 0;
                    com.itextpdf.kernel.colors.Color colorFondo = esPar ? 
                            ColorConstants.LIGHT_GRAY : ColorConstants.WHITE;

                    Cell cellLocal = new Cell().add(new Paragraph(partido.getEquipoLocal().getNombre()))
                            .setBackgroundColor(colorFondo)
                            .setPadding(8)
                            .setTextAlignment(TextAlignment.LEFT);
                    
                    Cell cellVs = new Cell().add(new Paragraph("vs"))
                            .setBackgroundColor(colorFondo)
                            .setPadding(8)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontColor(ColorConstants.GRAY);
                    
                    Cell cellVisitante = new Cell().add(new Paragraph(partido.getEquipoVisitante().getNombre()))
                            .setBackgroundColor(colorFondo)
                            .setPadding(8)
                            .setTextAlignment(TextAlignment.LEFT);
                    
                    String fechaTexto = partido.getFechaHora() != null ? 
                            partido.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : 
                            "Sin programar";
                    
                    Cell cellFecha = new Cell().add(new Paragraph(fechaTexto))
                            .setBackgroundColor(colorFondo)
                            .setPadding(8)
                            .setTextAlignment(TextAlignment.CENTER)
                            .setFontColor(partido.getFechaHora() != null ? 
                                    ColorConstants.BLACK : ColorConstants.RED);

                    tabla.addCell(cellLocal);
                    tabla.addCell(cellVs);
                    tabla.addCell(cellVisitante);
                    tabla.addCell(cellFecha);
                }
            }

            document.add(tabla);
        }

        // Pie de página
        Paragraph pie = new Paragraph("Documento generado automáticamente por el Sistema de Gestión de Torneos")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFontColor(ColorConstants.GRAY);
        document.add(pie);

        document.close();
        return rutaCompleta;
    }
    
    public static String generarPDFEliminatoria(Torneo torneo, String rutaCompleta) throws IOException {
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        Eliminatoria eliminatoria = torneo.getEliminatoria();

        // Título principal
        Paragraph titulo = new Paragraph(torneo.getNombre().toUpperCase())
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("FASE ELIMINATORIA - RESUMEN DE PARTIDOS")
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20)
                .setFontColor(ColorConstants.DARK_GRAY);
        document.add(subtitulo);

        // Información del torneo
        Paragraph info = new Paragraph("Fecha de generación: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(30)
                .setFontColor(ColorConstants.GRAY);
        document.add(info);

        // CUARTOS DE FINAL
        crearSeccionEliminatoria(document, "🏟️ CUARTOS DE FINAL", 
                eliminatoria.getPartidosCuartos(), ColorConstants.BLUE);

        // SEMIFINALES
        crearSeccionEliminatoria(document, "⚔️ SEMIFINALES", 
                eliminatoria.getPartidosSemifinal(), new DeviceRgb(255, 152, 0));

        // TERCER PUESTO
        if (eliminatoria.getPartidoTercerPuesto() != null) {
            crearSeccionPartidoUnico(document, "🥉 TERCER PUESTO", 
                    eliminatoria.getPartidoTercerPuesto(), new DeviceRgb(184, 115, 51));
        } else {
            crearSeccionVacia(document, "🥉 TERCER PUESTO", 
                    "Las semifinales aún no se han jugado");
        }

        // FINAL
        if (eliminatoria.getPartidoFinal() != null) {
            crearSeccionPartidoUnico(document, "🏆 FINAL", 
                    eliminatoria.getPartidoFinal(), new DeviceRgb(255, 215, 0));
        } else {
            crearSeccionVacia(document, "🏆 FINAL", 
                    "Las semifinales aún no se han jugado");
        }

        // Pie de página
        Paragraph pie = new Paragraph("Documento generado automáticamente por el Sistema de Gestión de Torneos")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(30)
                .setFontColor(ColorConstants.GRAY);
        document.add(pie);

        document.close();
        return rutaCompleta;
    }
    
    private static void crearSeccionEliminatoria(Document document, String titulo, 
            List<Partido> partidos, com.itextpdf.kernel.colors.Color colorTitulo) {
        Paragraph tituloSeccion = new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(colorTitulo)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(tituloSeccion);

        if (partidos == null || partidos.isEmpty()) {
            Paragraph mensaje = new Paragraph("Aún no se han generado los partidos de esta fase")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(15)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(mensaje);
            return;
        }

        // Tabla de partidos
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 1, 3, 2, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Encabezados
        Cell headerLocal = new Cell().add(new Paragraph("EQUIPO LOCAL")
                .setBold()
                .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        Cell headerVs = new Cell().add(new Paragraph("VS")
                .setBold()
                .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        Cell headerVisitante = new Cell().add(new Paragraph("EQUIPO VISITANTE")
                .setBold()
                .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        Cell headerResultado = new Cell().add(new Paragraph("RESULTADO")
                .setBold()
                .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        Cell headerEstado = new Cell().add(new Paragraph("ESTADO")
                .setBold()
                .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);

        tabla.addHeaderCell(headerLocal);
        tabla.addHeaderCell(headerVs);
        tabla.addHeaderCell(headerVisitante);
        tabla.addHeaderCell(headerResultado);
        tabla.addHeaderCell(headerEstado);

        // Filas de partidos
        int contador = 0;
        for (Partido partido : partidos) {
            contador++;
            boolean esPar = contador % 2 == 0;
            com.itextpdf.kernel.colors.Color colorFondo = esPar ? 
                    ColorConstants.LIGHT_GRAY : ColorConstants.WHITE;

            Cell cellLocal = new Cell().add(new Paragraph(partido.getEquipoLocal().getNombre()))
                    .setBackgroundColor(colorFondo)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.LEFT);
            
            Cell cellVs = new Cell().add(new Paragraph("vs"))
                    .setBackgroundColor(colorFondo)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY);
            
            Cell cellVisitante = new Cell().add(new Paragraph(partido.getEquipoVisitante().getNombre()))
                    .setBackgroundColor(colorFondo)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.LEFT);
            
            String resultadoTexto;
            if (partido.getJugado()) {
                resultadoTexto = String.format("%d - %d", partido.getGolesLocal(), partido.getGolesVisitante());
                if (partido.getPenalesLocal() != null && partido.getPenalesVisitante() != null) {
                    resultadoTexto += String.format("\n(Penales: %d-%d)", 
                            partido.getPenalesLocal(), partido.getPenalesVisitante());
                }
            } else {
                resultadoTexto = "Pendiente";
            }
            
            Cell cellResultado = new Cell().add(new Paragraph(resultadoTexto))
                    .setBackgroundColor(colorFondo)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(partido.getJugado() ? ColorConstants.BLACK : ColorConstants.RED);
            
            String estadoTexto = partido.getJugado() ? "✅ Jugado" : "⏳ Pendiente";
            Cell cellEstado = new Cell().add(new Paragraph(estadoTexto))
                    .setBackgroundColor(colorFondo)
                    .setPadding(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(partido.getJugado() ? ColorConstants.GREEN : ColorConstants.ORANGE);

            tabla.addCell(cellLocal);
            tabla.addCell(cellVs);
            tabla.addCell(cellVisitante);
            tabla.addCell(cellResultado);
            tabla.addCell(cellEstado);
        }

        document.add(tabla);
    }
    
    private static void crearSeccionPartidoUnico(Document document, String titulo, 
            Partido partido, com.itextpdf.kernel.colors.Color colorTitulo) {
        Paragraph tituloSeccion = new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(colorTitulo)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(tituloSeccion);

        // Tabla para un solo partido
        Table tabla = new Table(UnitValue.createPercentArray(new float[]{3, 1, 3, 2, 2}))
                .useAllAvailableWidth()
                .setMarginBottom(20);

        // Encabezados
        tabla.addHeaderCell(new Cell().add(new Paragraph("EQUIPO LOCAL").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY).setTextAlignment(TextAlignment.CENTER).setPadding(8));
        tabla.addHeaderCell(new Cell().add(new Paragraph("VS").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY).setTextAlignment(TextAlignment.CENTER).setPadding(8));
        tabla.addHeaderCell(new Cell().add(new Paragraph("EQUIPO VISITANTE").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY).setTextAlignment(TextAlignment.CENTER).setPadding(8));
        tabla.addHeaderCell(new Cell().add(new Paragraph("RESULTADO").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY).setTextAlignment(TextAlignment.CENTER).setPadding(8));
        tabla.addHeaderCell(new Cell().add(new Paragraph("ESTADO").setBold().setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(ColorConstants.DARK_GRAY).setTextAlignment(TextAlignment.CENTER).setPadding(8));

        // Fila del partido
        String resultadoTexto;
        if (partido.getJugado()) {
            resultadoTexto = String.format("%d - %d", partido.getGolesLocal(), partido.getGolesVisitante());
            if (partido.getPenalesLocal() != null && partido.getPenalesVisitante() != null) {
                resultadoTexto += String.format("\n(Penales: %d-%d)", 
                        partido.getPenalesLocal(), partido.getPenalesVisitante());
                if (partido.getGanadorPorPenales() != null) {
                    resultadoTexto += String.format("\n🏆 Ganador: %s", partido.getGanadorPorPenales().getNombre());
                }
            } else if (partido.getGanador() != null) {
                resultadoTexto += String.format("\n🏆 Ganador: %s", partido.getGanador().getNombre());
            }
        } else {
            resultadoTexto = "Pendiente";
        }

        tabla.addCell(new Cell().add(new Paragraph(partido.getEquipoLocal().getNombre()))
                .setPadding(8).setTextAlignment(TextAlignment.LEFT));
        tabla.addCell(new Cell().add(new Paragraph("vs"))
                .setPadding(8).setTextAlignment(TextAlignment.CENTER).setFontColor(ColorConstants.GRAY));
        tabla.addCell(new Cell().add(new Paragraph(partido.getEquipoVisitante().getNombre()))
                .setPadding(8).setTextAlignment(TextAlignment.LEFT));
        tabla.addCell(new Cell().add(new Paragraph(resultadoTexto))
                .setPadding(8).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(partido.getJugado() ? ColorConstants.BLACK : ColorConstants.RED));
        tabla.addCell(new Cell().add(new Paragraph(partido.getJugado() ? "✅ Jugado" : "⏳ Pendiente"))
                .setPadding(8).setTextAlignment(TextAlignment.CENTER)
                .setFontColor(partido.getJugado() ? ColorConstants.GREEN : ColorConstants.ORANGE));

        document.add(tabla);
    }
    
    private static void crearSeccionVacia(Document document, String titulo, String mensaje) {
        Paragraph tituloSeccion = new Paragraph(titulo)
                .setFontSize(18)
                .setBold()
                .setMarginTop(20)
                .setMarginBottom(10)
                .setFontColor(ColorConstants.WHITE)
                .setBackgroundColor(ColorConstants.GRAY)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(tituloSeccion);

        Paragraph mensajeParrafo = new Paragraph(mensaje)
                .setFontSize(12)
                .setFontColor(ColorConstants.GRAY)
                .setMarginBottom(15)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(mensajeParrafo);
    }
    
    /**
     * Genera un PDF con la tabla de posiciones por grupo
     */
    public static void generarPDFTablaPosiciones(Torneo torneo, String rutaCompleta) throws IOException {
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Título principal
        Paragraph titulo = new Paragraph(torneo.getNombre().toUpperCase())
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("TABLA DE POSICIONES POR GRUPO")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20)
                .setFontColor(new DeviceRgb(25, 118, 210));
        document.add(subtitulo);

        // Información del torneo
        Paragraph info = new Paragraph("Fecha de generación: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(30);
        document.add(info);

        Map<String, List<Equipo>> grupos = torneo.getGrupos();
        
        for (Map.Entry<String, List<Equipo>> entry : grupos.entrySet()) {
            String nombreGrupo = entry.getKey();
            List<Equipo> equiposDelGrupo = new java.util.ArrayList<>(entry.getValue());
            
            // Ordenar equipos
            equiposDelGrupo.sort((e1, e2) -> {
                int cmp = Integer.compare(e2.getPuntos(), e1.getPuntos());
                if (cmp == 0)
                    cmp = Integer.compare((e2.getGolesFavor() - e2.getGolesContra()),
                                          (e1.getGolesFavor() - e1.getGolesContra()));
                if (cmp == 0)
                    cmp = Integer.compare(e2.getGolesFavor(), e1.getGolesFavor());
                return cmp;
            });

            // Título del grupo
            Paragraph tituloGrupo = new Paragraph(nombreGrupo)
                    .setFontSize(16)
                    .setBold()
                    .setFontColor(new DeviceRgb(66, 66, 66))
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(tituloGrupo);

            // Crear tabla
            float[] columnWidths = {1, 3, 1, 1, 1, 1, 1, 1, 1, 1.5f};
            Table tabla = new Table(UnitValue.createPercentArray(columnWidths));
            tabla.setWidth(UnitValue.createPercentValue(100));

            // Encabezados
            agregarCeldaEncabezado(tabla, "#", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "EQUIPO", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "PJ", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "G", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "E", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "P", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "GF", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "GC", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "DG", new DeviceRgb(33, 150, 243));
            agregarCeldaEncabezado(tabla, "PTS", new DeviceRgb(33, 150, 243));

            // Filas de datos
            int posicion = 1;
            for (Equipo equipo : equiposDelGrupo) {
                agregarCeldaDatos(tabla, String.valueOf(posicion++), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, equipo.getNombre(), TextAlignment.LEFT);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getPartidosJugados()), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getGanados()), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getEmpatados()), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getPerdidos()), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getGolesFavor()), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getGolesContra()), TextAlignment.CENTER);
                int dif = equipo.getGolesFavor() - equipo.getGolesContra();
                agregarCeldaDatos(tabla, String.valueOf(dif), TextAlignment.CENTER);
                agregarCeldaDatos(tabla, String.valueOf(equipo.getPuntos()), TextAlignment.CENTER, true);
            }

            document.add(tabla);
            document.add(new Paragraph("\n"));
        }

        document.close();
    }
    
    /**
     * Genera un PDF con la tabla de goleadores
     */
    public static void generarPDFGoleadores(Torneo torneo, String rutaCompleta) throws IOException {
        PdfWriter writer = new PdfWriter(rutaCompleta);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(50, 50, 50, 50);

        // Título principal
        Paragraph titulo = new Paragraph(torneo.getNombre().toUpperCase())
                .setFontSize(24)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("TABLA DE GOLEADORES")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20)
                .setFontColor(new DeviceRgb(76, 175, 80));
        document.add(subtitulo);

        // Información del torneo
        Paragraph info = new Paragraph("Fecha de generación: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginBottom(30);
        document.add(info);

        // Obtener goleadores y filtrar solo los que tienen goles
        List<Jugador> goleadores = torneo.mostrarGoleadores();
        goleadores = goleadores.stream()
                .filter(j -> j.getGoles() > 0)
                .collect(Collectors.toList());

        if (goleadores.isEmpty()) {
            Paragraph mensaje = new Paragraph("No hay goleadores registrados aún.")
                    .setFontSize(12)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(50);
            document.add(mensaje);
            document.close();
            return;
        }

        // Crear tabla
        float[] columnWidths = {1, 3, 2.5f, 2, 1.5f};
        Table tabla = new Table(UnitValue.createPercentArray(columnWidths));
        tabla.setWidth(UnitValue.createPercentValue(100));

        // Encabezados
        agregarCeldaEncabezado(tabla, "#", new DeviceRgb(76, 175, 80));
        agregarCeldaEncabezado(tabla, "JUGADOR", new DeviceRgb(76, 175, 80));
        agregarCeldaEncabezado(tabla, "EQUIPO", new DeviceRgb(76, 175, 80));
        agregarCeldaEncabezado(tabla, "POSICIÓN", new DeviceRgb(76, 175, 80));
        agregarCeldaEncabezado(tabla, "GOLES", new DeviceRgb(76, 175, 80));

        // Filas de datos
        int posicion = 1;
        for (Jugador jugador : goleadores) {
            agregarCeldaDatos(tabla, String.valueOf(posicion++), TextAlignment.CENTER);
            agregarCeldaDatos(tabla, jugador.getNombre(), TextAlignment.LEFT);
            String nombreEquipo = jugador.getEquipo() != null ? jugador.getEquipo().getNombre() : "Sin equipo";
            agregarCeldaDatos(tabla, nombreEquipo, TextAlignment.LEFT);
            String pos = jugador.getPosicion() != null ? jugador.getPosicion().toString() : "-";
            agregarCeldaDatos(tabla, pos, TextAlignment.CENTER);
            agregarCeldaDatos(tabla, String.valueOf(jugador.getGoles()), TextAlignment.CENTER, true);
        }

        document.add(tabla);
        document.close();
    }
    
    private static void agregarCeldaEncabezado(Table tabla, String texto, DeviceRgb color) {
        Cell celda = new Cell()
                .add(new Paragraph(texto)
                        .setBold()
                        .setFontSize(11)
                        .setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(color)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(8);
        tabla.addHeaderCell(celda);
    }
    
    private static void agregarCeldaDatos(Table tabla, String texto, TextAlignment alineacion) {
        agregarCeldaDatos(tabla, texto, alineacion, false);
    }
    
    private static void agregarCeldaDatos(Table tabla, String texto, TextAlignment alineacion, boolean bold) {
        Paragraph parrafo = new Paragraph(texto).setFontSize(10);
        if (bold) {
            parrafo.setBold();
        }
        Cell celda = new Cell()
                .add(parrafo)
                .setTextAlignment(alineacion)
                .setPadding(6);
        tabla.addCell(celda);
    }
}


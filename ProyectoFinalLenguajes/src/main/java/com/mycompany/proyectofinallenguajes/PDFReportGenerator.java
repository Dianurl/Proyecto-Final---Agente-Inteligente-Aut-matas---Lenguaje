/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.util.List;
import java.io.FileWriter;
/**
 *
 * @author DELL
 */
public class PDFReportGenerator {
    public static void createHTMLReport( String path,String regex, java.util.List<String> pos, java.util.List<String> neg,
            String transitionTable,
            String graphPNG,
            String tablePNG) throws Exception { // Funcion que crearar un HTML que simulara un PDF con registro de todo

        StringBuilder html = new StringBuilder();

        html.append("""
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="UTF-8">
        <title>Reporte del Lenguaje</title>
        <style>
            body { font-family: Arial; padding: 20px; }
            h1 { color: #333; }
            pre { background: #f0f0f0; padding: 10px; white-space: pre-wrap; }
            .correcto { color: green; }
            .incorrecto { color: red; }
            img { border: 1px solid #ccc; margin-top: 20px; }
        </style>
        </head>
        <body>
        """);

        html.append("<h1>Reporte del Lenguaje</h1>"); // La informacion que se agregara de otras clases (ejemplos, tabla y grafo)

        html.append("<h2>Lenguaje Analizado:</h2><p>").append(regex).append("</p>");

        html.append("<h2>Ejemplos Correctos:</h2><ul>");
        for (String p : pos) html.append("<li class=\"correcto\">").append(p).append("</li>");
        html.append("</ul>");

        html.append("<h2>Ejemplos Incorrectos:</h2><ul>");
        for (String n : neg) html.append("<li class=\"incorrecto\">").append(n).append("</li>");
        html.append("</ul>");

        html.append("<h2>Tabla de Transición</h2>");
        html.append("<pre>").append(transitionTable).append("</pre>");
        html.append("<img src=\"")
                .append(tablePNG)
                .append("\" width=\"600\">");

        html.append("<h2>Grafo del Automata</h2>");

        html.append("<img src=\"")
                .append(graphPNG)
                .append("\" width=\"600\">");

        html.append("</body></html>");

        try (FileWriter fw = new FileWriter(path)) { // Aqui se guarda el archivo HTML
            fw.write(html.toString());
        }
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.io.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
/**
 *
 * @author DELL
 */
public class DotExporter {
    public static void exportGraphDot(DFA dfa, String path) throws IOException { // Funcion encargada de exportar el grafo como .dot
        StringBuilder sb = new StringBuilder();
        sb.append("digraph Automata {\n");
        sb.append("rankdir=LR;\n"); // Se apunto de izquierda a derecha

        for(State s : dfa.states) { // Empieza a exportar todos los estados del DFA
            sb.append(String.format("q%d [shape=%s];\n", s.id, s.isAccept ? "doublecircle" : "circle")); // Doble circulo para estado final y uno para no final
        }

        sb.append("start [shape=point];\n"); // Nodo de inicio
        sb.append("start -> q").append(dfa.start.id).append(";\n");
        for (State s : dfa.states) { // Exporta transiciones
            for (var e : s.trans.entrySet()) {
                sb.append(String.format("q%d -> q%d [label=\"%s\"];\n",
                    s.id, e.getValue().id, e.getKey()));
            }
        }

        sb.append("}\n");

        Files.write(Paths.get(path), sb.toString().getBytes()); // Guarda el archivo .dot
    }

    public static void exportTransitionDot(DFA dfa, String path) throws IOException { // Funcion encargada de pasar la tabla de transiscion .dot
        StringBuilder sb = new StringBuilder();
        sb.append("digraph TransitionTable {\n");
        sb.append("node [shape=plaintext];\n");
        sb.append("table [label=<\n");
        sb.append("<table border='1' cellborder='1' cellspacing='0'>\n");

        sb.append("<tr><td><b>Estado</b></td>");
        for(char a : dfa.alphabet) // Empieza por los estados
            sb.append("<td><b>").append(a).append("</b></td>");
        
        sb.append("<td><b>Aceptado</b></td></tr>\n");

        List<State> list = new ArrayList<>(dfa.states); // Empieza con las filas
        list.sort(Comparator.comparingInt(s -> s.id));
        for(State s : list) {
            sb.append("<tr><td>q").append(s.id).append("</td>");
            for(char a : dfa.alphabet) {
                State t = s.trans.get(a);
                sb.append("<td>").append(t == null ? "-" : "q" + t.id).append("</td>");
            }

            sb.append("<td>").append(s.isAccept ? "Si" : "No").append("</td></tr>\n");
        }

        sb.append("</table>\n");
        sb.append(">];\n}\n");

        Files.write(Paths.get(path), sb.toString().getBytes()); // Guarda el archivo .dot
    }
}

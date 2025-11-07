/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
/**
 *
 * @author DELL
 */
public class Utils {
    public static void writeStringToFile(String path, String content) throws IOException { // Funcion encargada de guardar el contenido a un archivo
        Files.write(Paths.get(path), content.getBytes());
    }
    
    public static String readAll(String path) throws IOException { // Funcion encargada de leer todo un archivo en un solo string
        return Files.lines(Paths.get(path)).collect(Collectors.joining("\n"));
    }
    
    public static void runGraphviz(String dotPath, String outPath) { // Funcion para crear los .png de Graphviz
        try {
            ProcessBuilder pb = new ProcessBuilder(
                "dot", "-Tpng", dotPath, "-o", outPath
            );
            pb.redirectErrorStream(true);
            Process p = pb.start();
            p.waitFor();
        } catch(Exception e) {
            System.out.println("Graphviz no pudo ejecutarse");
        }
    }
}

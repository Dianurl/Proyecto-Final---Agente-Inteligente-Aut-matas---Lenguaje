/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.io.File;
import java.util.*;
import java.util.regex.Pattern;
/**
 *
 * @author DELL
 */
public class ExampleManager {
    private RegexGenerator generator; // Encragado de generar cadenas
    private String regex; // Lenguaje regex a utilizar
    private List<String> positives = new ArrayList<>(); // Lista de ejemplos correctos
    private List<String> negatives = new ArrayList<>(); // Lista de ejemplos incorrectos

    public ExampleManager(RegexGenerator gen, String regex) { // Constructor
        this.generator = gen;
        this.regex = regex;
    }

    public void generateAndSaveExamples(int nPos, int nNeg) throws Exception { // Funcion encargda de generar y guardar los ejemplos
        Pattern pat = Pattern.compile("^" + regex + "$"); // Se analiza el lenguaje a utilizar
        for(int i=0; i<nPos; i++) { 
            String s = generator.generate(); // Empieza a generar cadenas random
            int tries = 0;
            while(!pat.matcher(s).matches() && tries < 10) { // Intenta crear cadenas correctas, reintentado hasta 10 veces
                s = generator.generate();
                tries++;
            }
            if(!pat.matcher(s).matches()) { // Si aun no se encuentran, se forza para crear mas ejemplos
                s = generator.generate();
            }
            positives.add(s);
        }

        Random rnd = new Random(); // Se crean cadenas incorrectas
        int attempts = 0;
        while(negatives.size() < nNeg && attempts < 2000) { // Empieza a generar cadenas random sin coincider con lenguaje
            String s = randomCandidate(rnd);
            if(!pat.matcher(s).matches()) // Si no coincide, se agrega
                negatives.add(s);
            
            attempts++;
        }
        
        if(negatives.size() < nNeg) { // Si no se crearon suficientes cadenas
            while(negatives.size() < nNeg) // Vuelve a crear mas
                negatives.add("x" + negatives.size());
        }

        String base = "Ejemplos-" + sanitize(regex); // Aqui se empieza a generar los ejemplos.txt
        StringBuilder sb = new StringBuilder();
        sb.append("Lenguaje a utilizar: ").append(regex).append("\n\nEjemplos Correctos:\n");
        for (String p : positives) 
            sb.append(p).append("\n"); // Se meten los ejemplos correctos
        
        sb.append("\nEjemplos Incorrectos:\n");
        for (String n : negatives)  // Se meten los ejemplos incorrectos
            sb.append(n).append("\n");
        
        Utils.writeStringToFile(base + ".txt", sb.toString()); // Se guarda el archivo en la carpeta del proyecto
        
        System.out.println("Ejemplos Correctos:"); // Aqui se muestra lo mismo pero dentro de la consola
        for (int i = 0; i < positives.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + positives.get(i));
        }

        System.out.println("\nEjemplos Incorrectos:");
        for (int i = 0; i < negatives.size(); i++) {
            System.out.println("  " + (i + 1) + ") " + negatives.get(i));
        }
    }

    private String randomCandidate(Random rnd) { // Funcion que genera cadenas random con letras del abecedario
        int len = 1 + rnd.nextInt(6); // Longitud hasta 6 para que sean visibles sin problemas
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<len;i++) 
            sb.append((char)('a' + rnd.nextInt(6))); // Se meten  las letras
        
        return sb.toString();
    }

    private String sanitize(String s) { // Genera correctamente el lenguaje para que sea entendible
        return s.replaceAll("[^a-zA-Z0-9]", "_"); 
    }

    public List<String> getPositiveExamples() { 
        return positives; 
    }
    
    public List<String> getNegativeExamples() { 
        return negatives; 
    }
}

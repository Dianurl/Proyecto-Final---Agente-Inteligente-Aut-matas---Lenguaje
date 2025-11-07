/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.proyectofinallenguajes;
import java.io.File;
import java.nio.file.Paths;
import java.util.*;
/**
 *
 * @author DELL
 */
public class ProyectoFinalLenguajes {
    private static Scanner type = new Scanner(System.in);
    private static Map<String, LearnedLanguage> processed = new HashMap<>(); // Encargado de guardar lenguajes analizados previamente
    private static final String OUTPUT_DIR = "gv"; // Carpeta donde se fuardaran los .gv
    
    public static void main(String[] args) throws Exception {
        new File(OUTPUT_DIR).mkdirs(); // Se crea la carpeta
        System.out.println("Iniciando IA Dindies...");
        
        String option = "";
        System.out.println("Bienvenido!");
        System.out.println("En que puedo ayudarte?");
        
        while(!option.equalsIgnoreCase("Salir") || !option.equalsIgnoreCase("salir")) {
            System.out.println("");
            System.out.println("Te gustaria ingresar algun lenguaje o salir del programa? (Lenguaje/Salir)"); // Menu principal que representara a una IA
            String answer = type.nextLine();
            
            try { 
                //option = Integer.parseInt(answer); // Seleccion de lenguaje para para MT
                option = answer;
                //switch(option){ 
                    if(option.contains("Lenguaje") || option.contains("lenguaje")) {
                        System.out.println("Perfecto! Indicame cual sera el lenguaje que quieres que analice y creare 5 ejemplos correctos y 5 incorrectos");
                        String regex = type.nextLine().trim();
                        
                        if(processed.containsKey(regex)) {
                            System.out.println("Uy, parece que ya has introducido este lenguaje antes,");
                            System.out.println("Quieres que repita el proceso de este lenguaje? (Si/No)");
                            String r = type.nextLine().trim();
                            if(!r.equals("Si"))
                                continue;
                        }
                        
                        runLanguageCycle(regex);
                    }
                    
                    else if(option.contains("Salir") || option.contains("salir")) {
                        System.out.println("Programa finzalizado correctamente");
                        option = "Salir";
                    }

                    else{
                        System.out.println("Perdon, no te he entendido bien.");
                        System.out.println("Por favor ten dentro de tu respuesta la palbara 'Lenguaje' para analizar o 'Salir' para terminar el chat.");
                    }
                //}
            }catch(NumberFormatException e) { // Si se escribio una letra, lanza texto y repite menu
                System.out.println("Se ingreso una respuesta incorrecta, vuelva a intentarlo.");
                //option = "Salir";
            }
        }
    }
    
    private static void runLanguageCycle(String regex) throws Exception { // Funcion que continua con la IA y que genera todo el chat
        RegexParser parser = new RegexParser(regex);
        RegexNode ast = parser.parse();
        RegexGenerator gen = new RegexGenerator(ast);
        ExampleManager exm = new ExampleManager(gen, regex);
        PTALearner learner = new PTALearner();
        
        System.out.println("Muy bien, analizemos el lenguaje " + "'" + regex + "'");
        System.out.println("Con el lenguaje " + "'" + regex + "'" + " puedo crear los siguientes ejemplos: \n");
        exm.generateAndSaveExamples(5, 5); // Genera y muestra los ejemplos correctos e incorrectos
        DFA dfa = learner.learn(exm.getPositiveExamples(), exm.getNegativeExamples()); // Analiza los ejemplos para crear el DFA
        
        
        System.out.println("\nAparte, estos ejemplos tambien los he guardado en un archivo .txt con el nombre del lenguaje.");
        System.out.println(" Te gustaria que ahora cree la tabla de transicion del automata? (Si/No)");
        String t = type.nextLine().trim();
        
        if (t.contains("Si") || t.contains("si")) {
            String tableDot = OUTPUT_DIR + "/TablaDeTransicion-" + sanitizeFilename(regex) + ".gv"; // Nombre del archivo .gv de la tabla
            String txtPath = "TablaDeTransicion-" + sanitizeFilename(regex) + ".txt"; // Nombre del archivo .txt de la tabla
            String tablePNG = "TablaDeTransicion-" + sanitizeFilename(regex) + ".png"; // Nombre del archivo .png de la tabla
            DotExporter.exportTransitionDot(dfa, tableDot); // Exporta el .gv de la tabla
            Utils.writeStringToFile(txtPath, dfa.toTransitionTableString()); // Utilizado para exportar la tabla.txt
            
            System.out.println("\nPerfecto! Emepzare a crear la tabla de transicion...");
            System.out.println(" La tabla quedaria de esta forma:\n");
            System.out.println(dfa.toTransitionTableString()); // Tabla en consola
            Utils.runGraphviz(tableDot, tablePNG); // Convertidor de .gv a .png
            java.awt.Desktop.getDesktop().open(new File(tablePNG)); // Abre automaticamente la tabla.png
            System.out.println("Como complemento, tambien he creado la tabla en un archivo .txt y .png para que lo tengas guardado dentro de tu proyecto.");
        }
        

        System.out.println(" Ahora, tambien te gustaria ver el grafo? (Si/No)");
        String g = type.nextLine().trim();
        String graphDot = OUTPUT_DIR + "/Automata-" + sanitizeFilename(regex) + ".gv";
        
        if (g.contains("Si") || g.contains("si")) {
            String graphPNG = "Automata-" + sanitizeFilename(regex) + ".png";
            System.out.println("Muy bien, emepzare a crear el grafo...");
            System.out.println("Aqui tienes la imagen del grafo (Se te tuvo que haber generado una foto por aparte).\n");
            
            DotExporter.exportGraphDot(dfa, graphDot); // Exporta .gv del grafo
            
            Utils.runGraphviz(graphDot, graphPNG); // Convertidor de .gv a .png
            java.awt.Desktop.getDesktop().open(new File(graphPNG));
            System.out.println("Tambien te he guardado el grafo como un .gv y un .png dentro del proyecto. ");
        }
        

        System.out.println("\nMuy bien, y con todos estos datos recopilados, te gustaria que ahora genere un resumen en una pagina HTML?");
        System.out.println("Dentro del archivo estaria guardando lo siguiente: ");
        System.out.println(" - Ejemplos correctos.");
        System.out.println(" - Ejemplos incorrectos.");
        System.out.println(" - Tabla de transicion.");
        System.out.println(" - Direccion en donde se guardo el grafo.");
        System.out.println("\n Puedes responder con un 'Si' o con un 'No'");
        String p = type.nextLine().trim();

        if (p.contains("Si") || p.contains("si")) {
            String pdfPath = "ReportePDF-" + sanitizeFilename(regex) + ".html";
            String graphPNG = "Automata-" + sanitizeFilename(regex) + ".png";
            String tablePNG = "TablaDeTransicion-" + sanitizeFilename(regex) + ".png";
            
            PDFReportGenerator.createHTMLReport(pdfPath, regex,
                exm.getPositiveExamples(),
                exm.getNegativeExamples(),
                dfa.toTransitionTableString(), graphPNG, tablePNG); // Aqui se llama al PDFReportGenerator para generar el HTML
            
            System.out.println("\nPerfecto, entonces creare un HTML el cual contendra los ejemplos, tabla de transiscio y grafo del lenguaje" + "'" + regex + "'.");
            System.out.println("He creado el HTML (te tuvo que haber apaarecido) y lo deje listo para que lo revises dentro de tu carpeta!");
            java.awt.Desktop.getDesktop().open(new File(pdfPath)); // Abre automaticamente el HTML
            System.out.println("Lo puedes encontrar en: " + pdfPath);
        }
        
        String ok = type.nextLine().trim();
        processed.put(regex, new LearnedLanguage(regex, dfa, exm)); // Se guarda el lenguaje por si se llega a repetir
        System.out.println("Ok! Ya he finalizado con el proceso para el lenguaje: " + regex + "y si quieres podemos seguir con otro.");
    }
    
    private static String sanitizeFilename(String s) { // Funcion encargada de obtener nombres de los archivos sin problemas
        return s.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
    }
    
    static class LearnedLanguage { // Funcion encargada de guardar todos los datos de los lenguajes para evitar repeticiones
        String regex;
        DFA dfa;
        ExampleManager examples;
        LearnedLanguage(String r, DFA d, ExampleManager e) {
            regex = r;
            dfa = d;
            examples = e;
        }
    }
}

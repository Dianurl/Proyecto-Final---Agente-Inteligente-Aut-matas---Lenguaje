/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.util.*;
import java.util.Random;
/**
 *
 * @author DELL
 */
public class RegexGenerator {
    private final RegexNode root;
    private final Random rnd = new Random();

    public RegexGenerator(RegexNode root) { // Constructor
        this.root = root;
    }

    public String generate() { 
        return generate(root);
    }

    private String generate(RegexNode node) {// Funcion encargada de generar cadenas validas
        if(node instanceof LiteralNode lit) {
            return "" + lit.c; // Devuelve el caracter
        }

        if(node instanceof ConcatNode con) {
            StringBuilder sb = new StringBuilder();
            for(RegexNode part : con.parts) {
                sb.append(generate(part)); // Une resultados de partes
            }
            
            return sb.toString();
        }

        if(node instanceof AltNode or) {
            return rnd.nextBoolean() ? generate(or.left): generate(or.right);
        }

        if(node instanceof StarNode st) {
            int times = 1 + rnd.nextInt(4); // evita vacío repetido
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < times; i++)
                sb.append(generate(st.child));
            
            return sb.toString();
        }

        if(node instanceof PlusNode pn) {
            int times = 1 + rnd.nextInt(4); // De 1 a 4 repeticiones
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < times; i++)
                sb.append(generate(pn.child));
            
            return sb.toString();
        }

        if (node instanceof QuesNode qn) {
            return rnd.nextBoolean() ? generate(qn.child) : "";
        }

        if (node instanceof DotNode) {
            return "" + (char)('a' + rnd.nextInt(26));
        }

        if (node instanceof CharClassNode cls) {
            if (cls.chars.isEmpty()) 
                return "";
            
            return "" + cls.chars.get(rnd.nextInt(cls.chars.size()));
        }

        return "";
    }
}

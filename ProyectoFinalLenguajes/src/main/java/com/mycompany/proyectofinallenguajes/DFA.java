/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.util.*;
/**
 *
 * @author DELL
 */
public class DFA {
    State start;
    Set<State> states = new HashSet<>(); // Todos los estados alcanzables
    Set<Character> alphabet = new TreeSet<>(); // Alfabeto que se usara para las transiciones

    public DFA(State s) { 
        start = s; 
        collectStates(); 
    }

    public void collectStates() { //Funcion encargada de rellenar los estados y alfabeto
        states.clear();
        Queue<State> q = new LinkedList<>();
        q.add(start);
        states.add(start);
        while(!q.isEmpty()) {
            State cur = q.poll();
            for(Map.Entry<Character, State> e : cur.trans.entrySet()) { // Se mira las transisciones desde el actual
                alphabet.add(e.getKey());
                if(!states.contains(e.getValue())) { // Si el estado de destino es nuevo, se agrega
                    states.add(e.getValue());
                    q.add(e.getValue());
                }
            }
        }
    }

    public boolean accepts(String w) { // Funcion que indica si una cadena es aceptada o no
        State cur = start;
        for(char c : w.toCharArray()) {
            State nxt = cur.trans.get(c);
            if (nxt == null) 
                return false;
            
            cur = nxt;
        }
        return cur.isAccept;
    }

    public String toTransitionTableString() { // Funcion la cual crea la tabla de transicion dentro de la consola
        StringBuilder sb = new StringBuilder();
        List<State> sList = new ArrayList<>(states);
        sList.sort(Comparator.comparingInt(a -> a.id)); // Se ordena por estado
        sb.append(String.format("%-6s", "Estado"));
        for(char a : alphabet) // Se encabezan los simbolos pertenecientes al alfabeto
            sb.append(String.format("%-6s", a));
        
        sb.append(" Aceptado\n");
        for(State s : sList) { // Se escriben las filas de las tablas
            sb.append(String.format("%-6s", "q"+s.id));
            for(char a : alphabet) {
                State t = s.trans.get(a);
                sb.append(String.format("%-6s", t == null ? "-" : "q"+t.id));
            }
            
            sb.append(s.isAccept ? "  Si\n" : "  No\n");
        }
        
        return sb.toString();
    }
}

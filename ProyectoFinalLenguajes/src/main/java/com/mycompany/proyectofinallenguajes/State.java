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
public class State {
    int id; // Identifica el estado
    boolean isAccept; // Acepta si es estado final
    Map<Character, State> trans = new HashMap<>(); // Cambio de estados
    
    State(int id){ 
        this.id = id; 
    }
    
    
    public String toString(){ 
        return "q" + id + (isAccept ? "(Aceptado)" : ""); 
    }
}

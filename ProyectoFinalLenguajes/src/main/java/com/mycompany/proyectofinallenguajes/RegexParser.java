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

abstract class RegexNode {
    abstract String debug();
}

class LiteralNode extends RegexNode {
    char c;
    LiteralNode(char c){ 
        this.c = c; 
    }
    
    String debug(){ 
        return Character.toString(c); 
    }
}

class ConcatNode extends RegexNode {
    List<RegexNode> parts;
    ConcatNode(List<RegexNode> p){ 
        parts = p; 
    }
    
    String debug(){ 
        StringBuilder sb = new StringBuilder(); 
        for(var n: parts) 
            sb.append(n.debug()); 
        
        return sb.toString(); 
    }
}

class AltNode extends RegexNode {
    RegexNode left, right;
    AltNode(RegexNode l, RegexNode r){ 
        left = l; right = r; 
    }
    
    String debug(){ 
        return "(" + left.debug() + "|" + right.debug() + ")"; 
    }
}

class StarNode extends RegexNode {
    RegexNode child; 
    StarNode(RegexNode c){ 
        child = c; 
    }
    
    String debug(){ 
        return "(" + child.debug() + ")*"; 
    }
}

class PlusNode extends RegexNode {
    RegexNode child; 
    PlusNode(RegexNode c){ 
        child = c; 
    }
    
    String debug(){ 
        return "(" + child.debug() + ")+"; 
    }
}

class QuesNode extends RegexNode {
    RegexNode child; QuesNode(RegexNode c){ 
        child = c; 
    }
    
    String debug(){ 
        return "(" + child.debug() + ")?"; 
    }
}

class DotNode extends RegexNode { 
    String debug(){ 
        return "."; 
    } 
}

class CharClassNode extends RegexNode {
    List<Character> chars;
    CharClassNode(List<Character> cs){ 
        chars = cs; 
    }
    
    String debug(){ 
        return "[" + String.valueOf(chars.toString()) + "]"; 
    }
}


public class RegexParser {
    private String s;
    private int pos = 0; // Indice actual

    public RegexParser(String s) { 
        this.s = s; 
    }

    public RegexNode parse() throws Exception { // Funcion principal para analizar el lenguaje
        RegexNode r = parseAlt();
        if (pos < s.length()) 
            throw new Exception("Parser: leftover at pos " + pos);
        
        return r;
    }

    private RegexNode parseAlt() throws Exception { //Funciones encargadas de leer los lenguajes del usuario
        RegexNode left = parseConcat();
        while(peek() == '|') {
            consume();
            RegexNode right = parseConcat();
            left = new AltNode(left, right);
        }
        
        return left;
    }

    private RegexNode parseConcat() throws Exception { // Encargado de de recibir or
        List<RegexNode> parts = new ArrayList<>();
        while(true) {
            char c = peek();
            if(c == 0 || c == ')' || c == '|') 
                break;
            
            parts.add(parseRepeat());
        }
        
        if(parts.size() == 1) 
            return parts.get(0);
        
        return new ConcatNode(parts);
    }

    private RegexNode parseRepeat() throws Exception { // Encargado de aceptar +
        RegexNode node = parseAtom();
        while(true) {
            char c = peek();
            if (c == '*') { 
                consume(); 
                node = new StarNode(node); 
            }else if(c == '+') { 
                consume(); 
                node = new PlusNode(node); 
            }else if (c == '?') { 
                consume(); 
                node = new QuesNode(node); 
            }else {
                break;
            }
        }
        
        return node;
    }

    private RegexNode parseAtom() throws Exception {
        char c = peek();
        if(c == '(') { 
            consume(); 
            RegexNode inside = parseAlt(); 
            if(peek() != ')') 
                throw new Exception("no )"); 
            
            consume(); 
            return inside; 
        }
        
        if(c == '.') { 
            consume(); 
            return new DotNode(); 
        }
        
        if(c == '[') {
            consume(); 
            List<Character> chars = new ArrayList<>();
            while (peek() != ']') {
                char x = consume();
                chars.add(x);
            }
            
            consume(); return new CharClassNode(chars);
        }
        
        if(c == 0) 
            throw new Exception("Final inesperado");
        
        return new LiteralNode(consume());
    }

    private char peek() {
        if (pos >= s.length()) return 0;
        return s.charAt(pos);
    }
    
    private char consume() {
        return s.charAt(pos++);
    }
}

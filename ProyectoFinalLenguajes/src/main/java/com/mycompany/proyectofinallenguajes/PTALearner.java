/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.proyectofinallenguajes;
import java.util.*;
import java.io.IOException;
/**
 *
 * @author DELL
 */
public class PTALearner {
    public DFA buildPTA(List<String> positives) { // Funcion encargada de crear el Prefix tree acceptor (PTA)
        int idCounter = 0;
        State start = new State(idCounter++);
        Map<String, State> prefixToState = new HashMap<>(); // Mapa que asocia cada prefijo con su estado
        prefixToState.put("", start);

        for(String s : positives) { // Se incertan ejemplos correctos
            String pref = "";
            State cur = start;
            for(char c : s.toCharArray()) { // Se recorre caracter por caracter
                pref = pref + c;
                State next = prefixToState.get(pref);
                if(next == null) { // Si no existe, se crea uno nuevo
                    next = new State(idCounter++);
                    prefixToState.put(pref, next);
                    cur.trans.put(c, next); // Transicion a nuevo estado
                }else {
                    cur.trans.putIfAbsent(c, next);
                }
                
                cur = next;
            }
            
            cur.isAccept = true; // Se marca como estado final
        }
        return new DFA(start); // Se construye un DFA
    }

    public DFA learn(List<String> positives, List<String> negatives) { // Funcion encargada que el DFA aprenda las cadenas correctas
        DFA dfa = buildPTA(positives);

        boolean mergedAny = true;
        while(mergedAny) { // Se intenta mezclar hasta quese haya unido todo
            mergedAny = false;
            List<State> states = new ArrayList<>(dfa.states);
            states.sort(Comparator.comparingInt(a -> a.id));
            outer:
            for(int i = 0; i < states.size(); i++) {
                for(int j = i + 1; j < states.size(); j++) {
                    State a = states.get(i);
                    State b = states.get(j);
                    if(a == b) // Si es igual continua
                        continue;

                    if(a.isAccept != b.isAccept) // Contunua si uno acpeta pero el otro no
                        continue;

                    if(!areCompatible(a, b)) // Se prueba compatibiidad entre a y b
                        continue;

                    DFA copy = copyDFA(dfa);
                    boolean ok = tryMerge(copy, a.id, b.id); // Se empieza haciendo merge con una DFA copia
                    if(!ok) 
                        continue;

                    boolean anyNegAccepted = false;
                    for(String n : negatives) { // Se comprueba si el DFA acepta algun negativo
                        if (copy.accepts(n)) { 
                            anyNegAccepted = true; 
                            break; 
                        }
                    }
                    if(!anyNegAccepted) { // De no acpetar, ya se hace merge con el DFA real
                        tryMerge(dfa, a.id, b.id);
                        dfa.collectStates();
                        mergedAny = true;
                        break outer;
                    }
                }
            }
        }

        return dfa; // DFA ya aprendido
    }

    private boolean areCompatible(State a, State b) { // Funcion que se encarga de comprobar si dos estados son compatibles
        Set<String> visited = new HashSet<>();
        return areCompatibleRec(a, b, visited);
    }

    private boolean areCompatibleRec(State a, State b, Set<String> visited) {
        String key = a.id + ":" + b.id;
        if(visited.contains(key)) 
            return true; // Ya comprobado
        
        visited.add(key);

        if(a.isAccept != b.isAccept)  // Retrona false si no son compatibles
            return false;

        Set<Character> symbols = new HashSet<>(); // Se crea una union de simbolos
        symbols.addAll(a.trans.keySet());
        symbols.addAll(b.trans.keySet());

        for(char sym : symbols) { // Se revisan las transiciones
            State at = a.trans.get(sym);
            State bt = b.trans.get(sym);
            if(at == null && bt == null) 
                continue;
            
            if(at == null || bt == null)  // Si uno tiene transicion y el otro no, retorna false
                return false;
            
            if(!areCompatibleRec(at, bt, visited)) 
                return false;
        }

        return true;
    }

    private DFA copyDFA(DFA dfa) { // Funcion encargada de copiar el DFA real
        Map<Integer, State> map = new HashMap<>();
        for(State s : dfa.states) 
            map.put(s.id, new State(s.id));
        
        for(State s : dfa.states) {
            State ns = map.get(s.id);
            ns.isAccept = s.isAccept;
        }
        
        for(State s : dfa.states) {
            State ns = map.get(s.id);
            for(Map.Entry<Character, State> e : s.trans.entrySet()) {
                ns.trans.put(e.getKey(), map.get(e.getValue().id));
            }
        }
        
        return new DFA(map.get(dfa.start.id));
    }

    private boolean tryMerge(DFA dfa, int aId, int bId) { // Funcion que intenta unir estados diferentes
        Map<Integer, State> map = new HashMap<>(); // Mapeo para encontrar estados
        for(State s : dfa.states) 
            map.put(s.id, s);

        State a = map.get(aId);
        State b = map.get(bId);
        if(a == null || b == null) 
            return false;

        a.isAccept = a.isAccept || b.isAccept; // Se acepta si alguno acepta

        for(Map.Entry<Character, State> e : b.trans.entrySet()) { // Terminar transiciones de B hacia A
            char sym = e.getKey();
            State targetOfB = e.getValue();
            if(!a.trans.containsKey(sym)) { // Si ya existe transiscion, lo omitimos
                a.trans.put(sym, targetOfB);
            }
        }

        for(State s : dfa.states) { // Redirigir transiciones de B hacia A
            for(Map.Entry<Character, State> e : new ArrayList<>(s.trans.entrySet())) {
                if(e.getValue().id == bId) {
                    s.trans.put(e.getKey(), a);
                }
            }
        }

        b.trans.clear();
        dfa.collectStates();
        return true;
    }
}

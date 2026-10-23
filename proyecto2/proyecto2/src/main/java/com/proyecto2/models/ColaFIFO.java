package com.proyecto2.models;

public class ColaFIFO {
    private NodoCola frente;
    private NodoCola ultimo;
    private int tamano;

    public ColaFIFO() {
        this.frente = null;
        this.ultimo = null;
        this.tamano = 0;
    }

    //ENCOLAR: se agrega siempre por el final
    public void encolar(Vehiculo v) {
        NodoCola nuevoNodo = new NodoCola(v);

        if (estaVacia()) {
            //si esta vacia, el nuevo es tanto el frente como el ultimo.
            frente = nuevoNodo;
            ultimo = nuevoNodo;
        } else {
            // el que era el ultimo ahora apunta al nuevo.
            ultimo.setSiguiente(nuevoNodo);
            //movemos la etiqueta de "ultimo" al nuevo nodo
            ultimo = nuevoNodo;
        }
        tamano++;
    }

    //DESENCOLAR: se saca siempre por el frente
    public Vehiculo desencolar() {
        if (estaVacia()) {
            return null; //no hay nada que sacar
        }

        //guardamos el vehiculo que vamos a entregar
        Vehiculo vehiculoAtendido = frente.getDato();

        //movemos el frente al siguiente en la fila
        frente = frente.getSiguiente();

        //caso especial: si al mover el frente, la fila quedo vacia,
        //debemos "limpiar" el puntero 'ultimo' para que no apunte a un nodo fantasma.
        if (frente == null) {
            ultimo = null;
        }

        tamano--;
        return vehiculoAtendido;
    }

    //METODO EXTRA para la interfaz grafica: ver quien sigue sin sacarlo de la cola
    public Vehiculo verFrente() {
        if (estaVacia()) return null; 
        return frente.getDato();
    }
        
    public boolean estaVacia() {
        return frente == null;  

    }

    public int getTamano() {
        return tamano;
    }   

    public NodoCola getFrente() {
        return frente;
    }

}

package com.proyecto2.models;

public class PilaEventos {
    private NodoPila tope;
    private int tamano;

    public PilaEventos() {
        this.tope = null;
        this.tamano = 0;
    }

    //PUSH: apilar un nuevo evento arriba de todos
    public void push(Evento e) {
        NodoPila nuevoNodo = new NodoPila(e);
        //el nuevo nodo apunta al que antes era el tope
        nuevoNodo.setSiguiente(tope);
        //el nuevo nodo ahora es la nueva cima 
        tope = nuevoNodo;
        tamano++;
    }

    //POP: quitar el evento de arriba
    public Evento pop() {
        if (estaVacia()) {
            return null; //no hay nada que sacar
        }

        //guardamos el evento que vamos a entregar
        Evento eventoRetirado = tope.getDato();

        //el tope baja un nivel
        tope = tope.getSiguiente();
        tamano--;
        return eventoRetirado;
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int getTamano() {
        return tamano;
    }

    /**
     * IMPORTANTE: para la interfaz grafica No usamos pop, 
     * porque si hacemos pop, borrariamos el historial.
     * solo exponemos el tope para que la vista lo recorra con un puntero temporal.
     */
    public NodoPila getTope() {
        return tope;

    }
    
}

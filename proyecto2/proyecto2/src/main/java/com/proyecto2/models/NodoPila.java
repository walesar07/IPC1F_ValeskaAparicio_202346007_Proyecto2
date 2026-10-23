package com.proyecto2.models;

public class NodoPila {
    private Evento dato;
    private NodoPila siguiente;

    public NodoPila(Evento dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Evento getDato() {
        return dato;
    }

    public NodoPila getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPila siguiente) {
        this.siguiente = siguiente;
    }
    

}

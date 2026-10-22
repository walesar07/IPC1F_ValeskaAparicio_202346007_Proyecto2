package com.proyecto2.models;

public class NodoCola {
    private Vehiculo dato;
    private NodoCola siguiente; //puntero hacia adelante

    public NodoCola(Vehiculo dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    public Vehiculo getDato() {
        return dato;
    }

    public void setDato(Vehiculo dato) {
        this.dato = dato;
    }

    public NodoCola getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoCola siguiente) {
        this.siguiente = siguiente;
    }

}

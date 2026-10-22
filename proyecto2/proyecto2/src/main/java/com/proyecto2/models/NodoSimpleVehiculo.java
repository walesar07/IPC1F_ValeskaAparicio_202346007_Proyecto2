package com.proyecto2.models;

public class NodoSimpleVehiculo {

    //el dato que guarda este nodo
    private Vehiculo dato;

    //el puntero al siguiente nodo en la lista
    private NodoSimpleVehiculo siguiente;

    public NodoSimpleVehiculo(Vehiculo dato) {
        this.dato = dato;
        this.siguiente = null;//cuando nace, no apunta a nadie
    
    }
    public Vehiculo getDato() {
        return dato;
    }

    public void setDato(Vehiculo dato) {
        this.dato = dato;
    }

    public NodoSimpleVehiculo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoSimpleVehiculo siguiente) {
        this.siguiente = siguiente;
    }
    
}

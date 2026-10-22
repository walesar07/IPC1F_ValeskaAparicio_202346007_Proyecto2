package com.proyecto2.models;

public class NodoDobleResidente {
    private Residente dato;
    private NodoDobleResidente anterior; //puntero hacia atras
    private NodoDobleResidente siguiente; //puntero hacia adelante

    public NodoDobleResidente(Residente dato) {
        this.dato = dato;
        this.anterior = null;
        this.siguiente = null;
    }

    // getters and setters
    public Residente getDato() {
        return dato;
    }

    public void setDato(Residente dato) {
        this.dato = dato;
    }

    public NodoDobleResidente getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoDobleResidente anterior) {
        this.anterior = anterior;
    }

    public NodoDobleResidente getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoDobleResidente siguiente) {
        this.siguiente = siguiente;
    }

}

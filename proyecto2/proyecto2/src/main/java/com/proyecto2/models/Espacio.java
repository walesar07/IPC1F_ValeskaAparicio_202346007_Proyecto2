package com.proyecto2.models;

public class Espacio {
    private String id;
    private String tipo;//socio o general
    private Vehiculo vehiculoEstacionado;

    //puntero para la lista circular
    private Espacio siguiente;

    public Espacio(String id, String tipo) {
        this.id = id;
        this.tipo = tipo;
        this.vehiculoEstacionado = null;
        this.siguiente = null;
    }

    public boolean isLibre() {
        return vehiculoEstacionado == null;
    }

    public void ocupar(Vehiculo v) {
        this.vehiculoEstacionado = v;
    }

    public void liberar() {
        this.vehiculoEstacionado = null;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public String getTipo() {
        return tipo;
    }

    public Vehiculo getVehiculoEstacionado() {
        return vehiculoEstacionado;
    }

    public Espacio getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Espacio siguiente) {
        this.siguiente = siguiente;
    }   


}

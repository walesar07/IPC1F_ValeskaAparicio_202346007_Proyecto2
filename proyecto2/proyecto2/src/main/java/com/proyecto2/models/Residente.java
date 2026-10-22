package com.proyecto2.models;

public class Residente {
    private String id;
    private String nombre;
    private String casa;
    private boolean esSocio;

    //aqui conectamos con nuestra lista simple
    private ListaSimpleVehiculo listaVehiculos;

    public Residente(String id, String nombre, String casa, boolean esSocio) {
        this.id = id;
        this.nombre = nombre;
        this.casa = casa;
        this.esSocio = esSocio;
        this.listaVehiculos = new ListaSimpleVehiculo(); //nace con una lista vacia

    }
    public boolean isEsSocio() {
        return esSocio;
    }

    //getters basicos
    public String getId() {
        return id;
    }       

    public String getNombre() {
        return nombre;
    }

    public String getCasa() {
        return casa;
    }

    public ListaSimpleVehiculo getListaVehiculos() {
        return listaVehiculos;
    }
    
}

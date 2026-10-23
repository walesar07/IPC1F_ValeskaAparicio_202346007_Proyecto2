package com.proyecto2.models;

public class Visitante {
    private String nombre;
    private String placa;
    private String idResidenteVisita;

    public Visitante(String nombre, String placa, String idResidenteVisita) {
        this.nombre = nombre;
        this.placa = placa;
        this.idResidenteVisita = idResidenteVisita;
    }

    // Getters 
    public String getNombre() {
        return nombre;
    }


    public String getPlaca() {
        return placa;
    }


    public String getIdResidenteVisita() {
        return idResidenteVisita;
    }

}

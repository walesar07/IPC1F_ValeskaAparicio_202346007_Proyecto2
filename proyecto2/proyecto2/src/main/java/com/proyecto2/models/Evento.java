package com.proyecto2.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Evento {
    private String fechaHora;
    private String tipoEvento;
    private String descripcion;
    private String garitaInvolucrada;

    public Evento(String tipoEvento, String descripcion, String garitaInvolucrada) {
       
       
       //generar la fecha y hora automaticamente al crear el evento
       LocalDateTime ahora= LocalDateTime.now();
       DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
       
        this.fechaHora = ahora.format(formato);

        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.garitaInvolucrada = garitaInvolucrada;
    }

    public String getFechaHora() {
        return fechaHora;
    } 
    
    public String getTipoEvento() {
        return tipoEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getGaritaInvolucrada() {
        return garitaInvolucrada;
    }

    //este metodo nos servira para mostrarlo bonito en el panel de eventos
    @Override
    public String toString() {
        String garita = (garitaInvolucrada != null && !garitaInvolucrada.isEmpty()) ? " | " + garitaInvolucrada : "";
        return "[" + fechaHora + "] " + tipoEvento + ": " + descripcion + garita;
    }



}

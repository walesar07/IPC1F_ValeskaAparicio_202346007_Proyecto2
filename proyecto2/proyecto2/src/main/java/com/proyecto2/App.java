package com.proyecto2;

import com.proyecto2.controllers.ControladorPrincipal;

public class App {
    public static void main(String[] args) {
        ControladorPrincipal controlador = new ControladorPrincipal();
        controlador.cargarDatos(); // cargar los .txt si existen

        
    }
}

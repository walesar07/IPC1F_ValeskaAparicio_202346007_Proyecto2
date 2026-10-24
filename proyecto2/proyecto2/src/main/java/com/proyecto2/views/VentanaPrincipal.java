package com.proyecto2.views;

import com.proyecto2.controllers.ControladorPrincipal;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class VentanaPrincipal extends JFrame {
    private ControladorPrincipal controlador;

    private PanelParqueo panelParqueo;
    private PanelResidentes panelResidentes;
    private PanelEntrada panelEntrada;
    private PanelSalida panelSalida;
    private PanelEventos panelEventos;

    private JTabbedPane pestanas;

    public VentanaPrincipal(ControladorPrincipal controlador) {
        this.controlador = controlador;

        //Configuracion basica de la ventana
        setTitle("ResiPark - Control de Accesos y Estacionamiento");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null); // centrar en la panttalla

        //Inicializar los paneles (les pasamos el controlador para que puedan pedir datos
        panelParqueo = new PanelParqueo(controlador);
        panelResidentes = new PanelResidentes(controlador);
        panelEntrada = new PanelEntrada(controlador);
        panelSalida = new PanelSalida(controlador);
        panelEventos = new PanelEventos(controlador);

        //crear las pestanas
        pestanas = new JTabbedPane();
        pestanas.addTab("Parqueo", panelParqueo);
        pestanas.addTab("Residentes y Vehiculos", panelResidentes);
        pestanas.addTab("Entrada", panelEntrada);
        pestanas.addTab("Salida", panelSalida);
        pestanas.addTab("Eventos", panelEventos);

        add(pestanas);

        //REGLA: Guarddar automaticamente al cerrar la ventana
        addWindowListener(new WindowAdapter() {
            @Override 
            public void windowClosing(WindowEvent e){
                System.out.println("Cerrando ventana... guardando datos.");

                controlador.guardarDatos();
                controlador.detenerSimulacion();

                //cerramos el programa a la fuerza de forma segura
                dispose();
                System.exit(0);
            }
        });

    }

    //Este metodo sera llamado desde App.java (a traves del Controlador) cada vez que haya un cambio
    public void actualizarPaneles() {
        panelParqueo.actualizar();
        panelEntrada.actualizar();
        panelSalida.actualizar();
        panelEventos.actualizar();
        panelResidentes.actualizar();
    }

}

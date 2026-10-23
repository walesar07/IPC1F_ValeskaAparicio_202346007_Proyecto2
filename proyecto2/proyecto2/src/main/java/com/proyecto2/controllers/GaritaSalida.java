package com.proyecto2.controllers;

import com.proyecto2.models.*;

public class GaritaSalida extends Thread {
    private ColaFIFO colaSalida;
    private Parqueo parqueo;
    private PilaEventos historial;
    private Vehiculo vehiculoActual;
    private Runnable onCambioVista;
    private volatile boolean ejecutando = true;

    public GaritaSalida(ColaFIFO colaSalida, Parqueo parqueo, PilaEventos historial, Runnable onCambioVista) {
        this.colaSalida = colaSalida;
        this.parqueo = parqueo;
        this.historial = historial;
        this.onCambioVista = onCambioVista;
    }

    @Override
    public void run() {
        while (ejecutando) {
            Vehiculo vehiculoAAtender = null;

            synchronized (colaSalida) {
                while (colaSalida.estaVacia() && ejecutando) {
                    try {
                        colaSalida.wait();
                    } catch (InterruptedException e) {
                        System.out.println("Garita de salida interrumpida.");
                    }
                }
                
                if (ejecutando && !colaSalida.estaVacia()) {
                    vehiculoAAtender = colaSalida.desencolar();
                }
            }

            if (vehiculoAAtender == null) continue;

            try {
                vehiculoActual = vehiculoAAtender;
                actualizarInterfaz();
                Thread.sleep(1500); // Tiempo de atención de la garita
            } catch (InterruptedException e) {
                System.out.println("Error en sleep de salida");
            }

            // Liberación del espacio
            synchronized (parqueo) {
                boolean liberado = parqueo.liberarEspacio(vehiculoAAtender.getPlaca());
                
                if (liberado) {
                    vehiculoAAtender.setEstado(EstadoVehiculo.FUERA);
                    synchronized (historial) {
                        historial.push(new Evento("SALIDA", 
                            "Vehículo " + vehiculoAAtender.getPlaca() + " salió. Espacio liberado.", 
                            "Garita Salida"));
                    }
                } else {
                    // Esto no debería pasar si la lógica es correcta
                    synchronized (historial) {
                        historial.push(new Evento("ERROR", 
                            "No se encontró espacio para " + vehiculoAAtender.getPlaca(), 
                            "Garita Salida"));
                    }
                }
            }

            vehiculoActual = null;
            actualizarInterfaz();
        }
    }

    private void actualizarInterfaz() {
        if (onCambioVista != null) {
            onCambioVista.run();
        }
    }

    public Vehiculo getVehiculoActual() {
        return vehiculoActual;
    }

    public void detener() {
        this.ejecutando = false;
        synchronized (colaSalida) {
            colaSalida.notifyAll();
        }
    }
}

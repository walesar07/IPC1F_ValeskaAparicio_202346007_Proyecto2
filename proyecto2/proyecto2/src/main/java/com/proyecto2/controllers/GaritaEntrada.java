package com.proyecto2.controllers;

import com.proyecto2.models.*;

public class GaritaEntrada extends Thread{
    private int numero;
    private ColaFIFO colaEntrada;
    private Parqueo parqueo;
    private PilaEventos historial;
    private Vehiculo vehiculoActual;
    private Runnable onCambioVista;
    private volatile boolean ejecutando = true;

    public GaritaEntrada(int numero, ColaFIFO colaEntrada, Parqueo parqueo, PilaEventos historial, Runnable onCambioVista){
        this.numero = numero;
        this.colaEntrada = colaEntrada;
        this.parqueo = parqueo;
        this.historial = historial;
        this.onCambioVista = onCambioVista;
    }

    @Override 
    public void run() {
        while (ejecutando){
            Vehiculo vehiculoAAtender = null;

            //1. Sincronizamos el acceso a la cola
            synchronized (colaEntrada){
                //2. EL CICLO WHILE ES OBLIGATORIO (el proyecto lo exige)
                while(colaEntrada.estaVacia() && ejecutando){
                    try {
                        colaEntrada.wait();// el hilo duerme hasta que le avisan
                    } catch (InterruptedException e){
                        System.out.println("Garita " + numero + " interrumpida.");
                
                    }
                }
                
                //3. cuando hay un vehiculo, lo sacamos de la cola
                if (ejecutando && !colaEntrada.estaVacia()) {
                    vehiculoAAtender = colaEntrada.desencolar();
                    vehiculoAAtender.setEstado(EstadoVehiculo.EN_COLA_ENTRADA);//Temporal
                
                }
            }

            //si no hay vehiculos (porque se detuve el hilo), continuamos
            if (vehiculoAAtender ==  null) continue;

            //4. Fuera del synchronized, simulamos el tiempo de atencion.
            try {
                vehiculoActual = vehiculoAAtender;
                actualizarInterfaz();
                Thread.sleep(1500); // 1.5 segundos de atencion (entre 1 y 3 como pide el proyecto)
            } catch (InterruptedException e) {
                System.out.println("Error en sleep");

            }

            //5. Asignacion en el parqueo (sincronizado)
            synchronized (parqueo) {
                Espacio espacio = parqueo.asignarEspacio(vehiculoAAtender);

                if ( espacio != null) {
                    //Exito
                    vehiculoAAtender.setEstado(EstadoVehiculo.ESTACIONADO);
                    synchronized (historial){
                        historial.push(new Evento("ASIGNACION", "Vehiculo " + vehiculoAAtender.getPlaca() + "asignado a " + espacio.getId(),
                    "Garita " + numero));
                    }

                } else {
                    //rechazo por parqueo lleno
                    vehiculoAAtender.setEstado(EstadoVehiculo.FUERA);
                    synchronized (historial){
                        historial.push(new Evento("RECHAZO", "Vehiculo " + vehiculoAAtender.getPlaca() + " rechazado: Area llena",
                    "Garita " + numero));

                    }
                }
            }

            //6. Limpiamos y actualizamos la vista
            vehiculoActual = null;
            actualizarInterfaz();
        
            
        }
    }

    //Metodo auxiliar para actualizar la UI de forma segura
    private void actualizarInterfaz() {
        if (onCambioVista != null) {
            onCambioVista.run(); // Esto ya viene envuelto en un SwingUtiliities.invokeLater desde el Controlador

        }
    }

    public Vehiculo getVehiculoActual() {
        return vehiculoActual;

    }

    public void detener() {
        this.ejecutando = false;
        synchronized (colaEntrada) {
            colaEntrada.notifyAll(); //Despertamos para que evalue el while y termine

        }
    }

}

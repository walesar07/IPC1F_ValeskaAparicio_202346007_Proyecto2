package com.proyecto2.controllers;

import com.proyecto2.models.*;
import com.proyecto2.persistencia.PersistenciaArchivos;

public class ControladorPrincipal {
    private ListaDobleResidentes residentes;
    private Parqueo parqueo;
    private ColaFIFO colaEntrada;
    private ColaFIFO colaSalida;
    private PilaEventos historial;
    private PersistenciaArchivos persistencia;

    private GaritaEntrada garita1;
    private GaritaEntrada garita2;
    private GaritaSalida garitaSalida;

   // Para comunicarnos con la vista de forma segura
   private Runnable actualizarVistaCallback;

   private ControladorPrincipal() {
    this.residentes = new ListaDobleResidentes();
    this.parqueo = new Parqueo();
    this.colaEntrada = new ColaFIFO();
    this.colaSalida = new ColaFIFO();
    this.historial = new PilaEventos();
    this.persistencia = new PersistenciaArchivos();

   }

   //Se llamara desde la vista una vez que la ventana este lista
   public void setActualizarVistaCallback(Runnable callback) {
    this.actualizarVistaCallback = callback;

   }

   public void iniciarSimulacion() {
    //creamos los hilos pasandoles las MISMAS referencias
    //Esto es vital: todos operan sobre las mismas colas y el mismo parqueo
    garita1 = new GaritaEntrada(1, colaEntrada, parqueo, historial, actualizarVistaCallback);
    garita2 = new GaritaEntrada(2, colaEntrada, parqueo, historial, actualizarVistaCallback);
    garitaSalida = new GaritaSalida(colaSalida, parqueo, historial, actualizarVistaCallback);

    //Arrancamos los hilos 
    garita1.start();
    garita2.start();
    garitaSalida.start();
   }

   public void detenerSimulacion() {
    if( garita1 != null) garita1.detener();
    if (garita2 != null) garita2.detener();
    if (garitaSalida != null) garitaSalida.detener();

   }

   
     
    // MÉTODOS DE NEGOCIO (Llamados por la Vista)
    

    public boolean registrarResidente(Residente r) {
        if (r.getId().contains("|") || r.getNombre().contains("|") || r.getCasa().contains("|")) {
            return false; // El PDF prohíbe el carácter | en los campos
        }
        return residentes.insertar(r);
    }

    public boolean registrarVehiculo(String idResidente, Vehiculo v) {
        if (v.getPlaca().contains("|") || existePlacaEnSistema(v.getPlaca())) {
            return false; // Placa única en todo el sistema
        }
        
        Residente r = residentes.buscar(idResidente);
        if (r == null) return false;

        v.setPropietario(r);
        return r.getListaVehiculos().insertar(v);
    }

    // MÉTODO CRÍTICO: Enviar a cola de entrada
    public boolean enviarVehiculoAEntrada(String placa) {
        // 1. Buscar el vehículo entre todos los residentes (simplificado para el ejemplo)
        Vehiculo v = buscarVehiculoEnResidentes(placa);
        
        if (v == null || v.getEstado() != EstadoVehiculo.FUERA) {
            return false; // Solo se encolan vehículos FUERA
        }

        // 2. Cambiar estado y encolar
        v.setEstado(EstadoVehiculo.EN_COLA_ENTRADA);
        
        synchronized (colaEntrada) {
            colaEntrada.encolar(v);
            // 3. Avisar a las garitas (Despertar hilos)
            colaEntrada.notifyAll(); 
        }

        // 4. Registrar evento
        synchronized (historial) {
            historial.push(new Evento("EN COLA", "Vehículo " + placa + " en cola de entrada", null));
        }

        if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        return true;
    }

    // MÉTODO CRÍTICO: Solicitar salida
    public boolean solicitarSalida(String placa) {
        Vehiculo v = buscarVehiculoEnResidentes(placa);
        
        if (v == null || v.getEstado() != EstadoVehiculo.ESTACIONADO) {
            return false; // Solo se pueden salir vehículos ESTACIONADOS
        }

        v.setEstado(EstadoVehiculo.EN_COLA_SALIDA);
        
        synchronized (colaSalida) {
            colaSalida.encolar(v);
            colaSalida.notifyAll(); // Despertar garita de salida
        }

        synchronized (historial) {
            historial.push(new Evento("EN COLA", "Vehículo " + placa + " en cola de salida", null));
        }

        if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        return true;
    }

    public boolean registrarVisitante(Visitante visitante) {
        if (visitante.getPlaca().contains("|") || existePlacaEnSistema(visitante.getPlaca())) {
            return false;
        }
        
        Residente residenteVisitado = residentes.buscar(visitante.getIdResidenteVisita());
        if (residenteVisitado == null) return false; // El residente debe existir

        Vehiculo v = new Vehiculo(visitante.getPlaca(), "N/A", "N/A", "N/A", "Automovil");
        v.setVisitante(visitante);
        v.setEstado(EstadoVehiculo.EN_COLA_ENTRADA);

        synchronized (colaEntrada) {
            colaEntrada.encolar(v);
            colaEntrada.notifyAll();
        }

        synchronized (historial) {
            historial.push(new Evento("VISITANTE", "Visitante " + visitante.getNombre() + " registrado. Placa: " + visitante.getPlaca(), null));
        }

        if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        return true;
    }

    // Método auxiliar para buscar placas
    public boolean existePlacaEnSistema(String placa) {
        return buscarVehiculoEnResidentes(placa) != null;
    }

    private Vehiculo buscarVehiculoEnResidentes(String placa) {
        NodoDobleResidente actual = residentes.getPrimero();
        while (actual != null) {
            Vehiculo v = actual.getDato().getListaVehiculos().buscar(placa);
            if (v != null) return v;
            actual = actual.getSiguiente();
        }
        return null;
    }

    public void guardarDatos() {
        persistencia.guardarDatos(residentes);
    }

    public void cargarDatos() {
        this.residentes = persistencia.cargarDatos(historial);
    }

    // Getters para que la Vista pueda leer los datos
    public ListaDobleResidentes getResidentes() { return residentes; }
    public Parqueo getParqueo() { return parqueo; }
    public ColaFIFO getColaEntrada() { return colaEntrada; }
    public ColaFIFO getColaSalida() { return colaSalida; }
    public PilaEventos getHistorial() { return historial; }
    public GaritaEntrada getGarita1() { return garita1; }
    public GaritaEntrada getGarita2() { return garita2; }
    public GaritaSalida getGaritaSalida() { return garitaSalida; }
}


     





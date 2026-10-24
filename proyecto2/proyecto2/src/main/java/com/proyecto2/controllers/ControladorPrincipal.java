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

    // Callback para avisar a la Vista que debe redibujarse
    private Runnable actualizarVistaCallback;

    public ControladorPrincipal() {
        this.residentes = new ListaDobleResidentes();
        this.parqueo = new Parqueo();
        this.colaEntrada = new ColaFIFO();
        this.colaSalida = new ColaFIFO();
        this.historial = new PilaEventos();
        this.persistencia = new PersistenciaArchivos();
    }

    public void setActualizarVistaCallback(Runnable callback) {
        this.actualizarVistaCallback = callback;
    }

    public void iniciarSimulacion() {
        garita1 = new GaritaEntrada(1, colaEntrada, parqueo, historial, actualizarVistaCallback);
        garita2 = new GaritaEntrada(2, colaEntrada, parqueo, historial, actualizarVistaCallback);
        garitaSalida = new GaritaSalida(colaSalida, parqueo, historial, actualizarVistaCallback);

        garita1.start();
        garita2.start();
        garitaSalida.start();
    }

    public void detenerSimulacion() {
        if (garita1 != null) garita1.detener();
        if (garita2 != null) garita2.detener();
        if (garitaSalida != null) garitaSalida.detener();
    }

    // ==========================================
    // GESTIÓN DE RESIDENTES Y VEHÍCULOS
    // ==========================================

    public boolean registrarResidente(Residente r) {
        if (r.getId().contains("|") || r.getNombre().contains("|") || r.getCasa().contains("|")) {
            return false;
        }
        
        boolean insertado = residentes.insertar(r);
        if (insertado && actualizarVistaCallback != null) {
            actualizarVistaCallback.run();
        }
        return insertado;
    }

    public boolean modificarResidente(String id, String nuevoNombre, String nuevaCasa, boolean nuevoEsSocio) {
        Residente r = residentes.buscar(id);
        if (r == null) return false;

        // REGLA DEL PDF: Si cambia de estatus (socio/no socio), validar vehículos
        if (r.isEsSocio() != nuevoEsSocio) {
            if (!r.puedeCambiarSocio()) {
                return false; // Tiene vehículos adentro/fuera, no se puede cambiar
            }
        }

        r.setNombre(nuevoNombre);
        r.setCasa(nuevaCasa);
        r.setEsSocio(nuevoEsSocio);
        
        synchronized (historial) {
            historial.push(new Evento("MODIFICACIÓN", "Residente modificado: " + id, null));
        }
        if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        return true;
    }

    public boolean eliminarResidente(String id) {
        boolean eliminado = residentes.eliminar(id);
        if (eliminado) {
            synchronized (historial) {
                historial.push(new Evento("ELIMINACIÓN", "Residente eliminado: " + id, null));
            }
            if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        }
        return eliminado;
    }

    public boolean registrarVehiculo(String idResidente, Vehiculo v) {
        if (v.getPlaca().contains("|") || existePlacaEnSistema(v.getPlaca())) {
            return false;
        }
        
        Residente r = residentes.buscar(idResidente);
        if (r == null) return false;

        v.setPropietario(r);
        boolean insertado = r.getListaVehiculos().insertar(v);

        if (insertado) {
            synchronized (historial) {
                historial.push(new Evento("VEHÍCULO REGISTRADO", "Vehículo " + v.getPlaca() + " agregado al residente " + idResidente, null));
            }
            if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        }

        return insertado;
    }

    public boolean eliminarVehiculo(String idResidente, String placa) {
        Residente r = residentes.buscar(idResidente);
        if (r == null) return false;

        Vehiculo v = r.getListaVehiculos().buscar(placa);
        if (v == null) return false;

        // REGLA DEL PDF: Solo se elimina si está FUERA
        if (v.getEstado() != EstadoVehiculo.FUERA) return false;

        boolean eliminado = r.getListaVehiculos().eliminar(placa);
        if (eliminado) {
            synchronized (historial) {
                historial.push(new Evento("VEHÍCULO ELIMINADO", "Vehículo " + placa + " eliminado del residente " + idResidente, null));
            }
            if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        }
        return eliminado;
    }

    // ==========================================
    // FLUJO DE ENTRADA Y SALIDA (HILOS)
    // ==========================================

    public boolean enviarVehiculoAEntrada(String placa) {
        Vehiculo v = buscarVehiculoGlobal(placa);
        
        if (v == null || v.getEstado() != EstadoVehiculo.FUERA) {
            return false; 
        }

        v.setEstado(EstadoVehiculo.EN_COLA_ENTRADA);
        
        synchronized (colaEntrada) {
            colaEntrada.encolar(v);
            colaEntrada.notifyAll(); 
        }

        synchronized (historial) {
            historial.push(new Evento("EN COLA", "Vehículo " + placa + " en cola de entrada", null));
        }

        if (actualizarVistaCallback != null) actualizarVistaCallback.run();
        return true;
    }

    public boolean solicitarSalida(String placa) {
        Vehiculo v = buscarVehiculoGlobal(placa);
        
        if (v == null || v.getEstado() != EstadoVehiculo.ESTACIONADO) {
            return false; 
        }

        v.setEstado(EstadoVehiculo.EN_COLA_SALIDA);
        
        synchronized (colaSalida) {
            colaSalida.encolar(v);
            colaSalida.notifyAll(); 
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
        if (residenteVisitado == null) return false; 

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

    // ==========================================
    // MÉTODOS AUXILIARES Y PERSISTENCIA
    // ==========================================

    public boolean existePlacaEnSistema(String placa) {
        return buscarVehiculoGlobal(placa) != null;
    }

    public Vehiculo buscarVehiculoGlobal(String placa) {
        // 1. Buscar en residentes
        NodoDobleResidente actual = residentes.getPrimero();
        while (actual != null) {
            Vehiculo v = actual.getDato().getListaVehiculos().buscar(placa);
            if (v != null) return v;
            actual = actual.getSiguiente();
        }
        
        // 2. Buscar en parqueo (Socios)
        if (parqueo.getAreaSocios().buscarOcupado(placa) != null) {
            return parqueo.getAreaSocios().buscarOcupado(placa).getVehiculoEstacionado();
        }
        
        // 3. Buscar en parqueo (General/Visitantes)
        if (parqueo.getAreaGeneral().buscarOcupado(placa) != null) {
            return parqueo.getAreaGeneral().buscarOcupado(placa).getVehiculoEstacionado();
        }
        
        return null; 
    }

    public void guardarDatos() {
        persistencia.guardarDatos(residentes);
    }

    public void cargarDatos() {
        this.residentes = persistencia.cargarDatos(historial);
    }

    // ==========================================
    // GETTERS PARA LA VISTA
    // ==========================================

    public ListaDobleResidentes getResidentes() { return residentes; }
    public Parqueo getParqueo() { return parqueo; }
    public ColaFIFO getColaEntrada() { return colaEntrada; }
    public ColaFIFO getColaSalida() { return colaSalida; }
    public PilaEventos getHistorial() { return historial; }
    public GaritaEntrada getGarita1() { return garita1; }
    public GaritaEntrada getGarita2() { return garita2; }
    public GaritaSalida getGaritaSalida() { return garitaSalida; }
}
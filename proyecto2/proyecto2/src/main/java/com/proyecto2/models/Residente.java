package com.proyecto2.models;

public class Residente {
    private String id;
    private String nombre;
    private String casa;
    private boolean esSocio;

    //aqui conectamos con nuestra lista simple
    private ListaSimpleVehiculos listaVehiculos;

    public Residente(String id, String nombre, String casa, boolean esSocio) {
        this.id = id;
        this.nombre = nombre;
        this.casa = casa;
        this.esSocio = esSocio;
        this.listaVehiculos = new ListaSimpleVehiculos(); //nace con una lista vacia

    }

    //regla: no se puede eliminar si un vehiculo no esta FUERA
    public boolean puedeEliminarse() {
        NodoSimpleVehiculo actual = listaVehiculos.getPrimero();

        while(actual != null) {
            if(actual.getDato().getEstado() != EstadoVehiculo.FUERA) {
                return false; //hay un vehiculo adentro o en cola
            }
            actual = actual.getSiguiente();
        }

        return true; //si recorrimos toda la lista y todos estan FUERA, si se puede eliminar
    }

    //regla: no se puede cambiar de socio si un vehiculo no esta FUERA
    public boolean puedeCambiarSocio(){
        return puedeEliminarse(); //mismo criterio que eliminarse
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

    public boolean isEsSocio() {
        return esSocio;
    }

    public void setEsSocio(boolean esSocio) {
        this.esSocio = esSocio;
    }

    public ListaSimpleVehiculos getListaVehiculos() {
        return listaVehiculos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCasa(String casa) {
        this.casa = casa;
    }
    
}

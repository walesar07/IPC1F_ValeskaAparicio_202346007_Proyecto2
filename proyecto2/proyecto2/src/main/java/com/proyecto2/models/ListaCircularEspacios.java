package com.proyecto2.models;

public class ListaCircularEspacios {
    private Espacio primero;
    private Espacio ultimo;
    private Espacio ultimoAsignado; // Nuevo atributo para rastrear el último espacio asignado
    private int tamano;

    public ListaCircularEspacios() {
        this.primero = null;
        this.ultimo = null;
        this.ultimoAsignado = null; // Inicializar el último espacio asignado como null
        this.tamano = 0;
    }

    public void agregar(Espacio nuevo){
        nuevo.setSiguiente(nuevo); // al inicio, se apunta a si mismo

        if (primero == null) {
            primero = nuevo;
            ultimo = nuevo;
        } else {
            nuevo.setSiguiente(primero); //el nuevo apunta al inicio
            ultimo.setSiguiente(nuevo); //el ultimo actual apunta al nuevo
            ultimo = nuevo; //movemos el puntero de ultimo
        }            
        tamano++;
    }

    //METODO CRITICO (para posible prueba en vivo)
    public Espacio asignar(Vehiculo v){
        if (primero == null) return null; //no hay espacios en esta area
        

        Espacio inicio;
        //si es la primera vez que se asigna, empezamos en el primero.
        //si no, empezamos en el SIGUIENTE del ultimo asignado.
        if (ultimoAsignado == null) {
            inicio = primero;
        } else {
            inicio = ultimoAsignado.getSiguiente();
        }

        Espacio actual = inicio;
        //recorremos una sola vuelta completa usando do-while
        do {
            if (actual.isLibre()){
                actual.ocupar(v);
                ultimoAsignado = actual; // guardamos donde nos quedamos
                return actual;//retornamos el espacio asignado
            }
            actual = actual.getSiguiente();
        } while (actual != inicio); //si vuelve al inicio, ya dimoos la vuelta

        return null; //si salimos del while, el area esta llena
    }
    
    public boolean liberar(String placa){
        if (primero == null) return false; //no hay espacios en esta area

        Espacio actual = primero;
        do {
            if (!actual.isLibre() && actual.getVehiculoEstacionado().getPlaca().equals(placa)){
                actual.liberar();
                return true; //liberamos el espacio y retornamos true
            }
            actual = actual.getSiguiente();
        } while (actual != primero); //si vuelve al inicio, ya dimos la vuelta

        return false; //si salimos del while, no encontramos el vehiculo
    }

    public Espacio buscarOcupado(String placa){
        if (primero == null) return null; //no hay espacios en esta area

        Espacio actual = primero;
        do {
            if (!actual.isLibre() && actual.getVehiculoEstacionado().getPlaca().equals(placa)){
                return actual; //retornamos el espacio ocupado por el vehiculo
            }
            actual = actual.getSiguiente();
        } while (actual != primero); //si vuelve al inicio, ya dimos la vuelta

        return null; //si salimos del while, no encontramos el vehiculo
    }

    public Espacio getPrimero() {
        return primero;
    }
    
}

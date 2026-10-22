package com.proyecto2.models;

public class ListaSimpleVehiculos {

    //referencia al primer nodo de la lista. si es null, la lista esta vacia
    private NodoSimpleVehiculo primero;
    private int tamano;

    public ListaSimpleVehiculos() {
        this.primero = null;
        this.tamano = 0;
    }

    //Regla: maximo 3 vehiculos por residente.
    public boolean insertar(Vehiculo v) {
        if (tamano>=3) {
            return false;//rechaza el cuarto vehiculo
        }
        NodoSimpleVehiculo nuevoNodo = new NodoSimpleVehiculo(v);

        if(primero == null) {
            //caso 1: la lista esta vacia. el nuevo nodo se vuelve el primero.
            primero = nuevoNodo;
        } else {
            //caso2: la lista ya tiene nodos. Debemos caminar hasta el final.
            NodoSimpleVehiculo actual = primero;
            while(actual.getSiguiente() != null) { //mientras no sea el ultimo ...
                actual = actual.getSiguiente(); //...avanza al siguiente
            }
            //cuando el ciclo termina, actual es el ultimo. lo enlazamos al nuevo.
            actual.setSiguiente(nuevoNodo);
        }

        tamano++;
        return true;    

    }
    public Vehiculo buscar(String placa) {
        NodoSimpleVehiculo actual = primero;

        // recorremos la lista hasta que nos quedemos sin nodos
        while(actual != null) {

            //si el vehiculo de este nodo tiene la placa que buscamos, lo devolvemos
            if(actual.getDato().getPlaca().equals(placa)) {
                return actual.getDato();
            }

            //si no, avanzamos al siguiente
            actual = actual.getSiguiente();
        }
        return null; //si terminamos el ciclo y no lo encontramos
    }

    public boolean eliminar(String placa){
        //si la lista esta vacia, no hay nada que eliminar
        if(primero == null) return false;

        //caso 1: el vehiculo a eliminar es el primero de la lista
        if(primero.getDato().getPlaca().equals(placa)) {
            primero = primero.getSiguiente(); //el segundo pasa a ser el primero
            tamano--;
            return true;
        }
         
        caso 2: el vehiculo a eliminar esta en el medio o al final
        NodoSimpleVehiculo actual = primero;

        //necesitamos un puntero "anterior" para saltaros el nodo a eliminar
        while(actual.getSiguiente() != null) {
            if(actual.getSiguiente().getDato().getPlaca().equals(placa)) {
                //encontramos el anterior. Ahora lo saltamos por encima del que vamos a eliminar
                //anterior.siguiente ahoa apunta al siguiente del siguiente.
                actual.setSiguiente(actual.getSiguiente().getSiguiente());
                tamano--;
                return true;
            }
            actual = actual.getSiguiente();
        }

        return false; // no se encontro.
    }

    public int getTamano() {
        return tamano;
    }
    
    public NodoSimpleVehiculo getPrimero() {
        return primero;
    }   

}

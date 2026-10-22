package com.proyecto2.models;

public class ListaDobleResidentes {
    private NodoDobleResidente primero;
    private NodoDobleResidente ultimo; //nos ahorra recorrer toda la lista para insertar

    public ListaDobleResidentes() {
        this.primero = null;
        this.ultimo = null;
    }

    public boolean insertar(Residente r){

        //validacion: el ID debe ser unico.
        if(buscar(r.getId()) != null){
            return false;
        }

        NodoDobleResidente nuevoNodo = new NodoDobleResidente(r);

        if(primero == null) {
            //caso 1: la lista esta vacia. Primero y ultimo son el mismo nodo.
            primero = nuevoNodo;
            ultimo = nuevoNodo;
        } else {
            //caso2: insertar al final
            //el nuevo nodo mira hacia atras al que era el ultimo
            nuevoNodo.setAnterior(ultimo);

            //el que era ultimo ahora mira hacia adelante al nuevo.
            ultimo.setSiguiente(nuevoNodo);

            //actualizamos nuestra refrencia de ultimo.
            ultimo = nuevoNodo;

        }
        return true;
    
    }

    public Residente buscar(String id){
        NodoDobleResidente actual = primero;

        while(actual != null) {
            if(actual.getDato().getId().equals(id)) {
                return actual.getDato();
            }
            actual = actual.getSiguiente(); //avanzamos
        }
        return null; //no encontrado
    }

    //el metodo eliminar es el mas complejo (y en algunos casos el que mas preguntan)
    public boolean eliminar(String id) {
        NodoDobleResidente actual = primero;

        //1. buscar el nodo a eliminar
        while(actual != null && !actual.getDato().getId().equals(id)    ) {
            actual = actual.getSiguiente();
        }
    
        if(actual == null ) return false; //no existe.
            
        //2. verificar regla de negocio
        if(!actual.getDato().puedeEliminarse()) {
            return false; //no se puede eliminar si tiene vehiculos adentro/fuera
        }

        //3. reasignar punteros (3 casos posibles)
        if(actual == primero && actual == ultimo) {
            //caso A: es el unico nodo de la lista
            primero = null;
            ultimo = null;

        } else if(actual == primero) {
            //caso B: es el primer nodo de la lista(pero hay mas)
            primero = actual.getSiguiente(); 
            primero.setAnterior(null); //el nuevo primero no tiene a nadie atras
            
        } else if(actual == ultimo) {
            //caso C: es el ultimo nodo de la lista
            ultimo = actual.getAnterior(); 
            ultimo.setSiguiente(null); //el nuevo ultimo ya no tiene a nadie adelante

        } else {
            //caso D : esta en medio de la lista
            //el nodo anterior al que queremos borrar, se ssalta y apunta al siguiente

            actual.getAnterior().setSiguiente(actual.getSiguiente());
            //el nodo siguiente al que queremos borrar, retrocede y apunta al anterior.

            actual.getSiguiente().setAnterior(actual.getAnterior());
    
        }

        return true;

    }

    public NodoDobleResidente getPrimero() {
        return primero;
    }

}

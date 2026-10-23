package com.proyecto2.models;

public class Parqueo {

    private ListaCircularEspacios areaSocios;
    private ListaCircularEspacios areaGeneral;

    public Parqueo() {
        this.areaSocios = new ListaCircularEspacios();
        this.areaGeneral = new ListaCircularEspacios();
        inicializarEspacios();

    }

    private void inicializarEspacios(){
        //AREA SOCIOS: A, B, C (25 CADA UNA)
        String[] filasSocios = {"A", "B", "C"};
        for (String fila : filasSocios) {
            for (int i = 1; i <= 25; i++) {
                areaSocios.agregar(new Espacio(fila + i, "Socio"));
            }
        }

        //AREA GENERAL: E, F, G, H, I (15 CADA UNA)
        String[] filasGeneral = {"E", "F", "G", "H", "I"};
        for (String fila : filasGeneral) {
            for (int i = 1; i <= 15; i++) {
                areaGeneral.agregar(new Espacio(fila + i, "General"));
            }
        }
    }

    //REGLA: socio intenta en socios, si esta lleno, va a general.
    public Espacio asignarEspacio(Vehiculo v){
        if(v.getTipoUsuario() == TipoUsuario.SOCIO){
            Espacio asignado = areaSocios.asignar(v);
            if(asignado != null) return asignado; // se pudo en socios
            return areaGeneral.asignar(v); // si no, intenta en general
        }
        else{
            //no socios y visitantes SOLO en general
            return areaGeneral.asignar(v);

        }
    }

    public boolean liberarEspacio(String placa){
        //buscamos en socios primero, si no esta, en general.
        if (areaSocios.liberar(placa)) return true;
        return areaGeneral.liberar(placa);
    }

    public ListaCircularEspacios getAreaSocios() {return areaSocios; }
    public ListaCircularEspacios getAreaGeneral() {return areaGeneral; }

}

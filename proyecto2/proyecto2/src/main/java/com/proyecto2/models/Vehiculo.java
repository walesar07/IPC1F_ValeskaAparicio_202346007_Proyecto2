package com.proyecto2.models;

public class Vehiculo {

    private String placa;
    private String marca;
    private String modelo;
    private String color;
    private String tipoVehiculo; // automovil, motocicleta, pickup, etc...
    
    private EstadoVehiculo estado;

    //Referencia para saber de quien es el vehiculo
    private Residente propietario;
    private Visitante visitante;

    public Vehiculo(String placa, String marca, String modelo, String color, String tipoVehiculo) {
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.color = color;
        this.tipoVehiculo = tipoVehiculo;
        this.estado = EstadoVehiculo.FUERA;// todo vehiculo nace FUERA
        this.propietario = null;
        this.visitante = null;
    }
    //LOGICA CRITICA PARA LA PRUEBA EN VIVO
    /**
     * Este metodo deduce el tipo de usuario SIN guardar
     * un dato extra que pueda quedar obsoleto.
     */
    public TipoUsuario getTipoUsuario() {
        if (this.visitante != null) {
            return TipoUsuario.VISITANTE;
        }
        if (this.propietario != null) {
           if(this.propietario.isEsSocio()) {
               return TipoUsuario.SOCIO;
           } else {
               return TipoUsuario.NO_SOCIO;
           }
        } 
         return null; // no deberia pasar si esta bien registrado

    }
    //getters y setters
    public String getPlaca() {
        return placa;
    }   
    // no se coloca setplaca porque la placa es unica e inmutable.

    public String getMarca() {
        return marca;
    }   

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }       
        
    public String getColor() {
        return color;
    }   

    public void setColor(String color) {
        this.color = color;
    }

    public String getTipoVehiculo() {
        return tipoVehiculo;
    }   

    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public void setEstado(EstadoVehiculo estado) {
        this.estado = estado;
    }   

    public Residente getPropietario() {
        return propietario;
    }   

    public void setPropietario(Residente propietario) {
        this.propietario = propietario;
    }

    public Visitante getVisitante() {
        return visitante;
    }

    public void setVisitante(Visitante visitante) {
        this.visitante = visitante;
    }
}


package com.proyecto2.persistencia;

import com.proyecto2.models.*;
import java.io.*;

public class PersistenciaArchivos {

    private static final String ARCHIVO_RESIDENTES = "residentes.txt";
    private static final String ARCHIVO_VEHICULOS = "vehiculos.txt";

        public void guardarDatos(ListaDobleResidentes lista) {
        // Esto te dirá en la consola exactamente en qué carpeta se están guardando
        File archivoRes = new File("residentes.txt");
        System.out.println("Guardando archivos en: " + archivoRes.getAbsolutePath());

        try (BufferedWriter writerRes = new BufferedWriter(new FileWriter(archivoRes));
             BufferedWriter writerVeh = new BufferedWriter(new FileWriter("vehiculos.txt"))) {
            
            NodoDobleResidente actual = lista.getPrimero();
            while (actual != null) {
                Residente r = actual.getDato();
                
                String lineaRes = r.getId() + "|" + r.getNombre() + "|" + r.getCasa() + "|" + (r.isEsSocio() ? "S" : "N");
                writerRes.write(lineaRes);
                writerRes.newLine();
                
                NodoSimpleVehiculo vehiculoActual = r.getListaVehiculos().getPrimero();
                while (vehiculoActual != null) {
                    Vehiculo v = vehiculoActual.getDato();
                    String lineaVeh = v.getPlaca() + "|" + v.getMarca() + "|" + v.getModelo() + "|" + v.getColor() + "|" + r.getId();
                    writerVeh.write(lineaVeh);
                    writerVeh.newLine();
                    
                    vehiculoActual = vehiculoActual.getSiguiente();
                }
                actual = actual.getSiguiente();
            }
            // Forzamos a que se escriba todo en el disco
            writerRes.flush();
            writerVeh.flush();
            System.out.println("¡Datos guardados exitosamente!");
            
        } catch (IOException e) {
            System.out.println("ERROR AL GUARDAR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public ListaDobleResidentes cargarDatos(PilaEventos historial) {
        ListaDobleResidentes residentes = new ListaDobleResidentes();
        File archivoRes = new File(ARCHIVO_RESIDENTES);

        if(!archivoRes.exists()){
            return residentes; // si no existe, inicia vacio(PDF)

        }

        //1. cargar residentes
        try(BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_RESIDENTES))){
            String linea;
            while((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 4) {
                    boolean esSocio = partes[3].equals("S");
                    Residente r = new Residente(partes[0], partes[1], partes[2], esSocio);

                    residentes.insertar(r);
                }                                

            }

        } catch (IOException e) {
            System.out.println("Error al leer residentes: " + e.getMessage());
        }

        //2. cargar vehiculos

        File archivoVeh = new File(ARCHIVO_VEHICULOS);
        if(archivoVeh.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(ARCHIVO_VEHICULOS))){
                String linea;
                int numeroLinea = 0 ;
                while((linea = reader.readLine()) != null) {
                    numeroLinea++;
                    String[] partes = linea.split("\\|");

                    if(partes.length == 5) {
                        String idResidente = partes[4];
                        Residente r = residentes.buscar(idResidente);

                        //REGLA: si el residente no existe, omitir y registrar evento
                        if (r == null) {
                            historial.push(new Evento("ERROR CARGA", "Línea " + numeroLinea + " omitida: Residente no existe", null));
                            continue;
                        }
                        
                        Vehiculo v = new Vehiculo(partes[0], partes[1], partes[2], partes[3], "Automovil"); // Tipo genérico por ahora
                        v.setPropietario(r);
                        
                        // REGLA: Si supera el máximo de 3 vehículos, omitir y registrar evento
                        if (!r.getListaVehiculos().insertar(v)) {
                            historial.push(new Evento("ERROR CARGA", "Línea " + numeroLinea + " omitida: Máximo de 3 vehículos excedido", null));
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error al leer vehículos: " + e.getMessage());
            }
        }
        
        return residentes;
    }
}


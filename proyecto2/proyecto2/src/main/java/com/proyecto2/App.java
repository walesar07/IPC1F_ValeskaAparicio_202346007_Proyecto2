package com.proyecto2;

import com.proyecto2.controllers.ControladorPrincipal;
import com.proyecto2.views.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        
        // 1. Instanciamos el Controlador (el cerebro)
        ControladorPrincipal controlador = new ControladorPrincipal();
        
        // 2. Cargamos los datos de los archivos .txt
        controlador.cargarDatos();

        // 3. Creamos la Ventana Principal pasándole el controlador
        VentanaPrincipal ventana = new VentanaPrincipal(controlador);
        
        // 4. Le decimos al Controlador cómo actualizar la ventana de forma segura
        // Usamos invokeLater porque los hilos no pueden tocar Swing directamente
        controlador.setActualizarVistaCallback(() -> {
            SwingUtilities.invokeLater(() -> ventana.actualizarPaneles());
        });

        // 5. Hacemos visible la ventana
        ventana.setVisible(true);
        //le decimos a la vista que se pinte con los datos que acabamos de cargar
        ventana.actualizarPaneles();

        // 6. Arrancamos los hilos de las garitas (Empiezan a esperar en el wait())
        controlador.iniciarSimulacion();
    }
}

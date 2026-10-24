package com.proyecto2.views;

import com.proyecto2.controllers.ControladorPrincipal;
import com.proyecto2.models.NodoPila;
import javax.swing.*;
import java.awt.*;

public class PanelEventos extends JPanel {
    private ControladorPrincipal controlador;
    private JTextArea txtHistorial;

    public PanelEventos(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titulo = new JLabel("Historial de Eventos (Más reciente arriba)");
        titulo.setFont(new Font("Arial", Font.BOLD, 14));
        add(titulo, BorderLayout.NORTH);

        txtHistorial = new JTextArea();
        txtHistorial.setEditable(false);
        txtHistorial.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        add(new JScrollPane(txtHistorial), BorderLayout.CENTER);
    }

    public void actualizar() {
        StringBuilder sb = new StringBuilder();
        
        // Sincronizamos para evitar que un hilo escriba en la pila mientras la estamos leyendo
        synchronized (controlador.getHistorial()) {
            NodoPila actual = controlador.getHistorial().getTope();
            
            if (actual == null) {
                sb.append("No hay eventos registrados aún.");
            } else {
                // Recorremos desde el tope hacia abajo
                while (actual != null) {
                    sb.append(actual.getDato().toString()).append("\n");
                    sb.append("--------------------------------------------------\n");
                    actual = actual.getSiguiente();
                }
            }
        }
        
        txtHistorial.setText(sb.toString());
        // Hacer scroll automático al inicio (el evento más reciente)
        txtHistorial.setCaretPosition(0); 
    }
}

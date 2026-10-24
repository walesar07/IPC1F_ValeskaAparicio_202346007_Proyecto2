package com.proyecto2.views;

import com.proyecto2.controllers.ControladorPrincipal;
import com.proyecto2.models.Espacio;
import com.proyecto2.models.ListaCircularEspacios;
import javax.swing.*;
import java.awt.*;

public class PanelParqueo extends JPanel {
    private ControladorPrincipal controlador;
    private JPanel gridSocios;
    private JPanel gridGeneral;

    public PanelParqueo(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout());
        
        // Panel superior para el área de socios (3 filas de 25 = 75)
        gridSocios = new JPanel(new GridLayout(3, 25, 2, 2));
        gridSocios.setBorder(BorderFactory.createTitledBorder("Área de Socios (A, B, C)"));
        
        // Panel inferior para el área general (5 filas de 15 = 75)
        gridGeneral = new JPanel(new GridLayout(5, 15, 2, 2));
        gridGeneral.setBorder(BorderFactory.createTitledBorder("Área General/Visitantes (E, F, G, H, I)"));

        add(gridSocios, BorderLayout.NORTH);
        add(gridGeneral, BorderLayout.CENTER);

        // Llenamos la cuadrícula inicial
        dibujarEspacios();
        actualizar();
    }

    private void dibujarEspacios() {
        // NOTA: Aquí usamos un arreglo SOLO para los componentes visuales de Swing, 
        // no para guardar datos (los datos están en las listas circulares).
        
        // Llenar Socios (75)
        for (int i = 0; i < 75; i++) {
            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            label.setFont(new Font("Arial", Font.BOLD, 9));
            gridSocios.add(label);
        }

        // Llenar General (75)
        for (int i = 0; i < 75; i++) {
            JLabel label = new JLabel("", SwingConstants.CENTER);
            label.setOpaque(true);
            label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            label.setFont(new Font("Arial", Font.BOLD, 9));
            gridGeneral.add(label);
        }
    }

    public void actualizar() {
        // Actualizamos Socios
        pintarListaCircular(controlador.getParqueo().getAreaSocios(), gridSocios);
        
        // Actualizamos General
        pintarListaCircular(controlador.getParqueo().getAreaGeneral(), gridGeneral);
        
        // Repintar la pantalla
        revalidate();
        repaint();
    }

    private void pintarListaCircular(ListaCircularEspacios lista, JPanel grid) {
        if (lista.getPrimero() == null) return;

        Espacio actual = lista.getPrimero();
        int index = 0; // Índice para saber qué JLabel modificar en el GridLayout

        // Recorremos la lista circular hasta volver al inicio
        do {
            if (index < grid.getComponentCount()) {
                JLabel label = (JLabel) grid.getComponent(index);
                
                label.setText(actual.getId());
                
                if (actual.isLibre()) {
                    label.setBackground(new Color(144, 238, 144)); // Verde claro (Libre)
                    label.setToolTipText("Libre");
                } else {
                    label.setBackground(new Color(255, 99, 71)); // Rojo (Ocupado)
                    // Mostrar la placa del vehículo
                    label.setText("<html><center>" + actual.getId() + "<br>" + actual.getVehiculoEstacionado().getPlaca() + "</center></html>");
                    label.setToolTipText("Ocupado por: " + actual.getVehiculoEstacionado().getPlaca());
                }
            }
            actual = actual.getSiguiente();
            index++;
        } while (actual != lista.getPrimero());
    }
}

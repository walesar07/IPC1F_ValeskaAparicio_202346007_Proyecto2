package com.proyecto2.views;

import com.proyecto2.controllers.ControladorPrincipal;
import com.proyecto2.models.*;
import javax.swing.*;
import java.awt.*;

public class PanelSalida extends JPanel {
    private ControladorPrincipal controlador;

    private JTextField txtPlacaSalida;
    private JTextArea txtAreaCola;
    private JLabel lblGaritaSalida;

    public PanelSalida(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Formulario
        JPanel panelForm = new JPanel(new FlowLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Solicitar Salida"));
        txtPlacaSalida = new JTextField(10);
        JButton btnSalir = new JButton("Solicitar Salida a Cola");
        panelForm.add(new JLabel("Placa:"));
        panelForm.add(txtPlacaSalida);
        panelForm.add(btnSalir);

        add(panelForm, BorderLayout.NORTH);

        // Visualización
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));
        
        JPanel panelCola = new JPanel(new BorderLayout());
        panelCola.setBorder(BorderFactory.createTitledBorder("Cola de Salida (FIFO)"));
        txtAreaCola = new JTextArea(8, 30);
        txtAreaCola.setEditable(false);
        panelCola.add(new JScrollPane(txtAreaCola), BorderLayout.CENTER);

        JPanel panelGarita = new JPanel(new GridLayout(1, 1));
        panelGarita.setBorder(BorderFactory.createTitledBorder("Estado Garita de Salida"));
        lblGaritaSalida = new JLabel("Garita de Salida: Libre", SwingConstants.CENTER);
        lblGaritaSalida.setFont(new Font("Arial", Font.BOLD, 14));
        panelGarita.add(lblGaritaSalida);

        panelCentro.add(panelCola, BorderLayout.CENTER);
        panelCentro.add(panelGarita, BorderLayout.EAST);
        add(panelCentro, BorderLayout.CENTER);

        // Eventos
        btnSalir.addActionListener(e -> {
            if (controlador.solicitarSalida(txtPlacaSalida.getText())) {
                txtPlacaSalida.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El vehículo no existe o no está ESTACIONADO.");
            }
        });
    }

    public void actualizar() {
        // 1. Actualizar Garita de Salida
        Vehiculo gs = controlador.getGaritaSalida() != null ? controlador.getGaritaSalida().getVehiculoActual() : null;
        lblGaritaSalida.setText("Garita Salida: " + (gs != null ? "Atendiendo " + gs.getPlaca() : "Libre"));
        lblGaritaSalida.setForeground(gs != null ? Color.RED : Color.GREEN.darker());

        // 2. Actualizar Cola de Salida
        StringBuilder sb = new StringBuilder();
        synchronized (controlador.getColaSalida()) {
            NodoCola actual = controlador.getColaSalida().getFrente();
            int pos = 1;
            while (actual != null) {
                sb.append(pos).append(". ").append(actual.getDato().getPlaca()).append("\n");
                actual = actual.getSiguiente();
                pos++;
            }
        }
        if (sb.length() == 0) sb.append("Cola vacía.");
        txtAreaCola.setText(sb.toString());
    }
}

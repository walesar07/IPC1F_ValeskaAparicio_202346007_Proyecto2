package com.proyecto2.views;

import com.proyecto2.controllers.ControladorPrincipal;
import com.proyecto2.models.*;
import javax.swing.*;
import java.awt.*;

public class PanelEntrada extends JPanel {
    private ControladorPrincipal controlador;

    // Formulario Vehículo Residente
    private JTextField txtPlacaResidente;
    
    // Formulario Visitante
    private JTextField txtNombreVisita, txtPlacaVisita, txtIdResidenteVisita;

    // Visualización
    private JTextArea txtAreaCola;
    private JLabel lblGarita1, lblGarita2;

    public PanelEntrada(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- ZONA SUPERIOR (Formularios) ---
        JPanel panelFormularios = new JPanel(new GridLayout(1, 2, 10, 10));

        // 1. Enviar Vehículo de Residente
        JPanel panelRes = new JPanel(new FlowLayout());
        panelRes.setBorder(BorderFactory.createTitledBorder("Enviar Vehículo de Residente"));
        txtPlacaResidente = new JTextField(10);
        JButton btnEnviar = new JButton("Enviar a Cola");
        panelRes.add(new JLabel("Placa:"));
        panelRes.add(txtPlacaResidente);
        panelRes.add(btnEnviar);

        // 2. Registrar Visitante
        JPanel panelVis = new JPanel(new GridLayout(4, 2, 5, 5));
        panelVis.setBorder(BorderFactory.createTitledBorder("Registrar Visitante"));
        txtNombreVisita = new JTextField();
        txtPlacaVisita = new JTextField();
        txtIdResidenteVisita = new JTextField();
        JButton btnRegistrarVisita = new JButton("Registrar y Encolar");
        
        panelVis.add(new JLabel("Nombre:"));
        panelVis.add(txtNombreVisita);
        panelVis.add(new JLabel("Placa:"));
        panelVis.add(txtPlacaVisita);
        panelVis.add(new JLabel("ID Residente a visitar:"));
        panelVis.add(txtIdResidenteVisita);
        panelVis.add(new JLabel("")); // Espacio vacío
        panelVis.add(btnRegistrarVisita);

        panelFormularios.add(panelRes);
        panelFormularios.add(panelVis);
        add(panelFormularios, BorderLayout.NORTH);

        // --- ZONA CENTRAL (Cola y Garitas) ---
        JPanel panelCentro = new JPanel(new BorderLayout(10, 10));

        // Cola de entrada
        JPanel panelCola = new JPanel(new BorderLayout());
        panelCola.setBorder(BorderFactory.createTitledBorder("Cola de Entrada (FIFO)"));
        txtAreaCola = new JTextArea(8, 30);
        txtAreaCola.setEditable(false);
        panelCola.add(new JScrollPane(txtAreaCola), BorderLayout.CENTER);
        
        // Estado de las Garitas
        JPanel panelGaritas = new JPanel(new GridLayout(2, 1));
        panelGaritas.setBorder(BorderFactory.createTitledBorder("Estado de Garitas"));
        lblGarita1 = new JLabel("Garita 1: Libre", SwingConstants.CENTER);
        lblGarita1.setFont(new Font("Arial", Font.BOLD, 14));
        lblGarita2 = new JLabel("Garita 2: Libre", SwingConstants.CENTER);
        lblGarita2.setFont(new Font("Arial", Font.BOLD, 14));
        panelGaritas.add(lblGarita1);
        panelGaritas.add(lblGarita2);

        panelCentro.add(panelCola, BorderLayout.CENTER);
        panelCentro.add(panelGaritas, BorderLayout.EAST);
        add(panelCentro, BorderLayout.CENTER);

        // --- EVENTOS ---
        btnEnviar.addActionListener(e -> {
            if (controlador.enviarVehiculoAEntrada(txtPlacaResidente.getText())) {
                txtPlacaResidente.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "El vehículo no existe o no está en estado FUERA.");
            }
        });

        btnRegistrarVisita.addActionListener(e -> {
            Visitante v = new Visitante(txtNombreVisita.getText(), txtPlacaVisita.getText(), txtIdResidenteVisita.getText());
            if (controlador.registrarVisitante(v)) {
                JOptionPane.showMessageDialog(this, "Visitante encolado.");
                txtNombreVisita.setText(""); txtPlacaVisita.setText(""); txtIdResidenteVisita.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error: Placa duplicada o el residente visitado no existe.");
            }
        });
    }

    public void actualizar() {
        // 1. Actualizar las Garitas (leemos el vehiculoActual de cada hilo)
        Vehiculo g1 = controlador.getGarita1() != null ? controlador.getGarita1().getVehiculoActual() : null;
        Vehiculo g2 = controlador.getGarita2() != null ? controlador.getGarita2().getVehiculoActual() : null;
        
        lblGarita1.setText("Garita 1: " + (g1 != null ? "Atendiendo " + g1.getPlaca() : "Libre"));
        lblGarita1.setForeground(g1 != null ? Color.RED : Color.GREEN.darker());
        
        lblGarita2.setText("Garita 2: " + (g2 != null ? "Atendiendo " + g2.getPlaca() : "Libre"));
        lblGarita2.setForeground(g2 != null ? Color.RED : Color.GREEN.darker());

        // 2. Actualizar la Cola (SINCRONIZADO para evitar choques con los hilos)
        StringBuilder sb = new StringBuilder();
        synchronized (controlador.getColaEntrada()) {
            NodoCola actual = controlador.getColaEntrada().getFrente();
            int pos = 1;
            while (actual != null) {
                sb.append(pos).append(". ").append(actual.getDato().getPlaca());
                if (actual.getDato().getVisitante() != null) {
                    sb.append(" (Visitante)");
                } else {
                    sb.append(" (Residente)");
                }
                sb.append("\n");
                actual = actual.getSiguiente();
                pos++;
            }
        }
        if (sb.length() == 0) sb.append("Cola vacía.");
        txtAreaCola.setText(sb.toString());
    }
}

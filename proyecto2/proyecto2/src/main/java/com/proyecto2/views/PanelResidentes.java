package com.proyecto2.views;

import com.proyecto2.controllers.ControladorPrincipal;
import com.proyecto2.models.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PanelResidentes extends JPanel {
    private ControladorPrincipal controlador;

    // Componentes de Residente
    private JTextField txtId, txtNombre, txtCasa;
    private JCheckBox chkSocio;
    
    // Componentes de Vehículo
    private JTextField txtPlaca, txtMarca, txtModelo, txtColor;
    private JComboBox<String> cmbTipo;
    private JTextField txtIdResidenteVehiculo; // Para saber a quién asignarle el carro
    
    // Tabla
    private JTable tablaResidentes;
    private DefaultTableModel modeloTabla;

    public PanelResidentes(ControladorPrincipal controlador) {
        this.controlador = controlador;
        setLayout(new BorderLayout(10, 10));

        //FORMULARIO RESIDENTES (NORTE)
        JPanel panelFormRes = new JPanel(new FlowLayout());
        panelFormRes.setBorder(BorderFactory.createTitledBorder("Gestión de Residentes"));
        
        txtId = new JTextField(5);
        txtNombre = new JTextField(10);
        txtCasa = new JTextField(5);
        chkSocio = new JCheckBox("Es Socio");
        
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");

        panelFormRes.add(new JLabel("ID:"));
        panelFormRes.add(txtId);
        panelFormRes.add(new JLabel("Nombre:"));
        panelFormRes.add(txtNombre);
        panelFormRes.add(new JLabel("Casa:"));
        panelFormRes.add(txtCasa);
        panelFormRes.add(chkSocio);
        panelFormRes.add(btnRegistrar);
        panelFormRes.add(btnModificar);
        panelFormRes.add(btnEliminar);
        panelFormRes.add(btnLimpiar);

        add(panelFormRes, BorderLayout.NORTH);

        // TABLA (CENTRO) 
        String[] columnas = {"ID", "Nombre", "Casa", "¿Es Socio?", "Vehículos"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Solo lectura
        };
        tablaResidentes = new JTable(modeloTabla);
        add(new JScrollPane(tablaResidentes), BorderLayout.CENTER);

        // FORMULARIO VEHÍCULOS (SUR)
        JPanel panelFormVeh = new JPanel(new FlowLayout());
        panelFormVeh.setBorder(BorderFactory.createTitledBorder("Asignar Vehículo al Residente (Selecciona uno en la tabla)"));
        
        txtPlaca = new JTextField(6);
        txtMarca = new JTextField(8);
        txtModelo = new JTextField(8);
        txtColor = new JTextField(8);
        cmbTipo = new JComboBox<>(new String[]{"Automovil", "Motocicleta", "Pickup"});
        
        JButton btnAgregarVehiculo = new JButton("Agregar Vehículo");
        JButton btnEliminarVehiculo = new JButton("Eliminar Vehículo");

        panelFormVeh.add(new JLabel("Placa:"));
        panelFormVeh.add(txtPlaca);
        panelFormVeh.add(new JLabel("Marca:"));
        panelFormVeh.add(txtMarca);
        panelFormVeh.add(new JLabel("Modelo:"));
        panelFormVeh.add(txtModelo);
        panelFormVeh.add(new JLabel("Color:"));
        panelFormVeh.add(txtColor);
        panelFormVeh.add(cmbTipo);
        panelFormVeh.add(btnAgregarVehiculo);
        panelFormVeh.add(btnEliminarVehiculo);

        add(panelFormVeh, BorderLayout.SOUTH);

        //EVENTOS DE LOS BOTONES
        
        // Seleccionar un residente de la tabla llena los textfields
        tablaResidentes.getSelectionModel().addListSelectionListener(e -> {
            int fila = tablaResidentes.getSelectedRow();
            if (fila >= 0) {
                txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
                txtId.setEditable(false); // El ID no se puede modificar
                txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
                txtCasa.setText(modeloTabla.getValueAt(fila, 2).toString());
                chkSocio.setSelected(modeloTabla.getValueAt(fila, 3).toString().equals("Sí"));
            }
        });

        btnRegistrar.addActionListener(e -> {
            try {
                validarSinPipe(txtId.getText(), txtNombre.getText(), txtCasa.getText());
                Residente r = new Residente(txtId.getText(), txtNombre.getText(), txtCasa.getText(), chkSocio.isSelected());
                if (controlador.registrarResidente(r)) {
                    JOptionPane.showMessageDialog(this, "Residente registrado.");
                    limpiarFormulario();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: ID duplicado o datos inválidos.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnModificar.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            if (controlador.modificarResidente(txtId.getText(), txtNombre.getText(), txtCasa.getText(), chkSocio.isSelected())) {
                JOptionPane.showMessageDialog(this, "Residente modificado.");
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede modificar. Verifique si tiene vehículos adentro.");
            }
        });

        btnEliminar.addActionListener(e -> {
            if (txtId.getText().isEmpty()) return;
            if (controlador.eliminarResidente(txtId.getText())) {
                JOptionPane.showMessageDialog(this, "Residente eliminado.");
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar. Tiene vehículos en el parqueo o en cola.");
            }
        });

        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Lógica para agregar un vehículo al residente seleccionado
        btnAgregarVehiculo.addActionListener(e -> {
            int fila = tablaResidentes.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona un residente en la tabla primero.");
                return;
            }
            String idRes = modeloTabla.getValueAt(fila, 0).toString();
            
            try {
                validarSinPipe(txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(), txtColor.getText());
                Vehiculo v = new Vehiculo(txtPlaca.getText(), txtMarca.getText(), txtModelo.getText(), txtColor.getText(), cmbTipo.getSelectedItem().toString());
                
                if (controlador.registrarVehiculo(idRes, v)) {
                    JOptionPane.showMessageDialog(this, "Vehículo agregado.");
                    txtPlaca.setText(""); txtMarca.setText(""); txtModelo.setText(""); txtColor.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Error: Placa duplicada o el residente ya tiene 3 vehículos.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        btnEliminarVehiculo.addActionListener(e -> {
            int fila = tablaResidentes.getSelectedRow();
            if (fila < 0 || txtPlaca.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Selecciona un residente y escribe la placa a eliminar.");
                return;
            }
            String idRes = modeloTabla.getValueAt(fila, 0).toString();
            if (controlador.eliminarVehiculo(idRes, txtPlaca.getText())) {
                JOptionPane.showMessageDialog(this, "Vehículo eliminado.");
                txtPlaca.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "No se puede eliminar. El vehículo no existe o no está FUERA.");
            }
        });
    }

    // Valida la regla del PDF: Nada de caracteres | en los datos
    private void validarSinPipe(String... campos) {
        for (String campo : campos) {
            if (campo.contains("|") || campo.trim().isEmpty()) {
                throw new IllegalArgumentException("Los campos no pueden estar vacíos ni contener el carácter '|'.");
            }
        }
    }

    private void limpiarFormulario() {
        txtId.setText(""); txtNombre.setText(""); txtCasa.setText("");
        txtId.setEditable(true); // Volver a habilitar el ID para un nuevo registro
        chkSocio.setSelected(false);
        tablaResidentes.clearSelection();
    }

    // Método llamado desde la VentanaPrincipal cuando hay cambios
    public void actualizar() {
        modeloTabla.setRowCount(0); // Limpiar tabla
        NodoDobleResidente actual = controlador.getResidentes().getPrimero();
        
        while (actual != null) {
            Residente r = actual.getDato();
            
            // Contar vehículos
            int cantVehiculos = r.getListaVehiculos().getTamano();
            
            Object[] fila = {
                r.getId(),
                r.getNombre(),
                r.getCasa(),
                r.isEsSocio() ? "Sí" : "No",
                cantVehiculos
            };
            modeloTabla.addRow(fila);
            
            actual = actual.getSiguiente();
        }
    }
}
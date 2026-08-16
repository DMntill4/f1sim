package f1sim.ui;

import f1sim.model.TipoNeumatico;

import javax.swing.*;
import java.awt.*;

// Ventana/Panel Swing para configurar la estrategia de neumáticos y combustible previa a la carrera
public class VentanaEstrategia extends JDialog {

    private JComboBox<TipoNeumatico> comboNeumaticos;
    private JSpinner spinnerCombustible;
    private JCheckBox checkCambiarAleron;
    private boolean confirmado = false;

    public VentanaEstrategia(Frame padre) {
        super(padre, "Configurar Estrategia de Carrera / Pit Stop", true);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setSize(420, 260);
        setLocationRelativeTo(getParent());

        JPanel panelForm = new JPanel(new GridLayout(3, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        panelForm.add(new JLabel("Compuesto Inicial / Próximo:"));
        comboNeumaticos = new JComboBox<>(TipoNeumatico.values());
        panelForm.add(comboNeumaticos);

        panelForm.add(new JLabel("Carga Combustible (kg):"));
        spinnerCombustible = new JSpinner(new SpinnerNumberModel(110.0, 50.0, 110.0, 5.0));
        panelForm.add(spinnerCombustible);

        panelForm.add(new JLabel("Ajustes Aerodinámicos:"));
        checkCambiarAleron = new JCheckBox("Reemplazar alerón delantero (+5s)");
        panelForm.add(checkCambiarAleron);

        add(panelForm, BorderLayout.CENTER);

        JPanel panelBotonera = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCancelar = new JButton("Cancelar");
        JButton btnAceptar = new JButton("Confirmar Estrategia");

        btnCancelar.addActionListener(e -> dispose());
        btnAceptar.addActionListener(e -> {
            confirmado = true;
            dispose();
        });

        panelBotonera.add(btnCancelar);
        panelBotonera.add(btnAceptar);
        add(panelBotonera, BorderLayout.SOUTH);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public TipoNeumatico getNeumaticoSeleccionado() {
        return (TipoNeumatico) comboNeumaticos.getSelectedItem();
    }

    public double getCargaCombustible() {
        return (Double) spinnerCombustible.getValue();
    }

    public boolean isCambiarAleron() {
        return checkCambiarAleron.isSelected();
    }
}

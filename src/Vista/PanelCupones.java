package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controlador.GestorVentas;
import modelo.Evento;

/*
    Pestaña de CUPONES. Cada evento tiene sus propios cupones de descuento
    (código -> porcentaje). Aquí se eligen por evento y se pueden agregar,
    listar y eliminar. Al vender un ticket, el cupón se valida contra los
    cupones del evento elegido.
*/
public class PanelCupones extends PanelBase
{
    private JComboBox<String> comboEventos;
    private List<Evento> eventosCombo = new ArrayList<>();

    private JTextField campoCodigo;
    private JTextField campoDescuento;

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public PanelCupones(GestorVentas gestor, Notificador notificador)
    {
        super(gestor, notificador);
        setLayout(new BorderLayout(15, 0));

        JPanel oeste = new JPanel(new BorderLayout());
        oeste.setOpaque(false);
        oeste.setPreferredSize(new Dimension(390, 100));
        oeste.add(construirParteSuperior(), BorderLayout.NORTH);
        add(oeste, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(construirTabla(), BorderLayout.CENTER);
        centro.add(construirBotones(), BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        actualizar();
    }

    private JPanel construirParteSuperior()
    {
        JPanel arriba = new JPanel();
        arriba.setLayout(new BoxLayout(arriba, BoxLayout.Y_AXIS));
        arriba.setOpaque(false);

        comboEventos = new JComboBox<>();
        comboEventos.setFont(EstiloUI.FUENTE);
        comboEventos.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { refrescarTablaCupones(); }
        });
        JPanel selector = new JPanel(new BorderLayout(10, 0));
        selector.setOpaque(false);
        selector.setBorder(EstiloUI.margen(0, 0, 8, 0));
        selector.add(EstiloUI.subtitulo("Evento:"), BorderLayout.WEST);
        selector.add(comboEventos, BorderLayout.CENTER);
        arriba.add(selector);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                EstiloUI.margen(12, 15, 12, 15)));

        campoCodigo = EstiloUI.campo(20);
        campoDescuento = EstiloUI.campo(20);

        form.add(EstiloUI.subtitulo("Nuevo cupón"));
        form.add(fila("Código:", campoCodigo));
        form.add(fila("Descuento (%):", campoDescuento));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBoton.setOpaque(false);
        JButton btnAgregar = EstiloUI.botonExito("Agregar cupón");
        btnAgregar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { agregarCupon(); }
        });
        filaBoton.add(btnAgregar);
        form.add(filaBoton);

        arriba.add(form);
        return arriba;
    }

    private JPanel construirTabla()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(EstiloUI.subtitulo("Cupones del evento seleccionado"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(new String[] {"Código", "Descuento"}, 0)
        {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(EstiloUI.FUENTE);
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(EstiloUI.FUENTE_BOLD);

        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel construirBotones()
    {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);
        JButton btnEliminar = EstiloUI.botonPeligro("Eliminar");
        JButton btnRefrescar = EstiloUI.botonPrimario("Refrescar");
        btnEliminar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { eliminarCupon(); }
        });
        btnRefrescar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { refrescarTablaCupones(); }
        });
        barra.add(btnEliminar);
        barra.add(btnRefrescar);
        return barra;
    }

    // ---- Acciones ----

    private Evento eventoSeleccionado()
    {
        int i = comboEventos.getSelectedIndex();
        if (i < 0 || i >= eventosCombo.size())
        {
            notificador.informar("Primero crea o selecciona un evento.");
            return null;
        }
        return eventosCombo.get(i);
    }

    private void agregarCupon()
    {
        Evento evento = eventoSeleccionado();
        if (evento == null) return;

        String codigo = leerTexto(campoCodigo, "Código");
        if (codigo == null) return;
        Integer porcentaje = leerEntero(campoDescuento, "Descuento");
        if (porcentaje == null) return;
        if (porcentaje <= 0 || porcentaje > 100)
        {
            notificador.informar("El descuento debe estar entre 1 y 100.");
            return;
        }

        evento.agregarCupon(codigo, porcentaje / 100.0);
        campoCodigo.setText("");
        campoDescuento.setText("");
        refrescarTablaCupones();
        notificador.informar("Cupón '" + codigo.toUpperCase() + "' agregado al evento " + evento.getNombre() + ".");
    }

    private void eliminarCupon()
    {
        Evento evento = eventoSeleccionado();
        if (evento == null) return;
        int fila = tabla.getSelectedRow();
        if (fila < 0)
        {
            notificador.informar("Primero selecciona un cupón en la tabla.");
            return;
        }
        String codigo = modeloTabla.getValueAt(fila, 0).toString();
        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar el cupón '" + codigo + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION)
        {
            evento.eliminarCupon(codigo);
            refrescarTablaCupones();
            notificador.informar("Cupón '" + codigo + "' eliminado.");
        }
    }

    private void refrescarTablaCupones()
    {
        if (modeloTabla == null) return;
        modeloTabla.setRowCount(0);
        int i = comboEventos.getSelectedIndex();
        if (i < 0 || i >= eventosCombo.size()) return;
        for (Map.Entry<String, Double> c : eventosCombo.get(i).getCupones().entrySet())
        {
            int porcentaje = (int) Math.round(c.getValue() * 100);
            modeloTabla.addRow(new Object[] { c.getKey(), porcentaje + "%" });
        }
    }

    @Override
    public void actualizar()
    {
        if (comboEventos == null) return;
        int seleccionPrevia = comboEventos.getSelectedIndex();

        eventosCombo.clear();
        comboEventos.removeAllItems();
        for (Evento e : gestor.getMapaEventos().values())
        {
            eventosCombo.add(e);
            comboEventos.addItem(e.getCodigo() + "   |   " + e.getNombre());
        }
        if (seleccionPrevia >= 0 && seleccionPrevia < comboEventos.getItemCount())
        {
            comboEventos.setSelectedIndex(seleccionPrevia);
        }
        refrescarTablaCupones();
    }
}

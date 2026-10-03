package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

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
import modelo.Ubicacion;

/*
    Pestaña de ZONAS (la colección anidada dentro de cada evento). Primero se
    elige un evento y luego se agregan, editan, eliminan o listan sus zonas.
    Solo se ocupa de zonas (alta cohesión).
*/
public class PanelZonas extends PanelBase
{
    private JComboBox<String> comboEventos;
    private List<Evento> eventosCombo = new ArrayList<>();

    private JTextField campoNombre;
    private JTextField campoCapacidad;
    private JTextField campoPrecio;

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public PanelZonas(GestorVentas gestor, Notificador notificador)
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

        // Selector de evento
        comboEventos = new JComboBox<>();
        comboEventos.setFont(EstiloUI.FUENTE);
        comboEventos.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { refrescarTablaZonas(); }
        });
        JPanel selector = new JPanel(new BorderLayout(10, 0));
        selector.setOpaque(false);
        selector.setBorder(EstiloUI.margen(0, 0, 8, 0));
        selector.add(EstiloUI.subtitulo("Evento:"), BorderLayout.WEST);
        selector.add(comboEventos, BorderLayout.CENTER);
        arriba.add(selector);

        // Formulario "Nueva zona"
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                EstiloUI.margen(12, 15, 12, 15)));

        campoNombre = EstiloUI.campo(20);
        campoCapacidad = EstiloUI.campo(20);
        campoPrecio = EstiloUI.campo(20);

        form.add(EstiloUI.subtitulo("Nueva zona"));
        form.add(fila("Nombre zona:", campoNombre));
        form.add(fila("Capacidad:", campoCapacidad));
        form.add(fila("Precio base:", campoPrecio));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBoton.setOpaque(false);
        JButton btnAgregar = EstiloUI.botonExito("Agregar zona");
        btnAgregar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { agregarZona(); }
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
        panel.add(EstiloUI.subtitulo("Zonas del evento seleccionado"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[] {"Nombre", "Capacidad", "Vendidos", "Precio"}, 0)
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

        JButton btnEditar = EstiloUI.botonPrimario("Editar");
        JButton btnEliminar = EstiloUI.botonPeligro("Eliminar");
        JButton btnRefrescar = EstiloUI.botonPrimario("Refrescar");

        btnEditar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { editarZona(); }
        });
        btnEliminar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { eliminarZona(); }
        });
        btnRefrescar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { refrescarTablaZonas(); }
        });

        barra.add(btnEditar);
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

    private void agregarZona()
    {
        Evento evento = eventoSeleccionado();
        if (evento == null) return;

        String nombre = leerTexto(campoNombre, "Nombre zona");
        if (nombre == null) return;
        Integer capacidad = leerEntero(campoCapacidad, "Capacidad");
        if (capacidad == null) return;
        Double precio = leerDouble(campoPrecio, "Precio base");
        if (precio == null) return;
        if (capacidad <= 0 || precio < 0)
        {
            notificador.informar("La capacidad debe ser mayor a 0 y el precio no puede ser negativo.");
            return;
        }
        if (evento.buscarUbicacion(nombre) != null)
        {
            notificador.informar("Ya existe una zona con ese nombre en este evento.");
            return;
        }

        evento.agregarUbicacion(new Ubicacion(nombre, capacidad, precio));
        campoNombre.setText("");
        campoCapacidad.setText("");
        campoPrecio.setText("");
        refrescarTablaZonas();
        notificador.informar("Zona '" + nombre + "' agregada al evento " + evento.getNombre() + ".");
    }

    private void editarZona()
    {
        Evento evento = eventoSeleccionado();
        if (evento == null) return;
        String nombreZona = zonaSeleccionada();
        if (nombreZona == null) return;
        Ubicacion u = evento.buscarUbicacion(nombreZona);
        if (u == null) return;

        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", u.getNombreZona());
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty())
        {
            u.setNombreZona(nuevoNombre.trim());
        }
        String capStr = JOptionPane.showInputDialog(this, "Nueva capacidad:", u.getCapacidadMaxima());
        if (capStr != null && !capStr.trim().isEmpty())
        {
            try
            {
                int nueva = Integer.parseInt(capStr.trim());
                if (nueva > 0) u.setCapacidadMaxima(nueva);
            }
            catch (NumberFormatException ex)
            {
                notificador.informar("Capacidad inválida, se mantiene la anterior.");
            }
        }
        String precioStr = JOptionPane.showInputDialog(this, "Nuevo precio:", u.getPrecioBase());
        if (precioStr != null && !precioStr.trim().isEmpty())
        {
            try
            {
                double nuevo = Double.parseDouble(precioStr.trim());
                if (nuevo >= 0) u.setPrecioBase(nuevo);
            }
            catch (NumberFormatException ex)
            {
                notificador.informar("Precio inválido, se mantiene el anterior.");
            }
        }
        refrescarTablaZonas();
        notificador.informar("Zona actualizada.");
    }

    private void eliminarZona()
    {
        Evento evento = eventoSeleccionado();
        if (evento == null) return;
        String nombreZona = zonaSeleccionada();
        if (nombreZona == null) return;

        int r = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la zona '" + nombreZona + "'?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION)
        {
            evento.eliminarUbicacion(nombreZona);
            refrescarTablaZonas();
            notificador.informar("Zona '" + nombreZona + "' eliminada.");
        }
    }

    private String zonaSeleccionada()
    {
        int fila = tabla.getSelectedRow();
        if (fila < 0)
        {
            notificador.informar("Primero selecciona una zona en la tabla.");
            return null;
        }
        return modeloTabla.getValueAt(fila, 0).toString();
    }

    private void refrescarTablaZonas()
    {
        if (modeloTabla == null) return;
        modeloTabla.setRowCount(0);
        Evento evento = actualEventoSinAviso();
        if (evento == null) return;
        for (Ubicacion u : evento.getZonas())
        {
            modeloTabla.addRow(new Object[] {
                    u.getNombreZona(), u.getCapacidadMaxima(), u.getAsientosVendidos(), u.getPrecioBase()
            });
        }
    }

    // Igual que eventoSeleccionado pero sin mostrar aviso (para refrescos automáticos)
    private Evento actualEventoSinAviso()
    {
        int i = comboEventos.getSelectedIndex();
        if (i < 0 || i >= eventosCombo.size()) return null;
        return eventosCombo.get(i);
    }

    // Reconstruye el combo de eventos y la tabla al entrar a la pestaña
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
        // Intentamos mantener la selección anterior
        if (seleccionPrevia >= 0 && seleccionPrevia < comboEventos.getItemCount())
        {
            comboEventos.setSelectedIndex(seleccionPrevia);
        }
        refrescarTablaZonas();
    }
}

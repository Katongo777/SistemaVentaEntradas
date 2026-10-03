package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controlador.GestorVentas;
import modelo.Categorias;
import modelo.Charla;
import modelo.Evento;
import modelo.Seminario;
import modelo.Ubicacion;

/*
    Pestaña de EVENTOS (colección principal). Tiene un formulario para crear un
    evento nuevo y una tabla que lista los eventos, con botones para editar,
    eliminar y buscar. Solo se preocupa de los eventos (alta cohesión).
*/
public class PanelEventos extends PanelBase
{
    private JComboBox<String> comboTipo;
    private JTextField campoNombre;
    private JComboBox<String> comboTematica;
    private JTextField campoExpositor;
    private JTextField campoDuracion;
    private JTextField campoCapacidad;
    private JTextField campoPrecio;

    private JTextField campoBuscar;
    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public PanelEventos(GestorVentas gestor, Notificador notificador)
    {
        super(gestor, notificador);
        setLayout(new BorderLayout(15, 0));

        // Formulario a la izquierda (ancho fijo), tabla a la derecha
        JPanel oeste = new JPanel(new BorderLayout());
        oeste.setOpaque(false);
        oeste.setPreferredSize(new Dimension(390, 100));
        oeste.add(construirFormulario(), BorderLayout.NORTH);
        add(oeste, BorderLayout.WEST);

        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);
        centro.add(construirTabla(), BorderLayout.CENTER);
        centro.add(construirBotones(), BorderLayout.SOUTH);
        add(centro, BorderLayout.CENTER);

        actualizar();
    }

    // ---- Formulario "Nuevo evento" ----
    private JPanel construirFormulario()
    {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(java.awt.Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new java.awt.Color(220, 224, 230)),
                EstiloUI.margen(12, 15, 12, 15)));

        comboTipo = new JComboBox<>(new String[] {"Charla", "Seminario"});
        comboTipo.setFont(EstiloUI.FUENTE);
        campoNombre = EstiloUI.campo(20);
        comboTematica = new JComboBox<>(Categorias.TEMATICAS);
        comboTematica.setFont(EstiloUI.FUENTE);
        campoExpositor = EstiloUI.campo(20);
        campoDuracion = EstiloUI.campo(20);
        campoCapacidad = EstiloUI.campo(20);
        campoPrecio = EstiloUI.campo(20);

        // Según el tipo, se habilita expositor (Charla) o duración (Seminario)
        comboTipo.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { ajustarCamposSegunTipo(); }
        });

        form.add(EstiloUI.subtitulo("Nuevo evento"));
        form.add(fila("Tipo:", comboTipo));
        form.add(fila("Nombre:", campoNombre));
        form.add(fila("Temática:", comboTematica));
        form.add(fila("Expositor (Charla):", campoExpositor));
        form.add(fila("Duración días (Seminario):", campoDuracion));
        form.add(fila("Capacidad zona General:", campoCapacidad));
        form.add(fila("Precio base:", campoPrecio));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBoton.setOpaque(false);
        javax.swing.JButton btnAgregar = EstiloUI.botonExito("Agregar evento");
        btnAgregar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { agregarEvento(); }
        });
        filaBoton.add(btnAgregar);
        form.add(filaBoton);

        ajustarCamposSegunTipo();
        return form;
    }

    private void ajustarCamposSegunTipo()
    {
        boolean esCharla = comboTipo.getSelectedIndex() == 0;
        campoExpositor.setEnabled(esCharla);
        campoDuracion.setEnabled(!esCharla);
    }

    // ---- Tabla de eventos + buscador ----
    private JPanel construirTabla()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        JPanel cabecera = new JPanel(new BorderLayout(0, 6));
        cabecera.setOpaque(false);
        cabecera.add(EstiloUI.subtitulo("Eventos registrados"), BorderLayout.NORTH);

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        buscador.setOpaque(false);
        campoBuscar = EstiloUI.campo(14);
        javax.swing.JButton btnBuscar = EstiloUI.botonPrimario("Buscar");
        btnBuscar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { buscar(); }
        });
        buscador.add(EstiloUI.etiqueta("Buscar (ID o nombre):"));
        buscador.add(campoBuscar);
        buscador.add(btnBuscar);
        cabecera.add(buscador, BorderLayout.CENTER);

        modeloTabla = new DefaultTableModel(
                new String[] {"ID", "Tipo", "Nombre", "Temática", "Detalle", "N° Zonas"}, 0)
        {
            @Override
            public boolean isCellEditable(int fila, int columna) { return false; }
        };
        tabla = new JTable(modeloTabla);
        tabla.setFont(EstiloUI.FUENTE);
        tabla.setRowHeight(26);
        tabla.getTableHeader().setFont(EstiloUI.FUENTE_BOLD);

        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    // ---- Botones de acción bajo la tabla ----
    private JPanel construirBotones()
    {
        JPanel barra = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        barra.setOpaque(false);

        javax.swing.JButton btnEditar = EstiloUI.botonPrimario("Editar");
        javax.swing.JButton btnEliminar = EstiloUI.botonPeligro("Eliminar");
        javax.swing.JButton btnRefrescar = EstiloUI.botonPrimario("Refrescar");

        btnEditar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { editarSeleccionado(); }
        });
        btnEliminar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { eliminarSeleccionado(); }
        });
        btnRefrescar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { actualizar(); }
        });

        barra.add(btnEditar);
        barra.add(btnEliminar);
        barra.add(btnRefrescar);
        return barra;
    }

    // ---- Acciones ----

    private void agregarEvento()
    {
        String nombre = leerTexto(campoNombre, "Nombre");
        if (nombre == null) return;
        String tematica = (String) comboTematica.getSelectedItem();
        Integer capacidad = leerEntero(campoCapacidad, "Capacidad");
        if (capacidad == null) return;
        Double precio = leerDouble(campoPrecio, "Precio base");
        if (precio == null) return;
        if (capacidad <= 0 || precio < 0)
        {
            notificador.informar("La capacidad debe ser mayor a 0 y el precio no puede ser negativo.");
            return;
        }

        String sufijo = java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        Evento nuevo;

        if (comboTipo.getSelectedIndex() == 0) // Charla
        {
            String expositor = leerTexto(campoExpositor, "Expositor");
            if (expositor == null) return;
            nuevo = new Charla("CH-" + sufijo, nombre, tematica, expositor);
        }
        else // Seminario
        {
            Integer duracion = leerEntero(campoDuracion, "Duración días");
            if (duracion == null) return;
            nuevo = new Seminario("SE-" + sufijo, nombre, tematica, duracion);
        }

        nuevo.agregarUbicacion(new Ubicacion("General", capacidad, precio));
        gestor.agregarEvento(nuevo);
        limpiarFormulario();
        actualizar();
        notificador.informar("Evento agregado: " + nuevo.getCodigo() + " - " + nuevo.getNombre());
    }

    private void limpiarFormulario()
    {
        campoNombre.setText("");
        campoExpositor.setText("");
        campoDuracion.setText("");
        campoCapacidad.setText("");
        campoPrecio.setText("");
    }

    private void editarSeleccionado()
    {
        String codigo = codigoSeleccionado();
        if (codigo == null) return;
        Evento e = gestor.buscarEvento(codigo);
        if (e == null) return;

        String nuevoNombre = JOptionPane.showInputDialog(this, "Nuevo nombre:", e.getNombre());
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty())
        {
            e.setNombre(nuevoNombre.trim());
        }
        String nuevaTematica = (String) JOptionPane.showInputDialog(this, "Nueva temática:",
                "Editar evento", JOptionPane.QUESTION_MESSAGE, null, Categorias.TEMATICAS, e.getTematica());
        if (nuevaTematica != null)
        {
            e.setTematica(nuevaTematica);
        }
        actualizar();
        notificador.informar("Evento " + codigo + " actualizado.");
    }

    private void eliminarSeleccionado()
    {
        String codigo = codigoSeleccionado();
        if (codigo == null) return;
        int r = JOptionPane.showConfirmDialog(this,
                "¿Seguro que quieres eliminar el evento " + codigo + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION)
        {
            gestor.eliminarEvento(codigo);
            actualizar();
            notificador.informar("Evento " + codigo + " eliminado.");
        }
    }

    // Devuelve el ID de la fila seleccionada o null (avisando) si no hay
    private String codigoSeleccionado()
    {
        int fila = tabla.getSelectedRow();
        if (fila < 0)
        {
            notificador.informar("Primero selecciona un evento en la tabla.");
            return null;
        }
        return modeloTabla.getValueAt(fila, 0).toString();
    }

    private void buscar()
    {
        String texto = campoBuscar.getText().trim().toLowerCase();
        modeloTabla.setRowCount(0);
        for (Evento e : gestor.getMapaEventos().values())
        {
            boolean coincide = texto.isEmpty()
                    || e.getCodigo().toLowerCase().contains(texto)
                    || e.getNombre().toLowerCase().contains(texto);
            if (coincide)
            {
                agregarFila(e);
            }
        }
        notificador.informar(texto.isEmpty() ? "Mostrando todos los eventos." : "Resultados para: " + texto);
    }

    // Refresca la tabla con todos los eventos
    @Override
    public void actualizar()
    {
        if (modeloTabla == null) return;
        modeloTabla.setRowCount(0);
        for (Evento e : gestor.getMapaEventos().values())
        {
            agregarFila(e);
        }
    }

    private void agregarFila(Evento e)
    {
        String tipo = e.getClass().getSimpleName();
        String detalle = "";
        if (e instanceof Charla)
        {
            detalle = "Expositor: " + ((Charla) e).getExpositorPrincipal();
        }
        else if (e instanceof Seminario)
        {
            detalle = ((Seminario) e).getDuracionDias() + " días";
        }
        modeloTabla.addRow(new Object[] {
                e.getCodigo(), tipo, e.getNombre(), e.getTematica(), detalle, e.getZonas().size()
        });
    }
}

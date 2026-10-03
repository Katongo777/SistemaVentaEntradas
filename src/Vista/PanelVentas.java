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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import controlador.GestorVentas;
import modelo.Evento;
import modelo.Ticket;
import modelo.Ubicacion;
import modelo.Usuario;
import excepciones.EdadInsuficienteException;
import excepciones.StockAgotadoException;

/*
    Pestaña de VENTAS. Arma el formulario de venta (evento, zona, datos del
    comprador y cupón), llama al controlador para vender y muestra el historial
    de tickets en una tabla. Las excepciones de negocio se atrapan con try-catch.
*/
public class PanelVentas extends PanelBase
{
    private JComboBox<String> comboEventos;
    private List<Evento> eventosCombo = new ArrayList<>();
    private JComboBox<String> comboZonas;
    private List<Ubicacion> zonasCombo = new ArrayList<>();

    private JTextField campoNombre;
    private JTextField campoRut;
    private JTextField campoEdad;
    private JTextField campoCupon;
    private JLabel etiquetaCupones;

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public PanelVentas(GestorVentas gestor, Notificador notificador)
    {
        super(gestor, notificador);
        setLayout(new BorderLayout(15, 0));

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

    private JPanel construirFormulario()
    {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                EstiloUI.margen(12, 15, 12, 15)));

        comboEventos = new JComboBox<>();
        comboEventos.setFont(EstiloUI.FUENTE);
        comboEventos.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { refrescarZonas(); }
        });
        comboZonas = new JComboBox<>();
        comboZonas.setFont(EstiloUI.FUENTE);

        campoNombre = EstiloUI.campo(20);
        campoRut = EstiloUI.campo(20);
        campoEdad = EstiloUI.campo(20);
        campoCupon = EstiloUI.campo(20);
        etiquetaCupones = EstiloUI.etiqueta(" ");

        form.add(EstiloUI.subtitulo("Vender ticket"));
        form.add(fila("Evento:", comboEventos));
        form.add(fila("Zona:", comboZonas));
        form.add(fila("Nombre comprador:", campoNombre));
        form.add(fila("Rut:", campoRut));
        form.add(fila("Edad:", campoEdad));
        form.add(fila("Cupón (opcional):", campoCupon));
        form.add(fila("Cupones disponibles:", etiquetaCupones));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBoton.setOpaque(false);
        JButton btnVender = EstiloUI.botonExito("Vender");
        btnVender.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { vender(); }
        });
        filaBoton.add(btnVender);
        form.add(filaBoton);
        return form;
    }

    private JPanel construirTabla()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(EstiloUI.subtitulo("Tickets vendidos"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[] {"Reserva", "Evento", "Cliente", "Zona", "Costo final"}, 0)
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
        JButton btnRefrescar = EstiloUI.botonPrimario("Refrescar");
        btnRefrescar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { actualizar(); }
        });
        barra.add(btnRefrescar);
        return barra;
    }

    // ---- Acciones ----

    private void vender()
    {
        int iE = comboEventos.getSelectedIndex();
        if (iE < 0 || iE >= eventosCombo.size())
        {
            notificador.informar("Selecciona un evento.");
            return;
        }
        int iZ = comboZonas.getSelectedIndex();
        if (iZ < 0 || iZ >= zonasCombo.size())
        {
            notificador.informar("El evento no tiene zonas. Agrega una zona primero.");
            return;
        }
        Evento evento = eventosCombo.get(iE);
        Ubicacion zona = zonasCombo.get(iZ);

        String nombre = leerTexto(campoNombre, "Nombre comprador");
        if (nombre == null) return;
        String rut = leerTexto(campoRut, "Rut");
        if (rut == null) return;
        Integer edad = leerEntero(campoEdad, "Edad");
        if (edad == null) return;
        if (edad < 0)
        {
            notificador.informar("La edad no puede ser negativa.");
            return;
        }
        String cupon = campoCupon.getText().trim();

        // El área de interés no se pide en la venta (va vacía); se usa en Recomendar
        Usuario comprador = new Usuario(nombre, rut, edad, "");

        try
        {
            Ticket ticket;
            if (cupon.isEmpty())
            {
                ticket = gestor.venderTicket(comprador, evento, zona);
            }
            else
            {
                ticket = gestor.venderTicket(comprador, evento, zona, cupon);
            }

            JOptionPane.showMessageDialog(this,
                    "Reserva: " + ticket.getCodigoReserva() + "\n" +
                    "Cliente: " + ticket.getAsistente().getNombre() + "\n" +
                    "Evento: " + ticket.getEvento().getNombre() + "\n" +
                    "Zona: " + ticket.getUbicacion().getNombreZona() + "\n" +
                    "Costo final: $" + ticket.getCostoFinal(),
                    "Venta exitosa", JOptionPane.INFORMATION_MESSAGE);

            campoNombre.setText("");
            campoRut.setText("");
            campoEdad.setText("");
            campoCupon.setText("");
            actualizar(); // refresca tabla y zonas (cambió el stock vendido)
            notificador.informar("Ticket vendido: " + ticket.getCodigoReserva());
        }
        catch (EdadInsuficienteException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Venta rechazada", JOptionPane.WARNING_MESSAGE);
            notificador.informar(ex.getMessage());
        }
        catch (StockAgotadoException ex)
        {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Venta rechazada", JOptionPane.WARNING_MESSAGE);
            notificador.informar(ex.getMessage());
        }
    }

    private void refrescarZonas()
    {
        zonasCombo.clear();
        comboZonas.removeAllItems();
        int i = comboEventos.getSelectedIndex();
        if (i < 0 || i >= eventosCombo.size())
        {
            if (etiquetaCupones != null) etiquetaCupones.setText(" ");
            return;
        }
        Evento evento = eventosCombo.get(i);
        for (Ubicacion u : evento.getZonas())
        {
            zonasCombo.add(u);
            int disponibles = u.getCapacidadMaxima() - u.getAsientosVendidos();
            comboZonas.addItem(u.getNombreZona() + "  ($" + u.getPrecioBase() + ", quedan " + disponibles + ")");
        }
        actualizarCuponesHint(evento);
    }

    // Muestra los cupones válidos del evento seleccionado (para que se sepan)
    private void actualizarCuponesHint(Evento evento)
    {
        if (etiquetaCupones == null) return;
        if (evento.getCupones().isEmpty())
        {
            etiquetaCupones.setText("(este evento no tiene cupones)");
            return;
        }
        StringBuilder sb = new StringBuilder();
        boolean primero = true;
        for (java.util.Map.Entry<String, Double> c : evento.getCupones().entrySet())
        {
            if (!primero) sb.append(", ");
            int porcentaje = (int) Math.round(c.getValue() * 100);
            sb.append(c.getKey()).append(" (").append(porcentaje).append("%)");
            primero = false;
        }
        etiquetaCupones.setText(sb.toString());
    }

    private void refrescarTablaTickets()
    {
        modeloTabla.setRowCount(0);
        for (Ticket t : gestor.getHistorialVentas())
        {
            modeloTabla.addRow(new Object[] {
                    t.getCodigoReserva(),
                    t.getEvento().getNombre(),
                    t.getAsistente().getNombre(),
                    t.getUbicacion().getNombreZona(),
                    "$" + t.getCostoFinal()
            });
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
        refrescarZonas();
        refrescarTablaTickets();
    }
}

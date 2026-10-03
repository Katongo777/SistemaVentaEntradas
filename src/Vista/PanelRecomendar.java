package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import modelo.Usuario;

/*
    Pestaña RECOMENDAR (funcionalidad propia, SIA-9). Pide los datos de un usuario
    y muestra el subconjunto de eventos que coinciden con su interés y edad,
    usando el filtro que vive en el controlador (gestor.eventosSugeridos).
*/
public class PanelRecomendar extends PanelBase
{
    private JTextField campoNombre;
    private JTextField campoEdad;
    private JComboBox<String> comboInteres;

    private DefaultTableModel modeloTabla;
    private JTable tabla;

    public PanelRecomendar(GestorVentas gestor, Notificador notificador)
    {
        super(gestor, notificador);
        setLayout(new BorderLayout(0, 15));
        add(construirFormulario(), BorderLayout.NORTH);
        add(construirTabla(), BorderLayout.CENTER);
    }

    private JPanel construirFormulario()
    {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 224, 230)),
                EstiloUI.margen(12, 15, 12, 15)));

        campoNombre = EstiloUI.campo(20);
        campoEdad = EstiloUI.campo(20);
        comboInteres = new JComboBox<>(Categorias.TEMATICAS);
        comboInteres.setFont(EstiloUI.FUENTE);

        form.add(EstiloUI.subtitulo("Recomendar eventos a un usuario"));
        form.add(fila("Nombre:", campoNombre));
        form.add(fila("Edad:", campoEdad));
        form.add(fila("Área de interés:", comboInteres));

        JPanel filaBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filaBoton.setOpaque(false);
        JButton btn = EstiloUI.botonPrimario("Buscar recomendaciones");
        btn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e) { recomendar(); }
        });
        filaBoton.add(btn);
        form.add(filaBoton);
        return form;
    }

    private JPanel construirTabla()
    {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(EstiloUI.subtitulo("Eventos recomendados"), BorderLayout.NORTH);

        modeloTabla = new DefaultTableModel(
                new String[] {"ID", "Tipo", "Nombre", "Temática", "Detalle"}, 0)
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

    private void recomendar()
    {
        String nombre = leerTexto(campoNombre, "Nombre");
        if (nombre == null) return;
        Integer edad = leerEntero(campoEdad, "Edad");
        if (edad == null) return;
        String interes = (String) comboInteres.getSelectedItem();

        Usuario usuario = new Usuario(nombre, "sin-rut", edad, interes);
        ArrayList<Evento> sugeridos = gestor.eventosSugeridos(usuario);

        modeloTabla.setRowCount(0);
        for (Evento e : sugeridos)
        {
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
                    e.getCodigo(), e.getClass().getSimpleName(), e.getNombre(), e.getTematica(), detalle
            });
        }

        if (sugeridos.isEmpty())
        {
            notificador.informar("No hay eventos que coincidan con '" + interes + "' para " + nombre + ".");
        }
        else
        {
            notificador.informar("Se encontraron " + sugeridos.size() + " evento(s) para " + nombre + ".");
        }
    }

    // No necesita refrescar nada al entrar; se recomienda al presionar el botón
    @Override
    public void actualizar() { }
}

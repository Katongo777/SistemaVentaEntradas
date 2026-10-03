package vista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import controlador.GestorVentas;
import controlador.ManejadorArchivos;

/*
    Ventana principal "Pro": la versión más completa y cuidada de la interfaz.

    Su única responsabilidad es ARMAR la ventana (cabecera, pestañas y barra de
    estado) y coordinar los paneles; toda la lógica del negocio sigue en el
    controlador (GestorVentas). Implementa Notificador para mostrar los mensajes
    en la barra de estado de abajo, así los paneles le avisan sin conocerla en
    concreto.

    Diseño (SOLID / GRASP):
      - Cada pestaña es un panel con una sola responsabilidad (SRP / alta cohesión).
      - Los paneles dependen del gestor y de la interfaz Notificador, no entre sí
        (bajo acoplamiento / DIP).
      - Al cambiar de pestaña se llama actualizar() sobre el panel, sin importar
        cuál sea (polimorfismo).
*/
public class VistaVentanaPro extends JFrame implements Notificador
{
    private GestorVentas gestor;
    private JLabel etiquetaEstado;
    private PanelBase[] paneles;

    public VistaVentanaPro(GestorVentas gestor)
    {
        this.gestor = gestor;
        aplicarLookAndFeel();

        setTitle("Sistema de Venta de Entradas");
        setSize(920, 640);
        setMinimumSize(new Dimension(760, 560));
        setLocationRelativeTo(null);
        getContentPane().setBackground(EstiloUI.FONDO);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) { guardarYSalir(); }
        });

        add(construirCabecera(), BorderLayout.NORTH);
        add(construirPestanas(), BorderLayout.CENTER);
        add(construirBarraEstado(), BorderLayout.SOUTH);
    }

    // Cabecera con color y título
    private JPanel construirCabecera()
    {
        JPanel cabecera = new JPanel(new BorderLayout());
        cabecera.setBackground(EstiloUI.PRIMARIO);
        cabecera.setBorder(EstiloUI.margen(16, 20, 16, 20));

        JLabel titulo = new JLabel("Sistema de Venta de Entradas");
        titulo.setFont(EstiloUI.FUENTE_TITULO);
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Gestión de eventos, zonas y venta de tickets");
        subtitulo.setForeground(new Color(210, 216, 230));
        subtitulo.setFont(EstiloUI.FUENTE);

        cabecera.add(titulo, BorderLayout.NORTH);
        cabecera.add(subtitulo, BorderLayout.SOUTH);
        return cabecera;
    }

    // Las 4 pestañas
    private JTabbedPane construirPestanas()
    {
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(EstiloUI.FUENTE_BOLD);
        pestanas.setBorder(EstiloUI.margen(10, 10, 5, 10));

        PanelEventos panelEventos = new PanelEventos(gestor, this);
        PanelZonas panelZonas = new PanelZonas(gestor, this);
        PanelCupones panelCupones = new PanelCupones(gestor, this);
        PanelVentas panelVentas = new PanelVentas(gestor, this);
        PanelRecomendar panelRecomendar = new PanelRecomendar(gestor, this);

        paneles = new PanelBase[] { panelEventos, panelZonas, panelCupones, panelVentas, panelRecomendar };

        pestanas.addTab("  Eventos  ", panelEventos);
        pestanas.addTab("  Zonas  ", panelZonas);
        pestanas.addTab("  Cupones  ", panelCupones);
        pestanas.addTab("  Ventas  ", panelVentas);
        pestanas.addTab("  Recomendar  ", panelRecomendar);

        // Al cambiar de pestaña, refrescamos los datos del panel que se muestra
        pestanas.addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                int i = pestanas.getSelectedIndex();
                if (paneles != null && i >= 0 && i < paneles.length)
                {
                    paneles[i].actualizar();
                }
            }
        });
        return pestanas;
    }

    // Barra de estado abajo
    private JPanel construirBarraEstado()
    {
        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(new Color(235, 238, 243));
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(210, 214, 220)),
                EstiloUI.margen(8, 15, 8, 15)));

        etiquetaEstado = new JLabel("Listo. Usa las pestañas para gestionar el sistema.");
        etiquetaEstado.setFont(EstiloUI.FUENTE);
        etiquetaEstado.setForeground(EstiloUI.TEXTO);
        barra.add(etiquetaEstado, BorderLayout.WEST);
        return barra;
    }

    // ---- Notificador ----
    @Override
    public void informar(String mensaje)
    {
        etiquetaEstado.setText(mensaje);
    }

    // ---- Cierre con guardado (persistencia batch) ----
    private void guardarYSalir()
    {
        ManejadorArchivos.guardarEventosBatch(gestor.getMapaEventos());
        javax.swing.JOptionPane.showMessageDialog(this, "Datos guardados. ¡Hasta pronto!");
        dispose();
        System.exit(0);
    }

    /*
        Aplica el Look and Feel Nimbus y lo tiñe con nuestro color principal, para
        que la ventana se vea más moderna. Si por algún motivo Nimbus no está
        disponible, se queda con el look por defecto sin caerse.
    */
    private void aplicarLookAndFeel()
    {
        try
        {
            UIManager.put("nimbusBase", EstiloUI.PRIMARIO);
            UIManager.put("nimbusBlueGrey", new Color(200, 206, 216));
            UIManager.put("control", EstiloUI.FONDO);
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            // Si falla, simplemente seguimos con el look por defecto
        }
    }
}

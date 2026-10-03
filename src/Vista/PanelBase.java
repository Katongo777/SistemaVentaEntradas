package vista;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import controlador.GestorVentas;

/*
    Clase base de todos los paneles de pestaña de la ventana Pro.

    Guarda lo común a todos: el controlador (gestor) y el notificador para
    mostrar mensajes. Además obliga a cada panel a implementar actualizar(),
    que sirve para refrescar sus datos cuando el usuario entra a esa pestaña.

    Así aplicamos:
      - Alta cohesión (GRASP): cada panel que herede se encarga SOLO de su tema.
      - Bajo acoplamiento (GRASP): los paneles conocen al gestor y al notificador,
        no a la ventana concreta ni a los otros paneles.
      - Polimorfismo (GRASP): la ventana llama actualizar() sin importar el panel.
*/
public abstract class PanelBase extends JPanel
{
    protected final GestorVentas gestor;
    protected final Notificador notificador;

    protected PanelBase(GestorVentas gestor, Notificador notificador)
    {
        this.gestor = gestor;
        this.notificador = notificador;
        setBackground(EstiloUI.FONDO);
        setBorder(EstiloUI.margen(15, 15, 15, 15));
    }

    // Cada panel refresca sus datos (tablas, combos) al mostrarse
    public abstract void actualizar();

    /*
        Ayudante para armar una fila de formulario: una etiqueta a la izquierda
        (de ancho fijo para que todo quede alineado) y el componente a la derecha.
    */
    protected JPanel fila(String etiqueta, JComponent componente)
    {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);
        JLabel l = EstiloUI.etiqueta(etiqueta);
        l.setPreferredSize(new Dimension(160, 30));
        p.add(l, BorderLayout.WEST);
        p.add(componente, BorderLayout.CENTER);
        p.setBorder(EstiloUI.margen(4, 0, 4, 0));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        return p;
    }

    /*
        Lectores con validación. Devuelven null (y avisan por el notificador) si el
        dato está vacío o no es un número. Así los paneles no repiten el try-catch.
    */
    protected String leerTexto(JTextField campo, String nombre)
    {
        String t = campo.getText().trim();
        if (t.isEmpty())
        {
            notificador.informar("Completa el campo '" + nombre + "'.");
            return null;
        }
        return t;
    }

    protected Integer leerEntero(JTextField campo, String nombre)
    {
        try
        {
            return Integer.parseInt(campo.getText().trim());
        }
        catch (NumberFormatException e)
        {
            notificador.informar("El campo '" + nombre + "' debe ser un número entero.");
            return null;
        }
    }

    protected Double leerDouble(JTextField campo, String nombre)
    {
        try
        {
            return Double.parseDouble(campo.getText().trim());
        }
        catch (NumberFormatException e)
        {
            notificador.informar("El campo '" + nombre + "' debe ser un número.");
            return null;
        }
    }
}

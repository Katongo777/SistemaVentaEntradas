package vista;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicButtonUI;

/*
    Clase de utilidad con los colores, fuentes y "fábricas" de componentes
    (botones, etiquetas, campos) que usamos en toda la interfaz gráfica.

    La idea es no repetir estilos en cada panel (DRY): si queremos cambiar el
    color principal o la fuente, se cambia acá una sola vez y se refleja en todo.
    El constructor es privado porque es una clase solo de helpers estáticos.
*/
public final class EstiloUI
{
    private EstiloUI() { }

    // Paleta de colores
    public static final Color PRIMARIO       = new Color(45, 62, 120);   // azul índigo
    public static final Color PRIMARIO_HOVER = new Color(63, 84, 156);
    public static final Color FONDO          = new Color(245, 247, 250); // gris muy claro
    public static final Color TEXTO          = new Color(33, 37, 41);
    public static final Color EXITO          = new Color(30, 130, 76);   // verde
    public static final Color EXITO_HOVER    = new Color(38, 160, 94);
    public static final Color PELIGRO        = new Color(192, 57, 43);    // rojo
    public static final Color PELIGRO_HOVER  = new Color(214, 69, 55);

    // Fuentes
    public static final Font FUENTE        = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FUENTE_BOLD   = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FUENTE_SUBTIT = new Font("Segoe UI", Font.BOLD, 15);

    // Etiqueta normal
    public static JLabel etiqueta(String texto)
    {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE);
        l.setForeground(TEXTO);
        return l;
    }

    // Subtítulo de una sección
    public static JLabel subtitulo(String texto)
    {
        JLabel l = new JLabel(texto);
        l.setFont(FUENTE_SUBTIT);
        l.setForeground(PRIMARIO);
        return l;
    }

    // Campo de texto con un ancho de referencia
    public static JTextField campo(int columnas)
    {
        JTextField c = new JTextField(columnas);
        c.setFont(FUENTE);
        return c;
    }

    /*
        Botón "plano" de color sólido. Usamos BasicButtonUI para que respete el
        color de fondo en cualquier Look and Feel (con Nimbus, si no, los ignora).
        Además le agregamos un pequeño efecto al pasar el mouse por encima.
    */
    public static JButton boton(String texto, final Color fondo, final Color fondoHover)
    {
        final JButton b = new JButton(texto);
        b.setUI(new BasicButtonUI());
        b.setBackground(fondo);
        b.setForeground(Color.WHITE);
        b.setFont(FUENTE_BOLD);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseEntered(MouseEvent e) { b.setBackground(fondoHover); }
            @Override
            public void mouseExited(MouseEvent e) { b.setBackground(fondo); }
        });
        return b;
    }

    // Atajos para los tres tipos de botón que más usamos
    public static JButton botonPrimario(String texto) { return boton(texto, PRIMARIO, PRIMARIO_HOVER); }
    public static JButton botonExito(String texto)    { return boton(texto, EXITO, EXITO_HOVER); }
    public static JButton botonPeligro(String texto)  { return boton(texto, PELIGRO, PELIGRO_HOVER); }

    // Margen (padding) rápido
    public static Border margen(int arriba, int izq, int abajo, int der)
    {
        return BorderFactory.createEmptyBorder(arriba, izq, abajo, der);
    }
}

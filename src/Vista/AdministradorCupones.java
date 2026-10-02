package vista;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import controlador.GestorVentas;
import modelo.Evento;

/*
    Gestiona por consola los cupones de descuento de un evento: agregar, listar
    y eliminar. Cada evento tiene su propia lista de cupones (código -> descuento),
    que después se usa al vender un ticket.
*/
public class AdministradorCupones
{
    private GestorVentas gestor;
    private BufferedReader lector;

    public AdministradorCupones(GestorVentas ge, BufferedReader le)
    {
        gestor = ge;
        lector = le;
    }

    public void agregarCupon() throws IOException
    {
        System.out.println("\n--- AGREGAR CUPÓN A UN EVENTO ---");
        Evento evento = pedirEvento();
        if (evento == null) return;

        System.out.print("Código del cupón (ej: PROMO50): ");
        String codigo = lector.readLine();

        // Validación del porcentaje de descuento (1 a 100)
        int porcentaje = 0;
        while (true)
        {
            try
            {
                System.out.print("Descuento en % (1 a 100): ");
                porcentaje = Integer.parseInt(lector.readLine());
                if (porcentaje <= 0 || porcentaje > 100)
                {
                    System.out.println("¡ERROR: Debe estar entre 1 y 100! Intente de nuevo...");
                    continue;
                }
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }

        evento.agregarCupon(codigo, porcentaje / 100.0);
        System.out.println("¡Cupón '" + codigo.toUpperCase() + "' agregado al evento " + evento.getNombre() + "!");
    }

    public void listarCupones() throws IOException
    {
        System.out.println("\n--- LISTAR CUPONES DE UN EVENTO ---");
        Evento evento = pedirEvento();
        if (evento == null) return;

        if (evento.getCupones().isEmpty())
        {
            System.out.println("El evento '" + evento.getNombre() + "' no tiene cupones.");
            return;
        }

        System.out.println("Cupones de '" + evento.getNombre() + "':");
        for (Map.Entry<String, Double> c : evento.getCupones().entrySet())
        {
            int porcentaje = (int) Math.round(c.getValue() * 100);
            System.out.println("- " + c.getKey() + " -> " + porcentaje + "% de descuento");
        }
    }

    public void eliminarCupon() throws IOException
    {
        System.out.println("\n--- ELIMINAR CUPÓN DE UN EVENTO ---");
        Evento evento = pedirEvento();
        if (evento == null) return;

        System.out.print("Código del cupón a eliminar: ");
        String codigo = lector.readLine();

        if (evento.eliminarCupon(codigo))
        {
            System.out.println("¡Cupón '" + codigo.toUpperCase() + "' eliminado!");
        }
        else
        {
            System.out.println("No se encontró ese cupón en el evento.");
        }
    }

    // Pide el código del evento y lo devuelve, o null si no existe
    private Evento pedirEvento() throws IOException
    {
        System.out.print("Ingrese el código (ID) del evento: ");
        String codigo = lector.readLine();
        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("¡ERROR: No se encontró ningún evento con ese código!");
        }
        return evento;
    }
}

package vista;

import java.io.BufferedReader;
import java.io.IOException;
import controlador.GestorVentas;
import modelo.Evento;
import modelo.Ubicacion;

/*
    Permite insertar y listar elementos de la colección anidada (Zonas/Ubicaciones) 
    de manera  completamente separada e independiente de la colección principal.
 */
public class AdministradorZonas
{
    private GestorVentas gestor;
    private BufferedReader lector;

    public AdministradorZonas(GestorVentas ge, BufferedReader le)
    {
        gestor = ge;
        lector = le;
    }

    public void agregarZona() throws IOException
    {
        System.out.println("\n--- AGREGAR NUEVA ZONA A EVENTO ---");
        System.out.print("Ingrese el código (ID) del evento: ");
        String codigo = lector.readLine();
        
        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("¡ERROR: No se encontró ningún evento con ese código!");
            return;
        }

        System.out.print("Ingrese el nombre de la nueva zona (Ej: VIP, Platea, Cancha): ");
        String nombre = lector.readLine();

        // Validación para la capacidad
        int capacidad = 0;
        while (true)
        {
            try
            {
                System.out.print("Ingrese la capacidad máxima de esta zona: ");
                capacidad = Integer.parseInt(lector.readLine());
                if (capacidad <= 0)
                {
                    System.out.println("¡ERROR: Capacidad negativa! Intente de nuevo...");
                    continue;
                }
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }

        // Validación segura para el precio
        double precio = 0.0;
        while (true)
        {
            try
            {
                System.out.print("Ingrese el precio base para esta zona: $");
                precio = Double.parseDouble(lector.readLine());
                if (precio < 0)
                {
                    System.out.println("¡ERROR: Precio negativo! Intente de nuevo...");
                    continue;
                }
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }

        // Instanciación y guardado en la colección anidada (SIA-4)
        Ubicacion nuevaZona = new Ubicacion(nombre, capacidad, precio);
        evento.agregarUbicacion(nuevaZona);
        System.out.println("¡Zona '" + nombre + "' agregada con éxito al evento " + evento.getNombre() + "!");
    }

    public void listarZonas() throws IOException
    {
        System.out.println("\n--- LISTAR ZONAS DE UN EVENTO ---");
        System.out.print("Ingrese el código (ID) del evento: ");
        String codigo = lector.readLine();
        
        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("¡ERROR: No se encontró ningún evento con ese código!");
            return;
        }

        if (evento.getZonas().isEmpty())
        {
            System.out.println("El evento '" + evento.getNombre() + "' aún no tiene zonas registradas.");
            return;
        }

        System.out.println("Zonas registradas para '" + evento.getNombre() + "':");
        for (Ubicacion u : evento.getZonas())
        {
            System.out.println("- Zona: " + u.getNombreZona() + 
                               " | Capacidad Total: " + u.getCapacidadMaxima() + 
                               " | Asientos Vendidos: " + u.getAsientosVendidos() +
                               " | Precio Base: $" + u.getPrecioBase());
        }
    }
}
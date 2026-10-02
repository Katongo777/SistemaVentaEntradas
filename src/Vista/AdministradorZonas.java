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

        // Instanciación y guardado en la colección anidada
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

    // Busca una zona puntual dentro de un evento y muestra sus datos.
    public void buscarZona() throws IOException
    {
        System.out.println("\n--- BUSCAR ZONA EN UN EVENTO ---");
        System.out.print("Ingrese el código (ID) del evento: ");
        String codigo = lector.readLine();

        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("¡ERROR: No se encontró ningún evento con ese código!");
            return;
        }

        System.out.print("Ingrese el nombre de la zona a buscar: ");
        String nombre = lector.readLine();

        Ubicacion u = evento.buscarUbicacion(nombre);
        if (u == null)
        {
            System.out.println("No se encontró la zona '" + nombre + "' en este evento.");
        }
        else
        {
            System.out.println("Zona encontrada:");
            System.out.println("- Zona: " + u.getNombreZona() +
                               " | Capacidad Total: " + u.getCapacidadMaxima() +
                               " | Asientos Vendidos: " + u.getAsientosVendidos() +
                               " | Precio Base: $" + u.getPrecioBase());
        }
    }

    /*
        Modifica los datos de una zona ya existente (nombre, capacidad y precio).
        Si se deja un campo en blanco se conserva el valor anterior.
    */

    public void editarZona() throws IOException
    {
        System.out.println("\n--- EDITAR ZONA DE UN EVENTO ---");
        System.out.print("Ingrese el código (ID) del evento: ");
        String codigo = lector.readLine();

        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("¡ERROR: No se encontró ningún evento con ese código!");
            return;
        }

        System.out.print("Ingrese el nombre de la zona a editar: ");
        String nombre = lector.readLine();

        Ubicacion u = evento.buscarUbicacion(nombre);
        if (u == null)
        {
            System.out.println("No se encontró la zona '" + nombre + "' en este evento.");
            return;
        }

        System.out.print("Nuevo nombre (Enter para no cambiar): ");
        String nuevoNombre = lector.readLine();
        if (!nuevoNombre.isEmpty())
        {
            u.setNombreZona(nuevoNombre);
        }

        System.out.print("Nueva capacidad máxima (Enter para no cambiar): ");
        String capStr = lector.readLine();
        if (!capStr.isEmpty())
        {
            try
            {
                int nuevaCap = Integer.parseInt(capStr);
                if (nuevaCap > 0)
                {
                    u.setCapacidadMaxima(nuevaCap);
                }
                else
                {
                    System.out.println("Capacidad inválida, se mantiene la anterior.");
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("No era un número, se mantiene la capacidad anterior.");
            }
        }

        System.out.print("Nuevo precio base (Enter para no cambiar): $");
        String precioStr = lector.readLine();
        if (!precioStr.isEmpty())
        {
            try
            {
                double nuevoPrecio = Double.parseDouble(precioStr);
                if (nuevoPrecio >= 0)
                {
                    u.setPrecioBase(nuevoPrecio);
                }
                else
                {
                    System.out.println("Precio inválido, se mantiene el anterior.");
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("No era un número, se mantiene el precio anterior.");
            }
        }

        System.out.println("¡Zona actualizada correctamente!");
    }

    // Elimina una zona de un evento buscándola por nombre.
    public void eliminarZona() throws IOException
    {
        System.out.println("\n--- ELIMINAR ZONA DE UN EVENTO ---");
        System.out.print("Ingrese el código (ID) del evento: ");
        String codigo = lector.readLine();

        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("¡ERROR: No se encontró ningún evento con ese código!");
            return;
        }

        System.out.print("Ingrese el nombre de la zona a eliminar: ");
        String nombre = lector.readLine();

        if (evento.eliminarUbicacion(nombre))
        {
            System.out.println("¡Zona '" + nombre + "' eliminada con éxito!");
        }
        else
        {
            System.out.println("No se encontró la zona '" + nombre + "' en este evento.");
        }
    }
}
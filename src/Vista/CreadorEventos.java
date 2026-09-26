package vista;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.UUID;
import controlador.GestorVentas;
import modelo.Evento;
import modelo.Charla;
import modelo.Seminario;
import modelo.Ubicacion;

/*
    Interactuar con el usuario para recopilar los datos, validar las entradas númericas 
    y delegar la instanciación de un nuevo Evento al Controlador
*/

public class CreadorEventos
{
    
    private GestorVentas gestor;
    private BufferedReader lector;

    public CreadorEventos(GestorVentas gestor, BufferedReader lector) {
        this.gestor = gestor;
        this.lector = lector;
    }

    public void ejecutar() throws IOException 
    {
        System.out.println("\n--- TIPO DE EVENTO ---");
        System.out.println("1. Charla");
        System.out.println("2. Seminario");
        System.out.print("Seleccione el tipo de evento a crear: ");
        String tipoStr = lector.readLine();
        
        System.out.print("Ingrese el nombre del evento: ");
        String nombre = lector.readLine();
        
        System.out.print("Ingrese la temática: ");
        String tematica = lector.readLine();
        
        // Bloque de validación: Evita que el programa colapse si se ingresa texto
        int capacidad = 0;
        while (true)
        {
            try
            {
                System.out.print("Ingrese la capacidad máxima (límite de tickets): ");
                capacidad = Integer.parseInt(lector.readLine());
                // Entrada válida, salimos del ciclo
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }
        
        // Bloque de validación: Evita que el programa colapse si se ingresa texto
        double precio = 0.0;
        while (true)
        {
            try
            {
                System.out.print("Ingrese el precio base del ticket: $");
                precio = Double.parseDouble(lector.readLine());
                // Entrada válida, salimos del ciclo
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }
        
        Evento nuevoEvento = null;
        
        // Generación de un código alfanúmerico único para el catálogo
        String sufijoAleatorio = UUID.randomUUID().toString().substring(0, 4).toUpperCase();

        // Polimorfismo: Se intancia la subclase correspondiente según la elección
        if (tipoStr.equals("1"))
        {
            System.out.print("Ingrese el expositor principal: ");
            String expositor = lector.readLine();
            String codigo = "CH-" + sufijoAleatorio;
            nuevoEvento = new Charla(codigo, nombre, tematica, expositor);
        }
        else if (tipoStr.equals("2"))
        {
            int duracion = 0;
            while (true)
            {
                try
                {
                    System.out.print("Ingrese la duración en días: ");
                    duracion = Integer.parseInt(lector.readLine());
                    // Entrada válida, salimos del ciclo
                    break;
                }
                catch(NumberFormatException e)
                {
                    System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
                }
            }
            String codigo = "SE-" + sufijoAleatorio;
            nuevoEvento = new Seminario(codigo, nombre, tematica, duracion);     
        }
        else
        {
            System.out.println("¡ERROR: Opción de tipo inválida! Abortando creación...");
            return;
        }

        Ubicacion zonaGeneral = new Ubicacion("General", capacidad, precio);
        nuevoEvento.agregarUbicacion(zonaGeneral);
        
        // Delegación de la persistencia en memoria al Controlador
        gestor.agregarEvento(nuevoEvento);
        System.out.println("¡Evento (" + nuevoEvento.getClass().getSimpleName() + ") agregado con éxito!");
    }
}
package vista;

import java.io.BufferedReader;
import java.io.IOException;
import controlador.GestorVentas;
import modelo.Evento;
import modelo.Charla;
import modelo.Seminario;
import modelo.Ubicacion;

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
        
        System.out.print("Ingrese el código del evento (Ej: SE-03): ");
        String codigo = lector.readLine();
        
        System.out.print("Ingrese el nombre del evento: ");
        String nombre = lector.readLine();
        
        System.out.print("Ingrese la temática: ");
        String tematica = lector.readLine();
        
        System.out.print("Ingrese la capacidad máxima (límite de tickets): ");
        int capacidad = Integer.parseInt(lector.readLine());
        
        System.out.print("Ingrese el precio base del ticket: $");
        double precio = Double.parseDouble(lector.readLine());
        
        Evento nuevoEvento = null;
        
        if (tipoStr.equals("1"))
        {
            System.out.print("Ingrese el expositor principal: ");
            String expositor = lector.readLine();
            nuevoEvento = new Charla(codigo, nombre, tematica, expositor);
        }
        else if (tipoStr.equals("2"))
        {
            System.out.print("Ingrese la duración en días: ");
            int duracion = Integer.parseInt(lector.readLine());
            nuevoEvento = new Seminario(codigo, nombre, tematica, duracion);
        }
        else
        {
            System.out.println("Opción de tipo inválida. Abortando creación.");
            return;
        }

        Ubicacion zonaGeneral = new Ubicacion("General", capacidad, precio);
        nuevoEvento.agregarUbicacion(zonaGeneral);
        
        gestor.agregarEvento(nuevoEvento);
        System.out.println("¡Evento (" + nuevoEvento.getClass().getSimpleName() + ") agregado con éxito!");
    }
}
package vista;

import java.io.BufferedReader;
import java.io.IOException;
import controlador.GestorVentas;
import modelo.Evento;
import modelo.Ticket;
import modelo.Ubicacion;
import modelo.Usuario;

/*
    Administra el flujo de interacción para la venta de entradas. Solicita los datos del usuario, verifica posibles
    descuentos, valida que las edades entrantes sean coherentes y maneja excepciones de negocio lanzadas
    por el Controlador
*/

public class CreadorTickets
{
    
    private GestorVentas gestor;
    private BufferedReader lector;

    public CreadorTickets(GestorVentas gestor, BufferedReader lector)
    {
        this.gestor = gestor;
        this.lector = lector;
    }

    public void ejecutarVenta() throws IOException
    {
        System.out.println("\n--- MÓDULO DE VENTA DE TICKETS ---");
        System.out.print("Ingrese el código del evento al que desea asistir: ");
        String codigo = lector.readLine();
        
        Evento evento = gestor.buscarEvento(codigo);
        if (evento == null)
        {
            System.out.println("Evento no encontrado. Abortando venta.");
            return;
        }
        
        System.out.print("Nombre del usuario: ");
        String nombre = lector.readLine();
        System.out.print("Rut: ");
        String rut = lector.readLine();
        
        // Bloque de validación: Evita que el programa colapse si se ingresa texto
        int edad= 0;
        while (true)
        {
            try
            {
                System.out.print("Edad: ");
                edad = Integer.parseInt(lector.readLine());
                if (edad < 0)
                {
                    System.out.println("¡ERROR: Edad negativa! Intente de nuevo...");
                    continue;
                }
                // Entrada válida, salimos del ciclo
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }
        
        System.out.print("Área de interés: ");
        String interes = lector.readLine();
        
        Usuario asistente = new Usuario(nombre, rut, edad, interes);

        Ubicacion ubicacionVenta;
        if (evento.getZonas().isEmpty())
        {
            ubicacionVenta = new Ubicacion("General", 50, 10000.0);
            evento.agregarUbicacion(ubicacionVenta);
        }
        else
        {
            ubicacionVenta = evento.getZonas().get(0);
        }
        
        System.out.print("Ingrese código de descuento (si no tiene un código presione Enter para saltar): ");
        String cupon = lector.readLine();

        // Bloque transaccional: Intenta concretar la venta y captura los errores lógicos del negocio
        try
        {
            Ticket nuevoTicket;
            
            // Sobrecarga según el ingreso del cupón
            if (cupon.isEmpty())
            {
                nuevoTicket = gestor.venderTicket(asistente, evento, ubicacionVenta);
            }
            else
            {
                nuevoTicket = gestor.venderTicket(asistente, evento, ubicacionVenta, cupon);
            }
            
            System.out.println("\n--- COMPROBANTE DE VENTA ---");
            System.out.println("¡Venta exitosa para " + nuevoTicket.getAsistente().getNombre() + "!");
            System.out.println("El costo final a pagar es: $" + nuevoTicket.getCostoFinal());
            
        }
        catch (excepciones.EdadInsuficienteException e)
        {
            System.out.println("ERROR DE PERFILAMIENTO: " + e.getMessage());
        }
        catch (excepciones.StockAgotadoException e)
        {
            System.out.println("ERROR DE INVENTARIO: " + e.getMessage());
        }
    }
}
package vista;

import java.io.BufferedReader;
import java.io.IOException;
import controlador.GestorVentas;
import modelo.Evento;
import modelo.Ticket;
import modelo.Ubicacion;
import modelo.Usuario;

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
        System.out.print("Edad: ");
        int edad = Integer.parseInt(lector.readLine());
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

        System.out.print("Ingrese un código de reserva para este ticket: ");
        String codReserva = lector.readLine();

        try
        {
            Ticket nuevoTicket = gestor.venderTicket(codReserva, asistente, evento, ubicacionVenta);
            System.out.println("¡Venta exitosa! El costo final es: $" + nuevoTicket.getCostoFinal());
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
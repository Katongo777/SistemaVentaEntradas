package vista;

import java.io.*;
import controlador.GestorVentas;
import modelo.Evento;
import modelo.Ticket;

public class VistaConsola
{
    
    private GestorVentas gestor;
    private BufferedReader lector;

    public VistaConsola(GestorVentas ge)
    {
        gestor = ge;
        lector  = new BufferedReader( new InputStreamReader( System.in ) );
    }

    public void iniciar() throws IOException
    {
        int opcion = -1;
        
        System.out.println("=====================================");
        System.out.println("   SISTEMA DE VENTA DE ENTRADAS");
        System.out.println("=====================================");

        while (opcion != 0)
        {
            System.out.println("\n--- MENÚ PRINCIPAL ---");
            System.out.println("1. Agregar Evento");
            System.out.println("2. Lista de Eventos");
            System.out.println("3. Buscar Evento");
            System.out.println("4. Comprar Ticket");
            System.out.println("5. Listar Tickets vendidos");
            System.out.println("6. Editar Evento");
            System.out.println("7. Eliminar Evento");
            System.out.println("0. Guardar y Salir");
            System.out.print("Seleccione una opción: ");
            
            try
            {
                opcion = Integer.parseInt(lector.readLine());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Por favor, ingrese un número válido...");
                continue;
            }

            switch (opcion)
            {
                case 1:
                    CreadorEventos creador = new CreadorEventos(gestor, lector);
                    creador.ejecutar();
                    break;
                case 2:
                    mostrarEventos();
                    break;
                case 3:
                    buscarEvento();
                    break;
                case 4:
                    CreadorTickets vendedor = new CreadorTickets(gestor, lector);
                    vendedor.ejecutarVenta();
                    break;
                case 5:
                    mostrarTickets();
                    break;
                case 6:
                    EditorEventos editor = new EditorEventos(gestor, lector);
                    editor.editar();
                    break;
                case 7:
                    EditorEventos borrador = new EditorEventos(gestor, lector);
                    borrador.eliminar();
                    break;
                case 0:
                    // Falta logica de manejador de archivos
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void mostrarEventos()
    {
        System.out.println("\n--- LISTADO DE EVENTOS ---");
        if (gestor.getMapaEventos().isEmpty())
        {
            System.out.println("No hay eventos registrados.");
            return;
        }
        
        for (Evento e : gestor.getMapaEventos().values())
        {
            System.out.println(e.mostrarDetalles());
        }
    }

    private void buscarEvento() throws IOException
    {
        System.out.print("Ingrese el código del evento a buscar: ");
        String codigo = lector.readLine();
        
        Evento e = gestor.buscarEvento(codigo);
        if (e != null)
        {
            System.out.println("Evento encontrado: " + e.mostrarDetalles());
        }
        else
        {
            System.out.println("No se encontró ningún evento con ese código.");
        }
    }
        
    private void mostrarTickets()
    {
        System.out.println("\n--- LISTADO DE TICKETS VENDIDOS ---");
        if (gestor.getHistorialVentas().isEmpty())
        {
            System.out.println("Aún no se han registrado ventas de tickets.");
            return;
        }
        
        for (Ticket t : gestor.getHistorialVentas())
        {
            System.out.println("Reserva: " + t.getCodigoReserva() + 
                               " | Evento: " + t.getEvento().getNombre() + 
                               " | Cliente: " + t.getAsistente().getNombre() + 
                               " | Costo Final: $" + t.getCostoFinal());
        }
    }
}
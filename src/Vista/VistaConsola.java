package vista;

import java.io.*;
import controlador.GestorVentas;
import controlador.ManejadorArchivos;
import modelo.Evento;
import modelo.Ticket;

/*
    Mantiene el ciclo de ejecución activo, captura las opciones del usuario para delegar las operaciones
    de entrada/salida a los submódulos correspondientes
*/

public class VistaConsola
{
    
    private GestorVentas gestor;
    private BufferedReader lector;
    
    public VistaConsola(GestorVentas ge)
    {
        gestor = ge;
        lector  = new BufferedReader( new InputStreamReader( System.in ) );
    }

    /*
        Contiene el menú interactivo y la captura errores de formato para evitar
        que el sistema colapse
    */
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
            System.out.println("8. Agregar Zona a un Evento");
            System.out.println("9. Listar Zonas de un Evento");
            System.out.println("0. Guardar y Salir");
            System.out.print("Seleccione una opción: ");
            
            try
            {
                opcion = Integer.parseInt(lector.readLine());
            }
            catch (NumberFormatException e)
            {
                System.out.println("Por favor, ingrese un número válido...");
                continue; // Reinicia el ciclo sin romper el programa
            }
            
            // Enrutador, delega las tareas a los submódulos correspondientes
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
                case 8:
                    AdministradorZonas adminAgg = new AdministradorZonas(gestor, lector);
                    adminAgg.agregarZona();
                    break;
                case 9:
                    AdministradorZonas adminLis = new AdministradorZonas(gestor, lector);
                    adminLis.listarZonas();
                    break;
                case 0:
                    /*
                        Guarda el estado actual de los datos en la memoria local (disco duro) 
                        antes de cerrar el programa
                    */
                    System.out.println("Guardando datos...");
                    ManejadorArchivos.guardarEventosBatch(gestor.getMapaEventos());
                    System.out.println("Saliendo del sistema... ¡Hasta pronto!");                        
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    // Extrae el catálogo (inmodificable) para imprimir los detalles de cada evento
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
            System.out.println("[ID: " + e.getCodigo() + "] " + e.mostrarDetalles());
        }
    }

    // Solicida el identificador y consulta por el objeto
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
        
    // Recorre el historial de transacciones y imprime por pantalla un reporte de ventas 
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
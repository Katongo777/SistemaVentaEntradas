package vista;

import java.io.*;
import java.util.ArrayList;
import controlador.GestorVentas;
import controlador.ManejadorArchivos;
import modelo.Categorias;
import modelo.Evento;
import modelo.Ticket;
import modelo.Usuario;

/*
    Mantiene el ciclo de ejecución activo, captura las opciones del usuario para delegar las operaciones
    de entrada/salida a los submódulos correspondientes
*/

public class VistaConsola
{
    
    private GestorVentas gestor;
    private BufferedReader lector;
    
    /*
        Recibimos el mismo BufferedReader que ya usa Principal en vez de crear
        uno nuevo. Si creáramos otro sobre System.in, ambos lectores se pelean
        el buffer de entrada y la aplicación puede quedarse sin leer bien los datos.
    */
    public VistaConsola(GestorVentas ge, BufferedReader le)
    {
        gestor = ge;
        lector = le;
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
            System.out.println("-- Eventos (colección principal) --");
            System.out.println("1. Agregar evento");
            System.out.println("2. Listar eventos");
            System.out.println("3. Buscar evento");
            System.out.println("4. Editar evento");
            System.out.println("5. Eliminar evento");
            System.out.println("-- Zonas de un evento (colección anidada) --");
            System.out.println("6. Agregar zona a un evento");
            System.out.println("7. Listar zonas de un evento");
            System.out.println("8. Buscar zona en un evento");
            System.out.println("9. Editar zona de un evento");
            System.out.println("10. Eliminar zona de un evento");
            System.out.println("-- Ventas y utilidades --");
            System.out.println("11. Vender ticket");
            System.out.println("12. Listar tickets vendidos");
            System.out.println("13. Recomendar eventos para un usuario");
            System.out.println("-- Cupones de un evento --");
            System.out.println("14. Agregar cupón a un evento");
            System.out.println("15. Listar cupones de un evento");
            System.out.println("16. Eliminar cupón de un evento");
            System.out.println("0. Guardar y salir");
            System.out.print("Seleccione una opción: ");
            
            String entrada = lector.readLine();
            if (entrada == null)
            {
                // Se acabó la entrada (EOF): salimos para no quedar en un bucle infinito
                break;
            }

            try
            {
                opcion = Integer.parseInt(entrada.trim());
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
                    EditorEventos editor = new EditorEventos(gestor, lector);
                    editor.editar();
                    break;
                case 5:
                    EditorEventos borrador = new EditorEventos(gestor, lector);
                    borrador.eliminar();
                    break;
                case 6:
                    AdministradorZonas adminAgg = new AdministradorZonas(gestor, lector);
                    adminAgg.agregarZona();
                    break;
                case 7:
                    AdministradorZonas adminLis = new AdministradorZonas(gestor, lector);
                    adminLis.listarZonas();
                    break;
                case 8:
                    AdministradorZonas adminBus = new AdministradorZonas(gestor, lector);
                    adminBus.buscarZona();
                    break;
                case 9:
                    AdministradorZonas adminEdi = new AdministradorZonas(gestor, lector);
                    adminEdi.editarZona();
                    break;
                case 10:
                    AdministradorZonas adminEli = new AdministradorZonas(gestor, lector);
                    adminEli.eliminarZona();
                    break;
                case 11:
                    CreadorTickets vendedor = new CreadorTickets(gestor, lector);
                    vendedor.ejecutarVenta();
                    break;
                case 12:
                    mostrarTickets();
                    break;
                case 13:
                    recomendarEventos();
                    break;
                case 14:
                    AdministradorCupones adminCupAgg = new AdministradorCupones(gestor, lector);
                    adminCupAgg.agregarCupon();
                    break;
                case 15:
                    AdministradorCupones adminCupLis = new AdministradorCupones(gestor, lector);
                    adminCupLis.listarCupones();
                    break;
                case 16:
                    AdministradorCupones adminCupEli = new AdministradorCupones(gestor, lector);
                    adminCupEli.eliminarCupon();
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

    /*
        Pide los datos de un usuario y le muestra los eventos recomendados
        según su área de interés y su edad, apoyándose en el filtro que
        vive en el controlador (GestorVentas.eventosSugeridos).
        Es la funcionalidad propia del negocio (SIA-9).
    */
    private void recomendarEventos() throws IOException
    {
        System.out.println("\n--- RECOMENDAR EVENTOS ---");
        System.out.print("Nombre del usuario: ");
        String nombre = lector.readLine();

        int edad = 0;
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
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }

        String interes = elegirTematica();

        // Creamos un usuario temporal solo para calcular las recomendaciones
        Usuario usuario = new Usuario(nombre, "sin-rut", edad, interes);
        ArrayList<Evento> sugeridos = gestor.eventosSugeridos(usuario);

        if (sugeridos.isEmpty())
        {
            System.out.println("No encontramos eventos que coincidan con '" + interes + "' para " + nombre + ".");
            return;
        }

        System.out.println("Eventos recomendados para " + nombre + ":");
        for (Evento e : sugeridos)
        {
            System.out.println("[ID: " + e.getCodigo() + "] " + e.mostrarDetalles());
        }
    }

    /*
        Muestra las temáticas disponibles numeradas y devuelve la que elija el
        usuario. Usa la lista fija Categorias para que siempre sea válida.
    */
    private String elegirTematica() throws IOException
    {
        while (true)
        {
            System.out.println("Seleccione la temática de interés:");
            for (int i = 0; i < Categorias.TEMATICAS.length; i++)
            {
                System.out.println("  " + (i + 1) + ". " + Categorias.TEMATICAS[i]);
            }
            System.out.print("Opción: ");
            try
            {
                int opcion = Integer.parseInt(lector.readLine());
                if (opcion >= 1 && opcion <= Categorias.TEMATICAS.length)
                {
                    return Categorias.TEMATICAS[opcion - 1];
                }
                System.out.println("Número fuera de rango. Intente de nuevo...");
            }
            catch (NumberFormatException e)
            {
                System.out.println("¡ERROR: No se ingresó un número! Intente de nuevo...");
            }
        }
    }
}
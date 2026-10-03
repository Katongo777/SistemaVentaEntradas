package main;

import controlador.GestorVentas;
import controlador.ManejadorArchivos;
import vista.VistaConsola;
import vista.VistaVentanaPro;
import java.io.*;

public class Principal
{

    public static void main(String[] args) throws IOException
    {
        GestorVentas gestor = new GestorVentas();
        
        ManejadorArchivos.cargarEventos(gestor);

        BufferedReader lector = new BufferedReader( new InputStreamReader( System.in ) );
        System.out.println("=========================================");
        System.out.println(" BIENVENIDO AL SISTEMA DE EVENTOS");
        System.out.println("=========================================");
        System.out.println("Seleccione el modo de ejecución:");
        System.out.println("1. Modo Consola");
        System.out.println("2. Modo Ventana (Interfaz Gráfica)");
        System.out.print("Opción: ");
        
        String seleccion = lector.readLine();
        if (seleccion == null)
        {
            seleccion = ""; // por si la entrada llega vacía (EOF)
        }

        if (seleccion.equals("1")) // Vista de consola
        {
            System.out.println("\nIniciando en Modo Consola...\n");
            // Le pasamos el MISMO lector para que no haya dos lectores sobre System.in
            VistaConsola vista = new VistaConsola(gestor, lector);

            try
            {
                vista.iniciar();
            }
            catch (IOException e)
            {
                System.out.println("Error crítico de lectura del sistema: " + e.getMessage());
            }
        }
        else if (seleccion.equals("2"))
        {
            // Vista de ventana (interfaz gráfica con Swing).
            // Por defecto abrimos la versión Pro (la más completa y bonita).
            System.out.println("\nIniciando en Modo Ventana...");
            VistaVentanaPro ventana = new VistaVentanaPro(gestor);
            ventana.setVisible(true);
            // Nota: existen otras dos versiones de ventana para comparar. Si prefieren
            // usar una de ellas, reemplacen las dos líneas de arriba por:
            //   new vista.VistaVentana(gestor).setVisible(true);        // completa (botones)
            //   new vista.VistaVentanaSimple(gestor).setVisible(true);  // mínima
        }
        else
        {
            System.out.println("\n[!] Opción inválida. Cerrando el sistema.");
        }
    }
}
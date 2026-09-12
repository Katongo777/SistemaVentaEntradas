package main;

import controlador.GestorVentas;
import controlador.ManejadorArchivos;
import vista.VistaConsola;
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

        if (seleccion.equals("1")) // Vista de consola
        {
            System.out.println("\nIniciando en Modo Consola...\n");
            VistaConsola vista = new VistaConsola(gestor);
            
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
            // Vista de ventana
            System.out.println("\n[!] Modo Ventana en construcción...");
        }
        else
        {
            System.out.println("\n[!] Opción inválida. Cerrando el sistema.");
        }
    }
}
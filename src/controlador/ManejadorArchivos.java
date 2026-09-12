package controlador;

import modelo.Evento;
import modelo.Charla;
import modelo.Seminario;
import modelo.Ubicacion;
import java.io.*;
import java.util.HashMap;

public class ManejadorArchivos
{
    
    // Archivo donde se guardará la información
    private static final String ARCHIVO_EVENTOS = "eventos.csv";

    public static void cargarEventos(GestorVentas gestor)
    {
        File archivo = new File(ARCHIVO_EVENTOS);
        
        // Si el archivo no existe
        if (!archivo.exists())
        {
            System.out.println("No se encontró historial previo. Cargando datos iniciales por defecto...");
            
            // Evento 1 (CHARLA)
            Charla charlaInicial = new Charla("EV-01", "Introducción a la Ciberseguridad", "Tecnología", "Alan Turing");
            charlaInicial.agregarUbicacion(new Ubicacion("General", 50, 5000.0));
            gestor.agregarEvento(charlaInicial);
            
            // Evento 2 (SEMINARIO)
            Seminario seminarioInicial = new Seminario("EV-02", "Gestión de Proyectos Ágiles", "Negocios", 3);
            seminarioInicial.agregarUbicacion(new Ubicacion("General", 30, 15000.0));
            gestor.agregarEvento(seminarioInicial);
            
            return;
        }

        // Lectura y construcción
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo)))
        {
            String linea;
            while ((linea = lector.readLine()) != null)
            {
                String[] datos = linea.split(",");
                if (datos.length < 7) continue; // Saltamos líneas mal formadas
                
                String tipo = datos[0];
                String codigo = datos[1];
                String nombre = datos[2];
                String tematica = datos[3];
                String atributoExtra = datos[4];
                int capacidad = Integer.parseInt(datos[5]);
                double precio = Double.parseDouble(datos[6]);
                
                Evento eventoReconstruido = null;
                
                if (tipo.equals("Charla"))
                {
                    eventoReconstruido = new Charla(codigo, nombre, tematica, atributoExtra);
                }
                else if (tipo.equals("Seminario"))
                {
                    int duracion = Integer.parseInt(atributoExtra);
                    eventoReconstruido = new Seminario(codigo, nombre, tematica, duracion);
                }
                
                if (eventoReconstruido != null)
                {
                    eventoReconstruido.agregarUbicacion(new Ubicacion("General", capacidad, precio));
                    gestor.agregarEvento(eventoReconstruido);
                }
            }
            System.out.println("Datos cargados exitosamente desde " + ARCHIVO_EVENTOS);
        }
        catch (IOException | NumberFormatException e)
        {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    public static void guardarEventosBatch(HashMap<String, Evento> mapaEventos)
    {
        // Cierra el archivo al terminar
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_EVENTOS)))
        {
            for (Evento e : mapaEventos.values())
            {
                // Revisamos si es Charla o Seminario
                String tipo = e.getClass().getSimpleName(); 
                String atributoExtra = "";
                
                if (e instanceof Charla)
                {
                    atributoExtra = ((Charla) e).getExpositorPrincipal();
                }
                else if (e instanceof Seminario)
                {
                    atributoExtra = String.valueOf(((Seminario) e).getDuracionDias());
                }
                
                // Extraemos la ubicación
                int capacidad = 0;
                double precio = 0.0;
                if (!e.getZonas().isEmpty())
                {
                    Ubicacion u = e.getZonas().get(0);
                    capacidad = u.getCapacidadMaxima();
                    precio = u.getPrecioBase();
                }
                
                // Concatenamos todo siguiendo el formato delimitado por comas
                String lineaCsv = tipo + "," + e.getCodigo() + "," + e.getNombre() + "," + 
                                  e.getTematica() + "," + atributoExtra + "," + capacidad + "," + precio;
                
                bw.write(lineaCsv);
                bw.newLine();
            }
            System.out.println("Datos respaldados correctamente antes de salir.");
        }
        catch (IOException e)
        {
            System.out.println("Error al guardar el archivo: " + e.getMessage());
        }
    }
}
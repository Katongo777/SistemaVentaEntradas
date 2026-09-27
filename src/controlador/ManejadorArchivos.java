package controlador;

import modelo.Evento;
import modelo.Charla;
import modelo.Seminario;
import modelo.Ubicacion;
import java.io.*;
import java.util.Map;

/*
    Se encarga de la persistencia de los datos. Carga los datos desde 
    un archivo CSV al iniciar el programa y sobreescribe el archivo con 
    los datos actualizados al cerrar el programa
*/

public class ManejadorArchivos
{
    // Ruta constante del archivo donde se guardará la información localmente
    private static final String ARCHIVO_EVENTOS = "eventos.csv";

    /*
        Lee el archivo CSV y reconstruye los objetos Evento, inyectandolos
        al GestorVentas en la memoria. Si no existe un archivo previo inyecta 
        datos por defecto
    */
    public static void cargarEventos(GestorVentas gestor)
    {
        File archivo = new File(ARCHIVO_EVENTOS);
        
        // Bloque de inyección de datos base
        if (!archivo.exists())
        {
            System.out.println("No se encontró historial previo. Cargando datos iniciales por defecto...");
            
            // Evento 1 (CHARLA)
            Charla charlaInicial = new Charla("CH-01", "Introducción a la Ciberseguridad", "Tecnología", "Alan Turing");
            charlaInicial.agregarUbicacion(new Ubicacion("General", 50, 5000.0));
            gestor.agregarEvento(charlaInicial);
            
            // Evento 2 (SEMINARIO)
            Seminario seminarioInicial = new Seminario("SE-02", "Gestión de Proyectos Ágiles", "Negocios", 3);
            seminarioInicial.agregarUbicacion(new Ubicacion("General", 30, 15000.0));
            gestor.agregarEvento(seminarioInicial);
            
            return;
        }

        // Deserialización usando try-with-resources para cerrar el programa
        try (BufferedReader lector = new BufferedReader(new FileReader(archivo)))
        {
            String linea;
            while ((linea = lector.readLine()) != null)
            {
                String[] datos = linea.split(",");
                
                // Evita líneas mal formadas
                if (datos.length < 7) continue;
                
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
                
                // Si la reconstrucción fue exitosa, se le añade su ubicación anidada y se guarda
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

    /*
        Extrae los datos del catálogo desde GestorVentas y los guarda en el archivo CSV
    */
    
    public static void guardarEventosBatch(Map<String, Evento> mapaEventos)
    {
        // try-with-resources asegura la liberación de la memoria aunque ocurra un error
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_EVENTOS)))
        {
            for (Evento e : mapaEventos.values())
            {
                // Se identifica si la subclase es Charla o Seminario para extrar su atributo único
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
                
                // Extracción de los datos de la primera zona
                int capacidad = 0;
                double precio = 0.0;
                if (!e.getZonas().isEmpty())
                {
                    Ubicacion u = e.getZonas().get(0);
                    capacidad = u.getCapacidadMaxima();
                    precio = u.getPrecioBase();
                }
                
                /*
                     Construcción de la línea para el archivo CSV, concatenando todo siguiendo el
                     formato CSV delimitado por comas
                */
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
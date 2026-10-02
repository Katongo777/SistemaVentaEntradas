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
            charlaInicial.agregarCupon("PROMO50", 0.5);   // 50% de descuento
            charlaInicial.agregarCupon("ESTUDIANTE", 0.2); // 20% de descuento
            gestor.agregarEvento(charlaInicial);

            // Evento 2 (SEMINARIO)
            Seminario seminarioInicial = new Seminario("SE-02", "Gestión de Proyectos Ágiles", "Negocios", 3);
            seminarioInicial.agregarUbicacion(new Ubicacion("General", 30, 15000.0));
            seminarioInicial.agregarCupon("BIENVENIDA", 0.1); // 10% de descuento
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
                if (datos.length < 6) continue;
                
                String tipo = datos[0];
                String codigo = datos[1];
                String nombre = datos[2];
                String tematica = datos[3];
                String atributoExtra = datos[4];
                String zonasEmpaquetadas = (datos[5]); // Ej; "General:50:5000.0|VIP:20:15000.0"
                // El 7mo campo (cupones) puede no existir en archivos antiguos
                String cuponesEmpaquetados = (datos.length >= 7) ? datos[6] : "SIN_CUPONES";

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
                    // Desempaquetar las zonas usando el separador "|"
                    if (!zonasEmpaquetadas.equals("SIN_ZONAS") && !zonasEmpaquetadas.isEmpty())
                    {
                        String[] arrayZonas = zonasEmpaquetadas.split("\\|"); 
                        
                        for (String zonaIndividual : arrayZonas)
                        {
                            // Separar los atributos de la zona usando ":"
                            String[] atributosZona = zonaIndividual.split(":");
                            if (atributosZona.length == 3)
                            {
                                String nombreZona = atributosZona[0];
                                int cap = Integer.parseInt(atributosZona[1]);
                                double precio = Double.parseDouble(atributosZona[2]);
                                
                                eventoReconstruido.agregarUbicacion(new Ubicacion(nombreZona, cap, precio));
                            }
                        }
                    }

                    // Desempaquetar los cupones (formato "CODIGO:0.5;CODIGO2:0.2")
                    if (!cuponesEmpaquetados.equals("SIN_CUPONES") && !cuponesEmpaquetados.isEmpty())
                    {
                        String[] arrayCupones = cuponesEmpaquetados.split(";");
                        for (String cuponIndividual : arrayCupones)
                        {
                            String[] partes = cuponIndividual.split(":");
                            if (partes.length == 2)
                            {
                                String codigoCupon = partes[0];
                                double descuento = Double.parseDouble(partes[1]);
                                eventoReconstruido.agregarCupon(codigoCupon, descuento);
                            }
                        }
                    }

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
                
                // Empaquetar todas las zonas de la colección anidada
                StringBuilder sbZonas = new StringBuilder();
                if (e.getZonas().isEmpty())
                {
                    sbZonas.append("SIN_ZONAS");
                }
                else
                {
                    for (int i = 0; i < e.getZonas().size(); i++)
                    {
                        Ubicacion u = e.getZonas().get(i);
                        // Formato: Nombre:Capacidad:Precio
                        sbZonas.append(u.getNombreZona()).append(":")
                               .append(u.getCapacidadMaxima()).append(":")
                               .append(u.getPrecioBase());
                        
                        // Añadir separador '|' si no es el último elemento
                        if (i < e.getZonas().size() - 1)
                        {
                            sbZonas.append("|");
                        }
                    }
                }
                
                // Empaquetar los cupones del evento (formato "CODIGO:0.5;CODIGO2:0.2")
                StringBuilder sbCupones = new StringBuilder();
                if (e.getCupones().isEmpty())
                {
                    sbCupones.append("SIN_CUPONES");
                }
                else
                {
                    boolean primero = true;
                    for (Map.Entry<String, Double> cupon : e.getCupones().entrySet())
                    {
                        if (!primero)
                        {
                            sbCupones.append(";");
                        }
                        sbCupones.append(cupon.getKey()).append(":").append(cupon.getValue());
                        primero = false;
                    }
                }

                /*
                     Construcción de la línea para el archivo CSV, concatenando todo siguiendo el
                     formato CSV delimitado por comas
                */
                String lineaCsv = tipo + "," + e.getCodigo() + "," + e.getNombre() + "," + e.getTematica() + "," + atributoExtra + "," + sbZonas.toString() + "," + sbCupones.toString();
                
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
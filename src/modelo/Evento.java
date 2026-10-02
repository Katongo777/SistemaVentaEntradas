package modelo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/*
    Clase abstracta base del dominio que define los atributos
    comunes de cualquier evento.
    Encapsula una colección anidada de zonas (Ubicacion) y además una lista de
    cupones de descuento propios del evento (código -> porcentaje de descuento).
*/

public abstract class Evento
{
    private String codigo;
    private String nombre;
    private String tematica;
    private ArrayList<Ubicacion> zonas;
    // Cupones propios del evento: código -> descuento (ej: "PROMO50" -> 0.5 = 50%)
    private Map<String, Double> cupones;

    public Evento(String co, String no, String te)
    {
        codigo = co;
        nombre = no;
        tematica = te;
        zonas =  new ArrayList<>();
        cupones = new LinkedHashMap<>();
    }

    /*
        Método abstracto que obliga a las subclases a implementar su 
        propia lógica de visualización
    */
    public abstract String mostrarDetalles();
    
    public void agregarUbicacion(Ubicacion ub)
    {
        zonas.add(ub);
    }

    /*
        Busca una zona por su nombre dentro de la colección anidada.
        Devuelve la Ubicacion si la encuentra o null si no existe.
        Lo dejamos aquí para no exponer la lista completa hacia afuera.
    */
    public Ubicacion buscarUbicacion(String nombreZona)
    {
        for (Ubicacion u : zonas)
        {
            if (u.getNombreZona().equalsIgnoreCase(nombreZona))
            {
                return u;
            }
        }
        return null;
    }

    /*
        Elimina una zona de la colección anidada buscándola por nombre.
        Retorna true si se pudo borrar y false si no se encontró.
    */
    public boolean eliminarUbicacion(String nombreZona)
    {
        Ubicacion u = buscarUbicacion(nombreZona);
        if (u != null)
        {
            zonas.remove(u);
            return true;
        }
        return false;
    }

    /*
        --- Cupones del evento ---
        Guardamos el código siempre en mayúsculas para no tener problemas si el
        usuario lo escribe distinto. El descuento es una fracción: 0.5 = 50%.
    */
    public void agregarCupon(String codigo, double descuento)
    {
        cupones.put(codigo.toUpperCase(), descuento);
    }

    // Devuelve el descuento del cupón (0.0 si el cupón no existe en este evento)
    public double getDescuentoCupon(String codigo)
    {
        if (codigo == null)
        {
            return 0.0;
        }
        Double d = cupones.get(codigo.toUpperCase());
        return (d == null) ? 0.0 : d;
    }

    public boolean eliminarCupon(String codigo)
    {
        return cupones.remove(codigo.toUpperCase()) != null;
    }

    // Vista inmodificable de los cupones (para listarlos sin poder alterarlos)
    public Map<String, Double> getCupones()
    {
        return Collections.unmodifiableMap(cupones);
    }

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String co) { codigo = co; }

    public String getNombre() { return nombre; }
    public void setNombre(String no) { nombre = no; }

    public String getTematica() { return tematica; }
    public void setTematica(String te) { tematica = te; }
    
    /*
        Retorna una vista inmodificable de las ubicaciones del evento,
        protegiendo la colección de modificaciones externas
    */
    public List<Ubicacion> getZonas()
    {
        return Collections.unmodifiableList(zonas);
    }
}
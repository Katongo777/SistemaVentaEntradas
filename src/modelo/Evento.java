package modelo;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

/*
    Clase abstracta base del dominio que define los atributos 
    comunes de cualquier evento.
    Encapsula una colección anidada de zonas (Ubicacion)
*/

public abstract class Evento
{
    private String codigo;
    private String nombre;
    private String tematica;
    private ArrayList<Ubicacion> zonas;

    public Evento(String co, String no, String te)
    {
        codigo = co;
        nombre = no;
        tematica = te;
        zonas =  new ArrayList<>();
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
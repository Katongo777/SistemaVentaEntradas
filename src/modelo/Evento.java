package modelo;

import java.util.ArrayList;

public abstract class Evento
{
    private String codigo;
    private String nombre;
    private String tematica;
    // Colección anidada
    private ArrayList<Ubicacion> zonas;

    public Evento(String codigo, String nombre, String tematica)
    {
        this.codigo = codigo;
        this.nombre = nombre;
        this.tematica = tematica;
        this.zonas = new ArrayList<>();
    }

    public void agregarUbicacion(Ubicacion u)
    {
        this.zonas.add(u);
    }

    public abstract String mostrarDetalles();

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTematica() { return tematica; }
    public void setTematica(String tematica) { this.tematica = tematica; }

    public ArrayList<Ubicacion> getZonas() { return zonas; }
    public void setZonas(ArrayList<Ubicacion> zonas) { this.zonas = zonas; }
}
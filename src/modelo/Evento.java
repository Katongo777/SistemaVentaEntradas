package modelo;

import java.util.ArrayList;

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

    public ArrayList<Ubicacion> getZonas() { return zonas; }
    public void setZonas(ArrayList<Ubicacion> zo) { zonas = zo; }
}
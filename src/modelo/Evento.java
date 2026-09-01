package modelo;

public abstract class Evento
{
    private String codigo;
    private String nombre;
    private String tematica;
    private Ubicacion zona;

    public Evento(String co, String no, String te, Ubicacion zo)
    {
        codigo = co;
        nombre = no;
        tematica = te;
        zona = zo;
    }

    public abstract String mostrarDetalles();

    // Getters y Setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String co) { codigo = co; }

    public String getNombre() { return nombre; }
    public void setNombre(String no) { nombre = no; }

    public String getTematica() { return tematica; }
    public void setTematica(String te) { tematica = te; }

    public Ubicacion getZona() { return zona; }
    public void setZona(Ubicacion zo) { zona = zo; }
}
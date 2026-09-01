package modelo;

public class Usuario
{
    private String nombre;
    private String rut;
    private int edad;
    private String tematicaInteres;

    public Usuario(String no, String ru, int ed, String teIn)
    {
        nombre = no;
        rut = ru;
        edad = ed;
        tematicaInteres = teIn;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String no) { nombre = no; }
    
    public String getRut() { return rut; }
    public void setRut(String ru) { rut = ru; }

    public int getEdad() { return edad; }
    public void setEdad(int ed) { edad = ed; }

    public String getAreaInteres() { return tematicaInteres; }
    public void setAreaInteres(String teIn) { tematicaInteres = teIn; }
}
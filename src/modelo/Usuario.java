package modelo;

public class Usuario
{
    private String nombre;
    private String rut;
    private int edad;
    private String areaInteres;

    public Usuario(String no, String ru, int ed, String arIn)
    {
        nombre = no;
        rut = ru;
        edad = ed;
        areaInteres = arIn;
    }

    // Getters y Setters
    public String getNombre() { return nombre; }
    public void setNombre(String no) { nombre = no; }
    
    public String getRut() { return rut; }
    public void setRut(String ru) { rut = ru; }

    public int getEdad() { return edad; }
    public void setEdad(int ed) { edad = ed; }

    public String getAreaInteres() { return areaInteres; }
    public void setAreaInteres(String arIn) { areaInteres = arIn; }
}
package modelo;

/*
    Representa a la persona que compra un ticket. Guarda sus datos básicos
    (nombre, rut, edad y área de interés). La edad se usa para los descuentos
    y para las validaciones de venta, y el área de interés para recomendar eventos.
*/
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
package modelo;

public class Seminario extends Evento
{
    private int duracionDias;

    public Seminario(String codigo, String nombre, String tematica, int duDi, Ubicacion zona)
    {
        super(codigo, nombre, tematica, zona);
        duracionDias = duDi;
    }

    // Sobreescritura de método
    @Override
    public String mostrarDetalles()
    {
        return "Seminario: " + getNombre() + " | Temática: " + getTematica() + " | Duración: " + duracionDias + " días";
    }

    // Getters y Setters
    public int getDuracionDias() { return duracionDias; }
    public void setDuracionDias(int duDi) { duracionDias = duDi; }
}
package modelo;

public class Seminario extends Evento
{
    private int duracionDias;

    public Seminario(String codigo, String nombre, String tematica, int duracionDias)
    {
        super(codigo, nombre, tematica);
        this.duracionDias = duracionDias;
    }

    // Sobreescritura de método
    @Override
    public String mostrarDetalles()
    {
        return "Seminario: " + getNombre() + " | Temática: " + getTematica() + " | Duración: " + duracionDias + " días";
    }

    // Getters y Setters
    public int getDuracionDias() { return duracionDias; }
    public void setDuracionDias(int duracionDias) { this.duracionDias = duracionDias; }
}
package modelo;

/*
    Clase que hereda de Evento, representa un evento académico de
    mayor extensión, añadiendo el atributo de la duración en días del evento
*/

public class Seminario extends Evento
{
    private int duracionDias;

    public Seminario(String codigo, String nombre, String tematica, int duDi)
    {
        super(codigo, nombre, tematica);
        duracionDias = duDi;
    }

    /*
        Sobreescritura de método, retorna una cadena con los detalles específicos
        de un Seminario
    */
    @Override
    public String mostrarDetalles()
    {
        return "Seminario: " + getNombre() + " | Temática: " + getTematica() + " | Duración: " + duracionDias + " días";
    }

    // Getters y Setters
    public int getDuracionDias() { return duracionDias; }
    public void setDuracionDias(int duDi) { duracionDias = duDi; }
}
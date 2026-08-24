package modelo;

public class Charla extends Evento
{
    private String expositorPrincipal;

    public Charla(String codigo, String nombre, String tematica, String expositorPrincipal)
    {
        super(codigo, nombre, tematica);
        this.expositorPrincipal = expositorPrincipal;
    }

    // Sobreescritura de método
    @Override
    public String mostrarDetalles()
    {
        return "Charla: " + getNombre() + " | Temática: " + getTematica() + " | Expositor: " + expositorPrincipal;
    }

    public String getExpositorPrincipal() { return expositorPrincipal; }
    public void setExpositorPrincipal(String expositorPrincipal) { this.expositorPrincipal = expositorPrincipal; }
}
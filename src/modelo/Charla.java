package modelo;

public class Charla extends Evento
{
    private String expositorPrincipal;

    public Charla(String codigo, String nombre, String tematica, String exPo, Ubicacion zona)
    {
        super(codigo, nombre, tematica, zona);
        expositorPrincipal = exPo;
    }

    // Sobreescritura de método
    @Override
    public String mostrarDetalles()
    {
        return "Charla: " + getNombre() + " | Temática: " + getTematica() + " | Expositor: " + expositorPrincipal;
    }

    public String getExpositorPrincipal() { return expositorPrincipal; }
    public void setExpositorPrincipal(String exPo) { expositorPrincipal = exPo; }
}
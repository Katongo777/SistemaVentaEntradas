package modelo;

/*
    Clase que hereda Evento, representa una exposición enfocada en 
    una temática, añadiendo el atributo de un expositor principal
*/

public class Charla extends Evento
{
    private String expositorPrincipal;

    public Charla(String codigo, String nombre, String tematica, String exPo)
    {
        super(codigo, nombre, tematica);
        expositorPrincipal = exPo;
    }

    /*
        Sobreescritura de método, retorna una cadena con los detalles específicos
        de una Charla
    */
    @Override
    public String mostrarDetalles()
    {
        return "Charla: " + getNombre() + " | Temática: " + getTematica() + " | Expositor: " + expositorPrincipal;
    }

    public String getExpositorPrincipal() { return expositorPrincipal; }
    public void setExpositorPrincipal(String exPo) { expositorPrincipal = exPo; }
}
package modelo;

public class Ticket
{
    private String codigoTicket;
    private Usuario comprador;
    private Evento evento;
    private Ubicacion ubicacion;
    private double costoFinal;

    public Ticket(String coRe, Usuario co, Evento ev, Ubicacion ub)
    {
        codigoTicket = coRe;
        comprador= co;
        evento = ev;
        ubicacion = ub;
        costoFinal = ubicacion.getPrecioBase(); 
        // NOTA:  Falta hacer la logica de poner precios distintos según condiciones;
    }

    // Getters y Setters
    public String getCodigoReserva() { return codigoTicket; }
    public void setCodigoReserva(String coTi) { codigoTicket = coTi; }
    
    public Usuario getAsistente() { return comprador; }
    public void setAsistente(Usuario co) { comprador = co; }

    public Evento getEvento() { return evento; }
    public void setEvento(Evento ev) { evento = ev; }

    public Ubicacion getUbicacion() { return ubicacion; }
    public void setUbicacion(Ubicacion ub) { ubicacion = ub; }

    public double getCostoFinal() { return costoFinal; }
    public void setCostoFinal(double coFi) { costoFinal = coFi; }
}
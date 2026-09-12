package modelo;

import java.util.UUID;

public class Ticket
{
    private String codigoTicket;
    private Usuario comprador;
    private Evento evento;
    private Ubicacion ubicacion;
    private double costoFinal;

    public Ticket(Usuario co, Evento ev, Ubicacion ub)
    {
        codigoTicket = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        comprador= co;
        evento = ev;
        ubicacion = ub;
    }
    
    public void calcularCostoFinal()
    {
        double precioBase = ubicacion.getPrecioBase();
        
        // Si el asistente tiene 65 años o más, 15% de descuento
        if (comprador.getEdad() >= 65)
        {
            costoFinal = precioBase * 0.85; 
        }
        else
        {
            costoFinal = precioBase;
        }
    }

    public void calcularCostoFinal(String codigoDescuento)
    {
        calcularCostoFinal(); 
        
        if (codigoDescuento.equalsIgnoreCase("COMPLETOSIBC"))
        {
            costoFinal = costoFinal * 0.50;
        } else {
            System.out.println("Cupón inválido. No se aplica descuento.");
        }
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
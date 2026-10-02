package modelo;

import java.util.UUID;

/*
    Representa la entrada (ticket) que compra un usuario para un evento
    en una zona (Ubicacion) específica. Guarda quién compró, a qué evento,
    en qué zona y cuánto pagó. El código del ticket se genera solo con un
    UUID para que sea único y no se pueda adivinar.

    Aquí también está la SOBRECARGA de métodos (SIA-5): calcularCostoFinal()
    sin cupón y calcularCostoFinal(String) con cupón de descuento.
*/
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

    /*
        Sobrecarga: misma idea pero aplicando además un cupón de descuento.
        El cupón se valida contra los cupones que tiene ESTE evento; si es válido
        se aplica su porcentaje, y si no, no se aplica descuento extra.
    */
    public void calcularCostoFinal(String codigoDescuento)
    {
        calcularCostoFinal();

        double descuento = evento.getDescuentoCupon(codigoDescuento);
        if (descuento > 0)
        {
            costoFinal = costoFinal * (1 - descuento);
        }
        else
        {
            System.out.println("Cupón inválido para este evento. No se aplica descuento.");
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
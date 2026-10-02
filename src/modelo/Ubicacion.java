package modelo;

/*
    Representa una zona o ubicación dentro de un evento (Ej: General, VIP).
    Es el elemento de la colección ANIDADA: cada Evento tiene una lista de
    Ubicaciones. Controla su capacidad y cuántos asientos ya se vendieron
    para saber si todavía queda stock.
*/
public class Ubicacion
{
    private String nombreZona;
    private int capacidadMaxima;
    private int asientosVendidos;
    private double precioBase;

    public Ubicacion(String noZo, int caMa, double prBa)
    {
        nombreZona = noZo;
        capacidadMaxima = caMa;
        precioBase = prBa;
        asientosVendidos = 0;
    }

    public boolean hayStock()
    {
        return asientosVendidos < capacidadMaxima;
    }

    // Getters y Setters
    public String getNombreZona() { return nombreZona; }
    public void setNombreZona(String noZo) { nombreZona = noZo; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int caMa) { capacidadMaxima = caMa; }

    public int getAsientosVendidos() { return asientosVendidos; }
    public void setAsientosVendidos(int asVe) { asientosVendidos = asVe; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double prBa) { precioBase = prBa; }
}
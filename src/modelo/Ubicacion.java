package modelo;

public class Ubicacion
{
    private String nombreZona;
    private int capacidadMaxima;
    private int asientosVendidos;
    private double precioBase;

    public Ubicacion(String nombreZona, int capacidadMaxima, double precioBase)
    {
        this.nombreZona = nombreZona;
        this.capacidadMaxima = capacidadMaxima;
        this.precioBase = precioBase;
        this.asientosVendidos = 0;
    }

    public boolean hayStock()
    {
        return asientosVendidos < capacidadMaxima;
    }

    // Getters y Setters
    public String getNombreZona() { return nombreZona; }
    public void setNombreZona(String nombreZona) { this.nombreZona = nombreZona; }

    public int getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(int capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public int getAsientosVendidos() { return asientosVendidos; }
    public void setAsientosVendidos(int asientosVendidos) { this.asientosVendidos = asientosVendidos; }

    public double getPrecioBase() { return precioBase; }
    public void setPrecioBase(double precioBase) { this.precioBase = precioBase; }
}
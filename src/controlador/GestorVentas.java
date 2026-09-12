package controlador;

import modelo.Evento;
import modelo.Ticket;
import modelo.Usuario;
import modelo.Ubicacion;
import modelo.Seminario;
import excepciones.StockAgotadoException;
import excepciones.EdadInsuficienteException;

import java.util.HashMap;
import java.util.ArrayList;

public class GestorVentas
{
    
    private HashMap<String, Evento> eventosMa;
    private ArrayList<Ticket> ticketsHistorialLi;

    public GestorVentas()
    {
        eventosMa  = new HashMap<>();
        ticketsHistorialLi = new ArrayList<>();
    }

    public void agregarEvento(Evento evento)
    {
        eventosMa.put(evento.getCodigo(), evento);
    }
    
    public void eliminarEvento(String codigo)
    {
        eventosMa.remove(codigo);
    }

    public Evento buscarEvento(String codigo)
    {
        return eventosMa.get(codigo);
    }

    public ArrayList<Evento> eventosSugeridos(Usuario comprador)
    {
        ArrayList<Evento> coincidencias = new ArrayList<>();
        
        // FALTA CONSTRUIR EL ALGORITMO PARA LAS SUGERENCIAS SEGÚN LA TEMATICA Y/O EDAD
            
        return coincidencias;
    }

    public Ticket venderTicket(String codigoTicket, Usuario comprador, Evento evento, Ubicacion zona) 
            throws StockAgotadoException, EdadInsuficienteException
    {   
        if (!zona.hayStock())
        {
            throw new StockAgotadoException("Venta fallida: La zona " + zona.getNombreZona()+ " del evento '" + evento.getNombre() + " está agotada.");
        }
        
        if (evento instanceof Seminario && comprador.getEdad() < 18)
        {
            throw new EdadInsuficienteException("Venta fallida: El comprador " + comprador.getNombre() + " es menor de edad y no puede asistir a este Seminario.");
        }

        zona.setAsientosVendidos(zona.getAsientosVendidos() + 1);

        Ticket nuevoTicket = new Ticket(codigoTicket, comprador, evento, zona);
        
        ticketsHistorialLi.add(nuevoTicket);

        return nuevoTicket;
    }

    public HashMap<String, Evento> getMapaEventos() { return eventosMa; }
    public ArrayList<Ticket> getHistorialVentas() { return ticketsHistorialLi; }
}
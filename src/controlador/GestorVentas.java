package controlador;

import modelo.Evento;
import modelo.Ticket;
import modelo.Usuario;
import modelo.Ubicacion;
import modelo.Seminario;
import excepciones.StockAgotadoException;
import excepciones.EdadInsuficienteException;

import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.text.Normalizer;

/*
    Centraliza el almacenamiento en memoria (HashMap y ArrayList) y procesa
    la lógica de negocio para la venta, asegurando el encapsulamiento, stock y edad
*/

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

    /*
        Funcionalidad propia del negocio (SIA-9): a partir de un usuario devuelve
        un subconjunto FILTRADO del catálogo con los eventos que le podrían interesar.
        Criterio:
          - La temática del evento coincide con el área de interés del usuario
            (comparación sin distinguir mayúsculas y por coincidencia parcial).
          - Si el usuario es menor de 18 años se descartan los Seminarios,
            porque en la venta esos eventos exigen ser mayor de edad.
        No modifica el catálogo, solo arma una lista nueva con las coincidencias.
    */
    public ArrayList<Evento> eventosSugeridos(Usuario comprador)
    {
        ArrayList<Evento> coincidencias = new ArrayList<>();

        String interes = comprador.getAreaInteres();
        if (interes == null)
        {
            return coincidencias;
        }
        // Normalizamos para comparar sin tildes ni mayúsculas (ej: "Tecnologia" == "Tecnología")
        interes = normalizar(interes);

        for (Evento evento : eventosMa.values())
        {
            // Regla de edad: los menores no ven Seminarios
            if (evento instanceof Seminario && comprador.getEdad() < 18)
            {
                continue;
            }

            String tematica = normalizar(evento.getTematica());
            if (tematica.contains(interes) || interes.contains(tematica))
            {
                coincidencias.add(evento);
            }
        }

        return coincidencias;
    }

    /*
        Deja un texto en minúsculas y sin tildes para poder compararlo de forma
        más flexible (así "Tecnología", "tecnologia" y "TECNOLOGIA" se consideran iguales).
    */
    private String normalizar(String texto)
    {
        String sinTildes = Normalizer.normalize(texto, Normalizer.Form.NFD)
                                     .replaceAll("\\p{M}", "");
        return sinTildes.trim().toLowerCase();
    }

    // Venta estándar
    public Ticket venderTicket(Usuario comprador, Evento evento, Ubicacion zona) 
            throws StockAgotadoException, EdadInsuficienteException
    {
        if (!zona.hayStock())
        {
            throw new StockAgotadoException("Venta fallida: La zona " + zona.getNombreZona()+ " está agotada.");
        }
        if (evento instanceof Seminario && comprador.getEdad() < 18)
        {
            throw new EdadInsuficienteException("Venta fallida: El comprador es menor de edad.");
        }

        zona.setAsientosVendidos(zona.getAsientosVendidos() + 1);
        Ticket nuevoTicket = new Ticket(comprador, evento, zona);
        nuevoTicket.calcularCostoFinal();
        ticketsHistorialLi.add(nuevoTicket);

        return nuevoTicket;
    }

    // Venta sobrecargada (con cupón)
    public Ticket venderTicket(Usuario comprador, Evento evento, Ubicacion zona, String cupon) 
            throws StockAgotadoException, EdadInsuficienteException
    {
        if (!zona.hayStock())
        {
            throw new StockAgotadoException("Venta fallida: La zona " + zona.getNombreZona()+ " está agotada.");
        }
        if (evento instanceof Seminario && comprador.getEdad() < 18)
        {
            throw new EdadInsuficienteException("Venta fallida: El comprador es menor de edad.");
        }

        zona.setAsientosVendidos(zona.getAsientosVendidos() + 1);
        Ticket nuevoTicket = new Ticket(comprador, evento, zona);
        nuevoTicket.calcularCostoFinal(cupon);
        ticketsHistorialLi.add(nuevoTicket);

        return nuevoTicket;
    }

    /*
        Retorna una vista inmodificable del mapa de eventos para prevenir que 
        clases externas modifiquen el catálogo directamente
    */
    public Map<String, Evento> getMapaEventos()
    { 
        return Collections.unmodifiableMap(eventosMa);
    }
    
    /*
        Retorna una vista inmodificable del historial de tickets, protegiendo el
        registro de ventas
    */
    public List<Ticket> getHistorialVentas()
    {
        return Collections.unmodifiableList(ticketsHistorialLi);
    
    }
}
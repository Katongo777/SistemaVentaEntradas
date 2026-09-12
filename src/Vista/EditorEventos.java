package vista;

import java.io.BufferedReader;
import java.io.IOException;
import controlador.GestorVentas;
import modelo.Evento;

public class EditorEventos
{
    
    private GestorVentas gestor;
    private BufferedReader lector;

    public EditorEventos(GestorVentas gestor, BufferedReader lector)
    {
        this.gestor = gestor;
        this.lector = lector;
    }

    public void editar() throws IOException
    {
        System.out.print("\nIngrese el código del evento a modificar: ");
        String codigo = lector.readLine();
        
        Evento evento = gestor.buscarEvento(codigo);
        if (evento != null)
        {
            System.out.println("Editando evento: " + evento.getNombre());
            System.out.print("Nuevo nombre (deje en blanco para no cambiar): ");
            String nuevoNombre = lector.readLine();
            if (!nuevoNombre.isEmpty())
            {
                evento.setNombre(nuevoNombre);
            }
            
            System.out.print("Nueva temática (deje en blanco para no cambiar): ");
            String nuevaTematica = lector.readLine();
            if (!nuevaTematica.isEmpty())
            {
                evento.setTematica(nuevaTematica);
            }
            System.out.println("¡Evento actualizado correctamente!");
        }
        else
        {
            System.out.println("Error: No existe un evento con ese código.");
        }
    }

    public void eliminar() throws IOException
    {
        System.out.print("\nIngrese el código del evento a eliminar: ");
        String codigo = lector.readLine();
        
        if (gestor.buscarEvento(codigo) != null)
        {
            gestor.eliminarEvento(codigo);
            System.out.println("¡Evento eliminado con éxito!");
        }
        else
        {
            System.out.println("Error: Evento no encontrado.");
        }
    }
}
package excepciones;

public class StockAgotadoException extends Exception
{
    public StockAgotadoException(String mensaje)
    {
        super(mensaje);
    }
}

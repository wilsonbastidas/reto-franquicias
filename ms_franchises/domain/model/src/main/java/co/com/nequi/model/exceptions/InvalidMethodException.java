package co.com.nequi.model.franchise.exceptions;

public class InvalidMethodException extends RuntimeException {
    public InvalidMethodException() {
        super("Metodo HTTP no permitido para la ruta solicitada");
    }
}

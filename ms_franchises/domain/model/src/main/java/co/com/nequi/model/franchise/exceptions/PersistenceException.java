package co.com.nequi.model.franchise.exceptions;

public class PersistenceException extends TechnicalException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
package co.com.nequi.model.exceptions;

import co.com.nequi.model.franchise.exceptions.TechnicalException;

public class PersistenceException extends TechnicalException {

    public PersistenceException(String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
package co.com.nequi.model.franchise.exceptions;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {
    public static final int SUCCESSFUL_REQUEST_CODE = 200;

    public static final int BAD_REQUEST_ERROR_CODE = 400;
    public static final String BAD_REQUEST_ERROR_EXCEPTION = "Bad Request";

    public static final int NOT_FOUND_ERROR_CODE = 404;
    public static final String NOT_FOUND_ERROR_EXCEPTION = "Not found";

    public static final int INVALID_METHOD_ERROR_CODE = 405;
    public static final String INVALID_METHOD_ERROR_EXCEPTION = "Method Not Allowed";
    public static final String INVALID_METHOD_ERROR_DETAIL = "Invalid HTTP method.";

    public static final int INTERNAL_SERVER_ERROR_CODE = 500;
    public static final String INTERNAL_SERVER_ERROR_EXCEPTION = "Internal Server Error";
    public static final String INTERNAL_SERVER_ERROR_DETAIL = "Ha ocurrido un error interno controlado en el servidor";

    public static final int SERVICE_UNAVAILABLE_ERROR_CODE = 503;
    public static final String SERVICE_UNAVAILABLE_ERROR_EXCEPTION = "Service Unavailable";
}

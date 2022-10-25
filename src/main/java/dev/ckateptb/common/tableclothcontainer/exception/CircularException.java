package dev.ckateptb.common.tableclothcontainer.exception;

public class CircularException extends RuntimeException {

    /**
     * Default constructor.
     */
    public CircularException() {
        super();
    }

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public CircularException(String message) {
        super(message);
    }

}

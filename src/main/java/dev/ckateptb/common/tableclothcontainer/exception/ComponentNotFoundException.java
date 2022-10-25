package dev.ckateptb.common.tableclothcontainer.exception;

public class ComponentNotFoundException extends RuntimeException {

    /**
     * Default constructor.
     */
    public ComponentNotFoundException() {
        super();
    }

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public ComponentNotFoundException(String message) {
        super(message);
    }

}

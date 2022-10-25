package dev.ckateptb.common.tableclothcontainer.exception;

public class ComponentConstructorNotFoundException extends RuntimeException {

    /**
     * Default constructor.
     */
    public ComponentConstructorNotFoundException() {
        super();
    }

    /**
     * Constructor that allows a specific error message to be specified.
     *
     * @param message the detail message.
     */
    public ComponentConstructorNotFoundException(String message) {
        super(message);
    }

}

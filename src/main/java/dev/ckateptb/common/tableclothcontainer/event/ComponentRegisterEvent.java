package dev.ckateptb.common.tableclothcontainer.event;

import dev.ckateptb.common.tableclothcontainer.container.Container;
import dev.ckateptb.common.tableclothevent.Event;
import lombok.Getter;

/**
 * The event fires immediately after the component is registered in the container
 */
@Getter
public final class ComponentRegisterEvent<T> implements Event {
    private final Container container;
    private final Class<T> clazz;
    private final String identifier;
    private final T instance;

    /**
     * @param container  the container in which the component is registered
     * @param clazz      the class of the component being registered
     * @param identifier identifier of the registered component
     * @param instance   instance of registered component
     */
    public ComponentRegisterEvent(Container container, Class<T> clazz, String identifier, T instance) {
        this.container = container;
        this.clazz = clazz;
        this.identifier = identifier;
        this.instance = instance;
    }
}

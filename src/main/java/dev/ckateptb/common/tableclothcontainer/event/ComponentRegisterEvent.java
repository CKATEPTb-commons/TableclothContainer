package dev.ckateptb.common.tableclothcontainer.event;

import dev.ckateptb.common.tableclothcontainer.container.Container;
import dev.ckateptb.common.tableclothevent.Event;

/**
 * The event fires immediately after the component is registered in the container
 * @param container the container in which the component is registered
 * @param clazz the class of the component being registered
 * @param identifier identifier of the registered component
 * @param instance instance of registered component
 */
public record ComponentRegisterEvent<T>(Container container, Class<T> clazz, String identifier, T instance) implements Event {
}

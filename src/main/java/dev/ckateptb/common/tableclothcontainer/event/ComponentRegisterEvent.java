package dev.ckateptb.common.tableclothcontainer.event;

import dev.ckateptb.common.tableclothcontainer.container.Container;
import dev.ckateptb.common.tableclothevent.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The event fires immediately after the component is registered in the container
 */
@Getter
@RequiredArgsConstructor
public final class ComponentRegisterEvent<T> implements Event {
    private final Container container;
    private final Class<T> clazz;
    private final Class<T> owner;
    private final T instance;
}
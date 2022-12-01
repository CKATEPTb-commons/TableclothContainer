package dev.ckateptb.common.tableclothcontainer.event;

import dev.ckateptb.common.tableclothevent.CancelableEvent;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * The event fires immediately after the component is registered in the container
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class ThrowComponentNotFoundExceptionEvent<T> implements CancelableEvent {
    private final Class<T> clazz;
    private final String identifier;
    private boolean canceled;
    private T returnResult;
}
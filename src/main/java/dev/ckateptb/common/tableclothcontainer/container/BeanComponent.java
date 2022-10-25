package dev.ckateptb.common.tableclothcontainer.container;

import lombok.Data;

@Data
public class BeanComponent<T> {
    private final String identifier;
    private final Class<T> type;
    private final T instance;
    private Class<?> parent;
}

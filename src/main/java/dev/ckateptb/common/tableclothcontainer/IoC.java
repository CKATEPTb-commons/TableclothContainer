package dev.ckateptb.common.tableclothcontainer;

import dev.ckateptb.common.tableclothcontainer.container.BeanComponent;
import dev.ckateptb.common.tableclothcontainer.container.Container;

import java.util.Collection;
import java.util.function.Predicate;

public class IoC {
    private static final Container container = new Container();

    public static void scan(Class<?> parentClass, String... packages) {
        container.scan(parentClass, packages);
    }

    public static void scan(Class<?> parentClass, Predicate<String> filter, String... packages) {
        container.scan(parentClass, filter, packages);
    }

    public static void scan(Class<?> parentClass, Predicate<String> filter, ClassLoader classLoader, String... packages) {
        container.scan(parentClass, filter, classLoader, packages);
    }

    public static void init() {
        container.init();
    }

    public static <T> BeanComponent<T> registerBean(T bean) {
        return container.registerBean(bean);
    }

    public static <T> BeanComponent<T> registerBean(T instance, String identifier) {
        return container.registerBean(instance, identifier);
    }

    public static <T> boolean containsBean(Class<T> beanClass) {
        return container.containsBean(beanClass);
    }

    public static <T> boolean containsBean(Class<T> beanClass, String identifier) {
        return container.containsBean(beanClass, identifier);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return container.getBean(beanClass);
    }

    public static <T> T getBean(Class<T> beanClass, String identifier) {
        return container.getBean(beanClass, identifier);
    }

    public static Collection<?> getBeans() {
        return container.getBeans();
    }

    public static Collection<?> getBeans(Class<?> parent) {
        return container.getBeans(parent);
    }
}

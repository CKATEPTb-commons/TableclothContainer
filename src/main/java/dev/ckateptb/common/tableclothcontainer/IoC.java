package dev.ckateptb.common.tableclothcontainer;

import dev.ckateptb.common.tableclothcontainer.container.Container;

import java.util.Collection;
import java.util.function.Predicate;

public class IoC {
    private static final Container container = new Container();

    public static void scan(Class<?> owner, String... packages) {
        container.scan(owner, packages);
    }

    public static void scan(Class<?> owner, Predicate<String> filter, String... packages) {
        container.scan(owner, filter, packages);
    }

    public static void scan(Class<?> owner, Predicate<String> filter, ClassLoader classLoader, String... packages) {
        container.scan(owner, filter, classLoader, packages);
    }

    public static void init() {
        container.init();
    }

    public static <T> void registerBean(T bean, Class<?> owner) {
        container.registerBean(bean, owner);
    }

    public static <T> boolean containsBean(Class<T> beanClass) {
        return container.containsBean(beanClass);
    }

    public static <T> T getBean(Class<T> beanClass) {
        return container.getBean(beanClass);
    }


    public static Collection<?> getBeans() {
        return container.getBeans();
    }

    public static Collection<?> getBeans(Class<?> parent) {
        return container.getBeans(parent);
    }
}

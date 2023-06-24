package dev.ckateptb.common.tableclothcontainer.container;


import com.google.common.reflect.ClassPath;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.common.tableclothcontainer.event.ComponentRegisterEvent;
import dev.ckateptb.common.tableclothcontainer.util.FinderUtil;
import dev.ckateptb.common.tableclothevent.EventBus;
import lombok.SneakyThrows;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.behaviors.Caching;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Predicate;

public class Container {
    private static boolean tableclothEventIsPresent = false;

    static {
        try {
            Class.forName("dev.ckateptb.common.tableclothevent.EventBus");
            tableclothEventIsPresent = true;
        } catch (ClassNotFoundException ignored) {
        }
    }

    private final DefaultPicoContainer container = new DefaultPicoContainer(new Caching());
    private final Map<Class<?>, Class<?>> ownerMap = new HashMap<>();

    public void scan(Class<?> owner, String... packages) {
        this.scan(owner, classInfoName -> true, packages);
    }

    public void scan(Class<?> owner, Predicate<String> filter, String... packages) {
        this.scan(owner, filter, null, packages);
    }

    @SneakyThrows
    public void scan(Class<?> owner, Predicate<String> filter, ClassLoader classLoader, String... packages) {
        if (packages.length == 0) packages = new String[]{owner.getPackageName()};
        for (String packageName : packages) {
            ClassPath.from(classLoader != null ? classLoader : owner.getClassLoader()).getTopLevelClassesRecursive(packageName).stream()
                    .filter(classInfo -> filter.test(classInfo.getName()))
                    .map(classInfo -> {
                        try {
                            return classInfo.load();
                        } catch (Exception ignored) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(clazz -> clazz.isAnnotationPresent(Component.class))
                    .peek(cl -> ownerMap.put(cl, owner))
                    .forEachOrdered(this.container::addComponent);
        }
    }

    public <T> void registerBean(T object, Class<?> owner) {
        this.container.addComponent(object);
        Class<?> objectClass = object.getClass();
        this.ownerMap.put(objectClass, owner);
        if (tableclothEventIsPresent) {
            ComponentRegisterEvent<?> event = new ComponentRegisterEvent(this, objectClass, owner, object);
            EventBus.GLOBAL.dispatchEvent(event);
        }
    }

    public void init() {
        for (Object object : this.container.getComponents()) {
            Class<?> objectClass = object.getClass();
            FinderUtil.findMethods(objectClass, PostConstruct.class).stream().findFirst().ifPresent(method -> {
                try {
                    method.invoke(object);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            if (tableclothEventIsPresent) {
                ComponentRegisterEvent<?> event = new ComponentRegisterEvent(this, objectClass, this.ownerMap.get(objectClass), object);
                EventBus.GLOBAL.dispatchEvent(event);
            }
        }
    }

    public <T> boolean containsBean(Class<T> beanClass) {
        try {
            this.container.getComponent(beanClass);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public <T> T getBean(Class<T> beanClass) {
        return this.container.getComponent(beanClass);
    }

    public Collection<?> getBeans() {
        return this.container.getComponents();
    }

    public Collection<?> getBeans(Class<?> owner) {
        HashSet<Object> beans = new HashSet<>();
        this.ownerMap.forEach((key, value) -> {
            if (value.equals(owner)) beans.add(key);
        });
        return beans;
    }
}


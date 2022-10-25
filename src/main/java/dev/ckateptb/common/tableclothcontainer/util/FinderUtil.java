package dev.ckateptb.common.tableclothcontainer.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class FinderUtil {
    public static <T> Constructor<T> findAnnotatedConstructor(Class<T> clazz, Class<? extends Annotation> annotationClass) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(annotationClass)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    public static Set<Field> findFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Set<Field> set = new HashSet<>();
        while (clazz != null) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    field.setAccessible(true);
                    set.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return set;
    }

    public static Set<Method> findMethods(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        Set<Method> set = new HashSet<>();
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(annotationClass)) {
                    method.setAccessible(true);
                    set.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return set;
    }
}

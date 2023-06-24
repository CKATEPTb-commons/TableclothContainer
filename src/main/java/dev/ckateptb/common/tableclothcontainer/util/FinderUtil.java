package dev.ckateptb.common.tableclothcontainer.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

public class FinderUtil {
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

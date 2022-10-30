package dev.ckateptb.common.tableclothcontainer.container;

import com.google.common.reflect.ClassPath;
import dev.ckateptb.common.tableclothcontainer.annotation.Autowired;
import dev.ckateptb.common.tableclothcontainer.annotation.Component;
import dev.ckateptb.common.tableclothcontainer.annotation.PostConstruct;
import dev.ckateptb.common.tableclothcontainer.annotation.Qualifier;
import dev.ckateptb.common.tableclothcontainer.event.ComponentRegisterEvent;
import dev.ckateptb.common.tableclothcontainer.exception.ComponentNotFoundException;
import dev.ckateptb.common.tableclothcontainer.exception.CircularException;
import dev.ckateptb.common.tableclothcontainer.exception.ComponentConstructorNotFoundException;
import dev.ckateptb.common.tableclothcontainer.util.FinderUtil;
import dev.ckateptb.common.tableclothevent.EventBus;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Container {
    private static boolean tableclothEventIsPresent = false;
    static {
        try {
            Class.forName("dev.ckateptb.common.tableclothevent.EventBus");
            tableclothEventIsPresent = true;
        } catch (ClassNotFoundException ignored) {
        }
    }
    private final Map<Class<?>, Set<BeanComponent<?>>> beans = new HashMap<>();
    private final Map<Class<?>, Set<CircularPreventTree<Class<?>>>> circularPreventTree = new HashMap<>();

    public void scan(Class<?> parentClass, String... packages) {
        this.scan(parentClass, classInfoName -> true, packages);
    }

    public void scan(Class<?> parentClass, Predicate<String> filter, String... packages) {
        this.scan(parentClass, classInfoName -> true, null, packages);
    }

    @SneakyThrows
    public void scan(Class<?> parentClass, Predicate<String> filter, ClassLoader classLoader, String... packages) {
        packages = Optional.ofNullable(packages).orElse(new String[0]);
        if (packages.length == 0) {
            packages = new String[]{parentClass.getPackageName()};
        }
        if (classLoader == null) {
            classLoader = parentClass.getClassLoader();
        }
        for (String packageName : packages) {
            ClassPath.from(classLoader).getTopLevelClassesRecursive(packageName).stream()
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
                    .forEach(clazz -> this.declareNewInstanceGenerator(clazz, null, parentClass));
        }
    }

    public void init() {
        circularPreventTree.values().forEach(circularPreventTrees -> circularPreventTrees.forEach(tree -> {
            try {
                tree.newInstance();
            } catch (CircularException circularException) {
                circularException.addSuppressed(new CircularException(tree.getCurrent().toString()));
                circularException.printStackTrace();
            }
        }));
    }

    public <T> BeanComponent<T> registerBean(T bean) {
        return this.registerBean(bean, "");
    }

    public <T> BeanComponent<T> registerBean(T instance, String identifier) {
        Class<T> instanceClass = (Class<T>) instance.getClass();
        BeanComponent<T> beanComponent = new BeanComponent<>(identifier, instanceClass, instance);
        beanComponent.setParent(instanceClass);
        this.beans.computeIfAbsent(instanceClass, (key) -> new HashSet<>()).add(beanComponent);
        if(tableclothEventIsPresent) {
            ComponentRegisterEvent<T> event = new ComponentRegisterEvent<>(this, instanceClass, identifier, instance);
            EventBus.GLOBAL.dispatchEvent(event);
        }
        return beanComponent;
    }

    public <T> T getBean(Class<T> beanClass) {
        return this.getBean(beanClass, "");
    }

    public <T> T getBean(Class<T> beanClass, String identifier) {
        try {
            return this.getBeanWrappers(beanClass)
                    .filter(beanComponent -> beanComponent.getIdentifier().equals(identifier))
                    .findFirst().map(BeanComponent::getInstance).orElseThrow();
        } catch (NullPointerException | NoSuchElementException exception) {
            throw new ComponentNotFoundException(String.format("Bean %s (%s) is missing", beanClass, identifier));
        }
    }

    public <T> boolean containsBean(Class<T> beanClass) {
        return this.containsBean(beanClass, "");
    }

    public <T> boolean containsBean(Class<T> beanClass, String identifier) {
        return this.getBeanWrappers(beanClass).anyMatch(beanComponent -> beanComponent.getIdentifier().equals(identifier));
    }

    public Collection<?> getBeans(Class<?> parent) {
        return this.getBeanWrappers()
                .filter(beanComponent -> beanComponent.getParent() == parent)
                .map(BeanComponent::getInstance)
                .collect(Collectors.toList());
    }

    public Collection<?> getBeans() {
        return this.getBeanWrappers().map(BeanComponent::getInstance).toList();
    }

    private Stream<BeanComponent<?>> getBeanWrappers() {
        Set<BeanComponent<?>> beanComponentSet = new HashSet<>();
        this.beans.values().forEach(beanComponentSet::addAll);
        return beanComponentSet.stream();
    }

    private <T> Stream<BeanComponent<T>> getBeanWrappers(Class<T> beanClass) {
        if (this.beans.containsKey(beanClass)) {
            Set<BeanComponent<?>> beanComponentSet = this.beans.get(beanClass);
            if (!beanComponentSet.isEmpty()) {
                return beanComponentSet.stream().map(beanComponent -> (BeanComponent<T>) beanComponent);
            }
            this.beans.remove(beanClass);
        }
        return Stream.empty();
    }

    private <T> Constructor<?> getDefaultConstructor(Class<T> clazz) {
        Constructor<T> defaultConstructor = FinderUtil.findAnnotatedConstructor(clazz, Autowired.class);
        if (defaultConstructor == null) {
            try {
                defaultConstructor = (Constructor<T>) clazz.getConstructors()[0];
            } catch (Throwable throwable) {
                try {
                    defaultConstructor = clazz.getConstructor();
                } catch (NoSuchMethodException e) {
                    throw new ComponentConstructorNotFoundException("There is no default constructor in class " + clazz.getName());
                }
            }
        }
        defaultConstructor.setAccessible(true);
        return defaultConstructor;
    }

    private void declareNewInstanceGenerator(Class<?> clazz, String identifier, Class<?> parentClass) {
        Component component = clazz.getAnnotation(Component.class);
        identifier = Optional.ofNullable(identifier).orElse(component.value());
        if (this.containsBean(clazz, identifier)) return;
        CircularPreventTree<Class<?>> tree = new CircularPreventTree<>(parentClass, clazz, identifier);
        String finalIdentifier = identifier;
        tree.setNewInstanceGenerator(self -> {
            if (this.containsBean(clazz, finalIdentifier)) return this.getBean(clazz, finalIdentifier);
            Set<CircularPreventTree<?>> childCircularPreventTree = self.getChildCircularPreventTree();
            Constructor<?> defaultConstructor = this.getDefaultConstructor(clazz);
            Parameter[] parameters = defaultConstructor.getParameters();
            Class<?>[] parameterTypes = defaultConstructor.getParameterTypes();
            for (int i = 0; i < parameters.length; i++) {
                Parameter parameter = parameters[i];
                String qualifier = parameter.isAnnotationPresent(Qualifier.class) ?
                        parameter.getAnnotation(Qualifier.class).value() : "";
                Class<?> parameterType = parameterTypes[i];
                CircularPreventTree<? extends Class<?>> tree1 = new CircularPreventTree<>(clazz, parameterType, qualifier);
                if (childCircularPreventTree.add(tree1)) {
                    this.declareNewInstanceGenerator(parameterType, qualifier, clazz);
                    childCircularPreventTree.remove(tree1);
                    childCircularPreventTree.add(this.circularPreventTree.get(parameterType)
                            .stream().filter(classCircularPreventTree ->
                                    classCircularPreventTree.getCurrent().equals(parameterType)
                                            && classCircularPreventTree.getIdentifier().equals(qualifier)).findFirst().orElseThrow());
                }
            }
            self.validateNotCircular();
            Object instance = defaultConstructor.newInstance(childCircularPreventTree.stream()
                    .map(tree1 -> {
                        Class<?> current = (Class<?>) tree1.getCurrent();
                        String identifier1 = tree1.getIdentifier();
                        if (this.containsBean(current, identifier1)) {
                            return getBean(current, identifier1);
                        } else {
                            return tree1.newInstance();
                        }
                    })
                    .toArray(Object[]::new));
            FinderUtil.findMethods(clazz, PostConstruct.class).stream().findFirst().ifPresent(method -> {
                try {
                    method.invoke(instance);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
            this.registerBean(instance, finalIdentifier);
            return instance;
        });
        circularPreventTree.computeIfAbsent(clazz, key -> new LinkedHashSet<>()).add(tree);
    }
}

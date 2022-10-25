package dev.ckateptb.common.tableclothcontainer.container;

import dev.ckateptb.common.tableclothcontainer.exception.CircularException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public class CircularPreventTree<T> {
    private final Class<?> parent;
    private final T current;
    private final String identifier;
    private final Set<CircularPreventTree<?>> childCircularPreventTree = new LinkedHashSet<>();
    private NewInstanceGenerator newInstanceGenerator;

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof CircularPreventTree<?> otherCircularPreventTree)) return false;
        return current.equals(otherCircularPreventTree.current) && identifier.equals(otherCircularPreventTree.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(current.hashCode(), identifier);
    }

    public boolean isNotContains(CircularPreventTree<?> other) {
        return !childCircularPreventTree.contains(other) && childCircularPreventTree.stream().allMatch(otherCircularPreventTree -> otherCircularPreventTree.isNotContains(other));
    }

    public void validateNotCircular() throws CircularException {
        if (!isNotContains(this))
            throw new CircularException(String.format("Dependencies of some components cause a circle: %s", current));
    }

    public void setNewInstanceGenerator(NewInstanceGenerator newInstanceGenerator) {
        this.newInstanceGenerator = newInstanceGenerator;
    }

    @SneakyThrows
    public Object newInstance() {
        return this.newInstanceGenerator.newInstance(this);
    }

    public interface NewInstanceGenerator {
        Object newInstance(CircularPreventTree<?> tree) throws InvocationTargetException, InstantiationException, IllegalAccessException;
    }
}

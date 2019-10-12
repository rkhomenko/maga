package org.khomenko.maga.stream;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IrisDataSetHelper {
    private Collection<Iris> dataSet;

    public IrisDataSetHelper(Collection<Iris> dataSet) {
        this.dataSet = dataSet;
    }

    public Double getAverage(ToDoubleFunction<Iris> func) {
        return getAverage(dataSet.stream(), func);
    }

    public Collection<Iris> filter(Predicate<Iris> predicate) {
        return filter(dataSet.stream(), predicate).collect(Collectors.toList());
    }

    public Double getAverageWithFilter(Predicate<Iris> filter, ToDoubleFunction<Iris> func) {
        var filtered = filter(dataSet.stream(), filter);
        return getAverage(filtered, func);
    }

    public <C> Map<C, List<Iris>> groupBy(Function<Iris, C> groupFunction) {
        return groupBy(dataSet.stream(), groupFunction);
    }

    public <C> Map<C, Optional<Iris>> maxFromGroupedBy(Function<Iris, C> groupFunction, ToDoubleFunction<Iris> func) {
        return dataSet.stream().collect(
                Collectors.groupingBy(groupFunction,
                        Collectors.maxBy(Comparator.comparingDouble(func)))
        );
    }

    private Double getAverage(Stream<Iris> stream, ToDoubleFunction<Iris> func) {
        return stream.mapToDouble(func).average().orElse(0);
    }

    private Stream<Iris> filter(Stream<Iris> stream, Predicate<Iris> predicate) {
        return stream.filter(predicate);
    }

    private <C> Map<C, List<Iris>> groupBy(Stream<Iris> stream, Function<Iris, C> groupFunction) {
        return stream.collect(Collectors.groupingBy(groupFunction));
    }
}

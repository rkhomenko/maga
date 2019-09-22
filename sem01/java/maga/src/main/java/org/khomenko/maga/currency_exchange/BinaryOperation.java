package org.khomenko.maga.currency_exchange;

@FunctionalInterface
public interface BinaryOperation<T> {
    T apply(T obj);
}

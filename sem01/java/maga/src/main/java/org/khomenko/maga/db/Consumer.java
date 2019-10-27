package org.khomenko.maga.db;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T book);
}

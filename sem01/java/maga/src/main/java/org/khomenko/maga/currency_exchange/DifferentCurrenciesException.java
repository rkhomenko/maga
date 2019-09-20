package org.khomenko.maga.currency_exchange;

public class DifferentCurrenciesException extends RuntimeException {
    public DifferentCurrenciesException(String message) {
        super(message);
    }
}
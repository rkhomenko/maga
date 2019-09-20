package org.khomenko.maga.currency_exchange;

public class IncorrectExchangeRateException extends RuntimeException {
    public IncorrectExchangeRateException(String message) {
        super(message);
    }
}

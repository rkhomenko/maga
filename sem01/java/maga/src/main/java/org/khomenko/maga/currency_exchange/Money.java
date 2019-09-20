package org.khomenko.maga.currency_exchange;

import java.math.BigDecimal;
import java.util.Currency;

public class Money {
    private Currency currency;
    private BigDecimal amount;

    public Money(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount.setScale(this.currency.getDefaultFractionDigits());
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money m) {
        throw new UnsupportedOperationException();
    }

    public Money subtract(Money m) {
        throw new UnsupportedOperationException();
    }

    public Money multiply(BigDecimal ratio) {
        throw new UnsupportedOperationException();
    }

    public Money devide(BigDecimal ratio) {
        throw new UnsupportedOperationException();
    }
}


package org.khomenko.maga.currency_exchange;

import java.math.BigDecimal;
import java.util.Currency;

public class CurrencyExchangeRate {
    private Currency fromCurrency;
    private Currency toCurrency;
    private BigDecimal rate;

    public CurrencyExchangeRate(double rate, Currency from, Currency to) {
        this(new BigDecimal(rate), from, to);
    }

    public CurrencyExchangeRate(BigDecimal rate, Currency from, Currency to) {
        this.rate = rate;
        this.fromCurrency = from;
        this.toCurrency = to;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public Money convert(Money m) {
        if (m.getCurrency() != getFromCurrency()) {
            throw new IncorrectExchangeRateException("incorrect exchange rate");
        }

        return new Money(getToCurrency(), m.multiply(getRate()).getAmount());
    }
}

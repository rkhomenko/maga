package org.khomenko.maga.currency_exchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

public class Money {
    private Currency currency;
    private BigDecimal amount;

    public Money(Currency currency, int amount) {
        this(currency, new BigDecimal(amount));
    }

    public Money(Currency currency, BigDecimal amount) {
        this.currency = currency;
        this.amount = amount.setScale(
                this.currency.getDefaultFractionDigits(),
                RoundingMode.FLOOR);
    }

    public Currency getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Money add(Money m) {
        return applyBinaryOperation(getAmount()::add, m);
    }

    public Money subtract(Money m) {
        return applyBinaryOperation(getAmount()::subtract, m);
    }

    public Money multiply(BigDecimal ratio) {
        return applyBinaryOperation(getAmount()::multiply, ratio);
    }

    public Money divide(BigDecimal ratio) {
        return applyBinaryOperation(getAmount()::divide, ratio);
    }

    public boolean isEqual(Money m) {
        return getCurrency() == m.getCurrency() && getAmount().compareTo(m.getAmount()) == 0;
    }

    private Money applyBinaryOperation(BinaryOperation<BigDecimal> op, Money m) {
        return applyBinaryOperation(op, m.getAmount(), m.getCurrency());
    }

    private Money applyBinaryOperation(BinaryOperation<BigDecimal> op, BigDecimal ratio) {
        return applyBinaryOperation(op, ratio, getCurrency());
    }

    private Money applyBinaryOperation(BinaryOperation<BigDecimal> op, BigDecimal number, Currency currency) {
        if (getCurrency() != currency) {
            throw new DifferentCurrenciesException("different currencies");
        }

        return new Money(getCurrency(), op.apply(number));
    }
}

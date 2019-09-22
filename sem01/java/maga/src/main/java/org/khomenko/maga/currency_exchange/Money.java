package org.khomenko.maga.currency_exchange;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

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

    public List<Money> divide(int numParts) {
        var result = new ArrayList<Money>();
        var part = getAmount().divide(new BigDecimal(numParts), RoundingMode.FLOOR);

        var sum = BigDecimal.ZERO;
        for (int i = 0; i < numParts - 1; i++) {
            result.add(new Money(getCurrency(), part));
            sum = sum.add(part);
        }

        var r = getAmount().subtract(sum);
        if (r.compareTo(BigDecimal.ZERO) > 0) {
            result.add(new Money(getCurrency(), r));
        }
        else if (r.compareTo(BigDecimal.ZERO) == 0) {
            result.add(new Money(getCurrency(), part));
        }

        return result;
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

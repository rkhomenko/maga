package org.khomenko.maga.currency_exchange;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

class CurrencyExchangeTest {
    private static Currency USD = Currency.getInstance("USD");
    private static Currency GBP = Currency.getInstance("GBP");
    private static Money UsdMoney = new Money(USD, new BigDecimal(100));
    private static Money GbpMoney = new Money(USD, new BigDecimal(100));

    @FunctionalInterface
    private interface BinaryOperation {
        Money apply(Money m);
    }

    private Money BinaryOperationApply(BinaryOperation op, Money m) {
        return op.apply(m);
    }

    private void BinaryOperationFailTest(BinaryOperation op, Money m) {
        Assertions.assertThrows(DifferentCurrenciesException.class, () -> {
            var money = BinaryOperationApply(op, m);
        });
    }

    @Test
    void IncompatibleCurrencyAddTest() {
        BinaryOperationFailTest(UsdMoney::add, GbpMoney);
    }

    @Test
    void IncompatibleCurrencySubtractTest() {
        BinaryOperationFailTest(UsdMoney::subtract, GbpMoney);
    }

    @Test
    void Test() {
//        Currency usd = Currency.getInstance("USD");
//        Currency gbp = Currency.getInstance("GBP");
//
//        Money usdMoney = new Money(usd, new BigDecimal(100));
//        Money tenDollars = new Money(usd, new BigDecimal(10));
//        Money tenPound = new Money(gbp, new BigDecimal(10));
//        CurrencyExchangeRate poundToUsd = new CurrencyExchangeRate(new BigDecimal(1.5), gbp, usd);
//
//        //should set usdMoney 110 with scale 2
//        usdMoney = usdMoney.add(tenDollars);
//        System.out.println(usdMoney.getAmount().equals(new BigDecimal(110).setScale(2)));
//
//        //should throw DifferentCurrenciesException
//        try {
//            usdMoney = usdMoney.subtract(tenPound);
//        } catch(DifferentCurrenciesException ex) {
//            System.out.println("DifferentCurrenciesException thrown");
//        }
//
//        //should set usdMoney 95 with scale 2
//        usdMoney = usdMoney.subtract(poundToUsd.convert(tenPound));
//        System.out.println(usdMoney.getAmount().equals(new BigDecimal(95).setScale(2)));
    }
}
package org.khomenko.maga.currency_exchange;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Currency;

class CurrencyExchangeTest {
    private static final Currency USD = Currency.getInstance("USD");
    private static final Currency GBP = Currency.getInstance("GBP");
    private static final Money usdMoney = new Money(USD, 100);
    private static final Money gbpMoney = new Money(GBP, 100);


    private void binaryOperationFailTest(BinaryOperation<Money> op, Money m) {
        Assertions.assertThrows(DifferentCurrenciesException.class, () -> {
            op.apply(m);
        });
    }

    private void binaryOperationTest(BinaryOperation<Money> op, Money m, Money expected) {
        var result = op.apply(m);
        Assertions.assertTrue(result.isEqual(expected));
    }

    @Test
    void moneyIsEqualTest() {
        var m1 = new Money(USD, 10);
        var m2 = new Money(USD, 10);

        Assertions.assertTrue(m1.isEqual(m2));
    }

    @Test
    void moneyAddTest() {
        var m1 = new Money(USD, 100);
        var m2 = new Money(USD, 10);
        var expected = new Money(USD, 110);

        binaryOperationTest(m1::add, m2, expected);
    }

    @Test
    void moneySubtractTest() {
        var m1 = new Money(USD, 100);
        var m2 = new Money(USD, 10);
        var expected = new Money(USD, 90);

        binaryOperationTest(m1::subtract, m2, expected);
    }

    @Test
    void moneyMultiplyTest() {
        var tenDollars = new Money(USD, 10);
        var hundredDollars = new Money(USD, 100);

        Assertions.assertTrue(tenDollars.multiply(tenDollars.getAmount()).isEqual(hundredDollars));
    }

    @Test
    void moneyDivideTest() {
        var tenDollars = new Money(USD, 10);
        var hundredDollars = new Money(USD, 100);

        Assertions.assertTrue(hundredDollars.divide(tenDollars.getAmount()).isEqual(tenDollars));
    }

    @Test
    void incompatibleMoneyAddTest() {
        binaryOperationFailTest(usdMoney::add, gbpMoney);
    }

    @Test
    void incompatibleMoneySubtractTest() {
        binaryOperationFailTest(usdMoney::subtract, gbpMoney);
    }

    @Test
    void test() {
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
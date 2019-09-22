package org.khomenko.maga.currency_exchange;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Currency;

class MoneyTest {
    public static final Currency USD = Currency.getInstance("USD");
    public static final Currency GBP = Currency.getInstance("GBP");
    public static final Money usdMoney = new Money(USD, 100);
    public static final Money gbpMoney = new Money(GBP, 100);


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

    @ParameterizedTest
    @ValueSource(ints = {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
            100, 129, 257, 513, 777, 1000, 1500, 10000})
    void moneyDivideTest(int numParts) {
        var hundredDollars = new Money(USD, 100);
        var result = hundredDollars.divide(numParts);

        var sum = new BigDecimal(0);
        for (Money part: result) {
            sum = sum.add(part.getAmount());
        }

        Assertions.assertEquals(numParts, result.size());
        Assertions.assertEquals(hundredDollars.getAmount(), sum);
    }

    @Test
    void incompatibleMoneyAddTest() {
        binaryOperationFailTest(usdMoney::add, gbpMoney);
    }

    @Test
    void incompatibleMoneySubtractTest() {
        binaryOperationFailTest(usdMoney::subtract, gbpMoney);
    }
}
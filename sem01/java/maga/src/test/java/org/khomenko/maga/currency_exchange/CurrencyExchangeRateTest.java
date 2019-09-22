package org.khomenko.maga.currency_exchange;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CurrencyExchangeRateTest {
    private static final CurrencyExchangeRate usdToGbp =
            new CurrencyExchangeRate(1.5, MoneyTest.USD, MoneyTest.GBP);

    @Test
    void incompatibleCurrencyTest()
    {
        Assertions.assertThrows(IncorrectExchangeRateException.class,
                () -> usdToGbp.convert(MoneyTest.gbpMoney));
    }

    @Test
    void convertTest() {
        var money = new Money(MoneyTest.USD, 100);
        var expected = new Money(MoneyTest.GBP, 150);
        var result = usdToGbp.convert(money);

        Assertions.assertTrue(result.isEqual(expected));
    }
}

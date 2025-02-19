package ru.fastdelivery.usecase;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.price.VolumeParameter;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TariffCalculateUseCaseTest {

    private final WeightPriceProvider weightPriceProvider = mock(WeightPriceProvider.class);
    private final Currency currency = new CurrencyFactory(code -> true).create("RUB");
    private final TariffCalculateUseCase tariffCalculateUseCase = new TariffCalculateUseCase(weightPriceProvider);

    @Test
    @DisplayName("Расчет стоимости доставки -> успешно")
    public void whenCalculatePrice_thenSuccess() {
        when(weightPriceProvider.costPerKg()).thenReturn(new Price(BigDecimal.valueOf(400.0), currency));
        when(weightPriceProvider.minimalPrice()).thenReturn(new Price(BigDecimal.valueOf(50.0), currency));

        BigDecimal deliveryCostAmount = BigDecimal.valueOf(105534.0);
        Shipment shipment = new Shipment(
                List.of(new Pack(new Weight(BigInteger.valueOf(100)))),
                currency,
                List.of(new VolumeParameter(1200, 13000, 1500)),
                55.755826,
                37.6173,
                59.9342802,
                30.3350986,
                10.0
        );

        Price result = tariffCalculateUseCase.calc(shipment);
        assertEquals(deliveryCostAmount, result.amount());
        assertEquals(currency, result.currency());
    }

    @Test
    @DisplayName("Получение минимальной стоимости -> успешно")
    public void whenMinimalPrice_thenSuccess() {
        BigDecimal minimalValue = BigDecimal.TEN;
        var minimalPrice = new Price(minimalValue, currency);
        when(weightPriceProvider.minimalPrice()).thenReturn(minimalPrice);
        var actual = tariffCalculateUseCase.minimalPrice();
        assertThat(actual).isEqualTo(minimalPrice);
    }
}
package ru.fastdelivery.domain.delivery.shipment;

import ru.fastdelivery.domain.common.currency.Currency;
import ru.fastdelivery.domain.common.price.VolumeParameter;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;

import java.util.List;

/**
 * @param packages упаковки в грузе
 * @param currency валюта объявленная для груза
 */
public record Shipment(
        List<Pack> packages,
        Currency currency,
        List<VolumeParameter> volumeParameters,
        double departureLatitude,
        double departureLongitude,
        double destinationLatitude,
        double destinationLongitude,
        double costCubicMeter
) {
    public Shipment(List<Pack> packages, Currency currency) {
        this(packages, currency, List.of(), 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public Weight weightAllPackages() {
        return packages.stream()
                .map(Pack::weight)
                .reduce(Weight.zero(), Weight::add);
    }
}
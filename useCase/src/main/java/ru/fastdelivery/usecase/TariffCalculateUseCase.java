package ru.fastdelivery.usecase;

import ru.fastdelivery.domain.common.price.Price;
import ru.fastdelivery.domain.common.price.VolumeParameter;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.List;

@Named
public class TariffCalculateUseCase {
    private static final double EARTH_RADIUS = 6371;
    private static final int SEGMENT_LENGTH = 450;

    private final WeightPriceProvider weightPriceProvider;

    public TariffCalculateUseCase(WeightPriceProvider weightPriceProvider) {
        this.weightPriceProvider = weightPriceProvider;
    }

    public Price calc(Shipment shipment) {
        Price volumeCost = calculateVolumeCost(shipment);
        Price weightCost = calculateWeightCost(shipment);
        Price baseCost = weightCost.max(volumeCost);
        double deliveryCost = calculateDeliveryCost(
                shipment.departureLatitude(),
                shipment.departureLongitude(),
                shipment.destinationLatitude(),
                shipment.destinationLongitude(),
                baseCost.amount().doubleValue()
        );
        return new Price(BigDecimal.valueOf(deliveryCost), baseCost.currency());
    }

    private Price calculateWeightCost(Shipment shipment) {
        var weightAllPackagesKg = shipment.weightAllPackages().kilograms();
        var minimalPrice = weightPriceProvider.minimalPrice();
        return weightPriceProvider
                .costPerKg()
                .multiply(weightAllPackagesKg)
                .max(minimalPrice);
    }

    private Price calculateVolumeCost(Shipment shipment) {
        double totalVolumeInCubicMeters = calculateTotalVolume(shipment.volumeParameters());
        return new Price(BigDecimal.valueOf(totalVolumeInCubicMeters * shipment.costCubicMeter()), shipment.currency());
    }

    private double calculateTotalVolume(List<VolumeParameter> packages) {
        return packages.stream()
                .mapToDouble(this::calculatePackageVolume)
                .sum();
    }

    private double calculatePackageVolume(VolumeParameter pack) {
        int length = roundToNearestMultipleOfFifty(pack.length());
        int width = roundToNearestMultipleOfFifty(pack.width());
        int height = roundToNearestMultipleOfFifty(pack.height());
        return ((double) length / 1000) * ((double) width / 1000) * ((double) height / 1000);
    }

    private int roundToNearestMultipleOfFifty(int value) {
        return (int) Math.round((value / 50d)) * 50;
    }

    private double calculateDistance(double departureLatitude, double departureLongitude, double destinationLatitude, double destinationLongitude) {
        double dLat = Math.toRadians(destinationLatitude - departureLatitude);
        double dLon = Math.toRadians(destinationLongitude - departureLongitude);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(departureLatitude)) * Math.cos(Math.toRadians(destinationLatitude))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }

    private double calculateDeliveryCost(double departureLatitude, double departureLongitude, double destinationLatitude, double destinationLongitude, double baseCost) {
        double distance = calculateDistance(departureLatitude, departureLongitude, destinationLatitude, destinationLongitude);
        int fullSegments = (int) (distance / SEGMENT_LENGTH);
        double remainingDistance = distance % SEGMENT_LENGTH;
        double deliveryCost = fullSegments * baseCost + Math.max(remainingDistance, SEGMENT_LENGTH) * baseCost;
        return Math.ceil(deliveryCost * 100) / 100;
    }

    public Price minimalPrice() {
        return weightPriceProvider.minimalPrice();
    }
}
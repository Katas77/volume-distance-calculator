package ru.fastdelivery.domain.common.price;


import java.util.List;

public class CalculateVolume {

    public double calculateDeliveryCost(List<Packages> packages, double costPerCubicMeter) {
        double totalVolumeInCubicMeters = calculateTotalVolume(packages);
        return  totalVolumeInCubicMeters * costPerCubicMeter;
    }

    private double calculateTotalVolume(List<Packages> packages) {
        return packages.stream()
                .mapToDouble(this::calculatePackageVolume)
                .sum();//Рассчитывает общий объём всех упаковок в кубических метрах.
    }

    /**
     * Рассчитывает объём одной упаковки в кубических метрах.
     */
    private double calculatePackageVolume(Packages pack) {
        int length = roundToNearestMultipleOfFifty(pack.getLength());
        int width = roundToNearestMultipleOfFifty(pack.getWidth());
        int height = roundToNearestMultipleOfFifty(pack.getHeight());
        return ((double) length / 1000) * ((double) width / 1000) * ((double) height / 1000);
    }

    /**
     * Округляет значение до ближайшего числа, кратного 50.
     */
    private int roundToNearestMultipleOfFifty(int value) {
        return (int) Math.round((value / 50d)) * 50;
    }
}
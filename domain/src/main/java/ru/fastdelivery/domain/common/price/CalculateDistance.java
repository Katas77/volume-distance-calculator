package ru.fastdelivery.domain.common.price;

public class CalculateDistance {

    private static final double EARTH_RADIUS = 6371; // Радиус Земли в километрах
    private static final int SEGMENT_LENGTH = 450; // Длина одного сегмента в километрах

    public double calculateDistance(double lat1,double lon1,double lat2,double lon2) {
        // Формула Харверсина для вычисления расстояния между двумя точками на сфере
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
    public double calculateDeliveryCost(double lat1,double lon1,double lat2,double lon2,double base_cost) {
        double distance = calculateDistance(lat1, lon1,lat2, lon2);

        int fullSegments = (int)(distance / SEGMENT_LENGTH); // Количество полных сегментов
        double remainingDistance = distance % SEGMENT_LENGTH; // Остаток после деления на сегмент

        double baseCost = fullSegments * base_cost + Math.max(remainingDistance, SEGMENT_LENGTH) * base_cost;

        return Math.ceil(baseCost * 100) / 100; // Округляем до ближайшей копейки вверх
    }

}

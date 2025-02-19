package ru.fastdelivery.presentation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ConfigLoader {
    @Value("${delivery-cost-config.departure.latitude}")
    private double departureLatitude;
    @Value("${delivery-cost-config.departure.longitude}")
    private double departureLongitude;
    @Value("${delivery-cost-config.destination.latitude}")
    private double destinationLatitude;
    @Value("${delivery-cost-config.destination.longitude}")
    private double destinationLongitude;
    @Value("${delivery-cost-config.cubic-meter}")
    private double costCubicMeter;
}
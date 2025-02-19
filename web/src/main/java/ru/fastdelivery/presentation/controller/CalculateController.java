package ru.fastdelivery.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.fastdelivery.domain.common.currency.CurrencyFactory;
import ru.fastdelivery.domain.common.weight.Weight;
import ru.fastdelivery.domain.delivery.pack.Pack;
import ru.fastdelivery.domain.delivery.shipment.Shipment;
import ru.fastdelivery.presentation.api.request.CalculatePackagesRequest;
import ru.fastdelivery.presentation.api.request.CargoPackage;
import ru.fastdelivery.presentation.api.response.CalculatePackagesResponse;
import ru.fastdelivery.domain.common.price.VolumeParameter;
import ru.fastdelivery.presentation.config.ConfigLoader;
import ru.fastdelivery.usecase.TariffCalculateUseCase;
import java.util.List;

@RestController
@RequestMapping("/api/v1/calculate/")
@RequiredArgsConstructor
@Tag(name = "Расчеты стоимости доставки")
public class CalculateController {
    private final TariffCalculateUseCase tariffCalculateUseCase;
    private final CurrencyFactory currencyFactory;
    private final ConfigLoader loader;

    @PostMapping
    @Operation(summary = "Расчет стоимости по упаковкам груза")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Invalid input provided")
    })
    public CalculatePackagesResponse calculate(
            @Valid @RequestBody CalculatePackagesRequest request) {
        request.packages().forEach(cargoPackage -> cargoPackage.validateParameters(cargoPackage));
        var packsWeights = request.packages().stream()
                .map(CargoPackage::weight)
                .map(Weight::new)
                .map(Pack::new)
                .toList();
        List<VolumeParameter> packagesList = request.packages().stream()
                .map(cp -> new VolumeParameter(cp.length(), cp.width(), cp.height()))
                .toList();
        Shipment shipment = new Shipment(packsWeights, currencyFactory.create(request.currencyCode()), packagesList,
                loader.getDepartureLatitude(), loader.getDepartureLongitude(), loader.getDestinationLatitude(),
                loader.getDestinationLongitude(), loader.getCostCubicMeter());
        var calculatedPrice = tariffCalculateUseCase.calc(shipment);
        var minimalPrice = tariffCalculateUseCase.minimalPrice();
        return new CalculatePackagesResponse(calculatedPrice, minimalPrice);
    }
}
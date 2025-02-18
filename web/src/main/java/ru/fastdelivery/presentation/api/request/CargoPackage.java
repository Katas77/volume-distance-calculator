package ru.fastdelivery.presentation.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import ru.fastdelivery.presentation.exception.InvalidDimensionsException;


import java.math.BigInteger;
@Slf4j
public record CargoPackage(
        @Schema(description = "Вес упаковки, граммы", example = "5667.45")
        BigInteger weight,

        @Schema(description = "Длина упаковки, миллиметры", example = "1000")
        int length,

        @Schema(description = "Ширина упаковки, миллиметры", example = "800")
        int width,

        @Schema(description = "Высота упаковки, миллиметры", example = "600")
        int height
) {

        public CargoPackage(BigInteger weight, int length, int width, int height) {
                this.weight = weight;
                this.length = length;
                this.width = width;
                this.height = height;
        }


        public void validateParameters(CargoPackage cargoPackage) {
                if (cargoPackage.weight == null ||cargoPackage.length <= 0 || cargoPackage.width <= 0 || cargoPackage.height <= 0) {
                       log.info("Weight cannot be null, and dimensions must be greater than zero.");
                        throw new InvalidDimensionsException("Weight cannot be null, and dimensions must be greater than zero.");
                }
                if (cargoPackage.length > 1500 ||cargoPackage.width > 1500 || cargoPackage.height > 1500) {
                        log.info("One or more dimensions exceed the maximum allowed value of 1500 mm.");
                        throw new InvalidDimensionsException("One or more dimensions exceed the maximum allowed value of 1500 mm.");
                }
        }


}
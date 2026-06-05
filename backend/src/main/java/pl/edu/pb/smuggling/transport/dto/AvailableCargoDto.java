package pl.edu.pb.smuggling.transport.dto;

import java.math.BigDecimal;

public record AvailableCargoDto(
        Integer cargoId,
        Integer orderId,
        String name,
        String cargoType,
        Integer packagesCount,
        BigDecimal estimatedValue,
        Integer warehouseId,
        String warehouseName
) {
}

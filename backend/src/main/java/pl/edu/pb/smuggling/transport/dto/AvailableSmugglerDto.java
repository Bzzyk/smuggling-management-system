package pl.edu.pb.smuggling.transport.dto;

import java.math.BigDecimal;

public record AvailableSmugglerDto(
        Integer smugglerId,
        String firstName,
        String lastName,
        String username,
        String experienceLevel,
        Integer completedTransportsCount,
        Integer failedTransportsCount,
        BigDecimal successRatePercent
) {
}

package pl.edu.pb.smuggling.transport.dto;

public record AvailableVehicleDto(
        Integer vehicleId,
        String registrationNumber,
        String brand,
        String model,
        String vehicleType,
        Integer loadCapacity
) {
}

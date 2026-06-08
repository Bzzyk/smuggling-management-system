package pl.edu.pb.smuggling.transport.dto.rest;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.Vehicle;
import pl.edu.pb.smuggling.transport.model.VehicleType;

@Data
@Builder
public class VehicleDto {
    private Integer id;
    private String registrationNumber;
    private String brand;
    private String model;
    private VehicleType vehicleType;
    private Integer loadCapacity;
    private Boolean available;
    private Boolean active;

    public static VehicleDto fromEntity(Vehicle vehicle) {
        return VehicleDto.builder()
                .id(vehicle.getId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .vehicleType(vehicle.getVehicleType())
                .loadCapacity(vehicle.getLoadCapacity())
                .available(vehicle.getAvailable())
                .active(vehicle.getActive())
                .build();
    }
}

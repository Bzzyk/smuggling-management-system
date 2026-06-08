package pl.edu.pb.smuggling.transport.dto.rest;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.transport.model.Transport;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransportDto {
    private Integer id;
    private Integer orderId;
    private String orderTitle;
    private Integer routeId;
    private String routeName;
    private Integer vehicleId;
    private String vehicleRegistrationNumber;
    private String vehicleName;
    private String statusName;
    private String startLocation;
    private String destination;
    private LocalDate transportDate;
    private LocalDate plannedArrivalDate;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransportDto fromEntity(Transport transport) {
        return TransportDto.builder()
                .id(transport.getId())
                .orderId(transport.getOrder() != null ? transport.getOrder().getId() : null)
                .orderTitle(transport.getOrder() != null ? transport.getOrder().getTitle() : null)
                .routeId(transport.getRoute() != null ? transport.getRoute().getId() : null)
                .routeName(transport.getRoute() != null ? transport.getRoute().getName() : null)
                .vehicleId(transport.getVehicle() != null ? transport.getVehicle().getId() : null)
                .vehicleRegistrationNumber(transport.getVehicle() != null ? transport.getVehicle().getRegistrationNumber() : null)
                .vehicleName(transport.getVehicle() != null ? transport.getVehicle().getBrand() + " " + transport.getVehicle().getModel() : null)
                .statusName(transport.getStatus() != null ? transport.getStatus().getName() : null)
                .startLocation(transport.getStartLocation())
                .destination(transport.getDestination())
                .transportDate(transport.getTransportDate())
                .plannedArrivalDate(transport.getPlannedArrivalDate())
                .description(transport.getDescription())
                .createdAt(transport.getCreatedAt())
                .updatedAt(transport.getUpdatedAt())
                .build();
    }
}

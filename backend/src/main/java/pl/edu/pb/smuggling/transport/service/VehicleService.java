package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.transport.dto.VehicleFormDto;
import pl.edu.pb.smuggling.transport.model.Vehicle;
import pl.edu.pb.smuggling.transport.repository.VehicleRepository;

import pl.edu.pb.smuggling.common.service.AuditLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final AuditLogService auditLogService;

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Integer id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o ID: " + id));
    }

    @Transactional
    public void createVehicle(VehicleFormDto dto) {
        if (vehicleRepository.findByRegistrationNumber(dto.getRegistrationNumber()).isPresent()) {
            throw new IllegalArgumentException("Pojazd o takiej rejestracji już istnieje w systemie");
        }

        Vehicle vehicle = new Vehicle();
        updateVehicleFromDto(vehicle, dto);
        vehicle = vehicleRepository.save(vehicle);
        auditLogService.logAction("vehicles", vehicle.getId(), "CREATE", null, vehicleToMap(vehicle));
    }

    @Transactional
    public void updateVehicle(Integer id, VehicleFormDto dto) {
        Vehicle vehicle = getVehicleById(id);
        
        // Sprawdzenie czy rejestracja nie należy do innego pojazdu
        vehicleRepository.findByRegistrationNumber(dto.getRegistrationNumber())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(vehicle.getId())) {
                        throw new IllegalArgumentException("Pojazd o takiej rejestracji już istnieje w systemie");
                    }
                });

        Map<String, Object> oldState = vehicleToMap(vehicle);
        updateVehicleFromDto(vehicle, dto);
        vehicleRepository.save(vehicle);
        auditLogService.logAction("vehicles", vehicle.getId(), "UPDATE", oldState, vehicleToMap(vehicle));
    }

    @Transactional
    public void deleteVehicle(Integer id) {
        Vehicle vehicle = getVehicleById(id);
        Map<String, Object> oldState = vehicleToMap(vehicle);
        vehicleRepository.delete(vehicle);
        auditLogService.logAction("vehicles", vehicle.getId(), "DELETE", oldState, null);
    }

    private void updateVehicleFromDto(Vehicle vehicle, VehicleFormDto dto) {
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setLoadCapacity(dto.getLoadCapacity());
        vehicle.setAvailable(dto.getAvailable());
    }

    private Map<String, Object> vehicleToMap(Vehicle vehicle) {
        if (vehicle == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("registrationNumber", vehicle.getRegistrationNumber());
        map.put("brand", vehicle.getBrand());
        map.put("model", vehicle.getModel());
        map.put("vehicleType", vehicle.getVehicleType());
        map.put("loadCapacity", vehicle.getLoadCapacity());
        map.put("available", vehicle.getAvailable());
        return map;
    }
}

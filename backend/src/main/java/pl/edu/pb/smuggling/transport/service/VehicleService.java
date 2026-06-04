package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.transport.dto.VehicleFormDto;
import pl.edu.pb.smuggling.transport.model.Vehicle;
import pl.edu.pb.smuggling.transport.repository.VehicleRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

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
        vehicleRepository.save(vehicle);
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

        updateVehicleFromDto(vehicle, dto);
        vehicleRepository.save(vehicle);
    }

    @Transactional
    public void deleteVehicle(Integer id) {
        Vehicle vehicle = getVehicleById(id);
        // FIXME: w przyszłosci moze dodac walidację przed usunieciem gdy jest przypisany do transportu
        vehicleRepository.delete(vehicle);
    }

    private void updateVehicleFromDto(Vehicle vehicle, VehicleFormDto dto) {
        vehicle.setRegistrationNumber(dto.getRegistrationNumber());
        vehicle.setBrand(dto.getBrand());
        vehicle.setModel(dto.getModel());
        vehicle.setVehicleType(dto.getVehicleType());
        vehicle.setLoadCapacity(dto.getLoadCapacity());
        vehicle.setAvailable(dto.getAvailable());
    }
}

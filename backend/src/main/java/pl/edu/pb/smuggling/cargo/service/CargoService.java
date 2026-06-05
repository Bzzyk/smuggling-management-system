package pl.edu.pb.smuggling.cargo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.cargo.dto.CargoFormDto;
import pl.edu.pb.smuggling.cargo.model.Cargo;
import pl.edu.pb.smuggling.cargo.model.CargoType;
import pl.edu.pb.smuggling.cargo.model.Warehouse;
import pl.edu.pb.smuggling.cargo.repository.CargoRepository;
import pl.edu.pb.smuggling.cargo.repository.CargoTypeRepository;
import pl.edu.pb.smuggling.cargo.repository.WarehouseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;
    private final CargoTypeRepository cargoTypeRepository;
    private final WarehouseRepository warehouseRepository;

    public List<Cargo> findAll() {
        return cargoRepository.findAll();
    }

    public Cargo getCargoById(Integer id) {
        return cargoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ładunku o ID: " + id));
    }

    public List<CargoType> getAllCargoTypes() {
        return cargoTypeRepository.findAll();
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Transactional
    public void createCargo(CargoFormDto dto) {
        Cargo cargo = new Cargo();
        updateCargoFromDto(cargo, dto);
        cargoRepository.save(cargo);
    }

    @Transactional
    public void updateCargo(Integer id, CargoFormDto dto) {
        Cargo cargo = getCargoById(id);
        updateCargoFromDto(cargo, dto);
        cargoRepository.save(cargo);
    }

    @Transactional
    public void deleteCargo(Integer id) {
        if (!cargoRepository.existsById(id)) {
            throw new IllegalArgumentException("Nie znaleziono ładunku o ID: " + id);
        }
        cargoRepository.deleteById(id);
    }

    private void updateCargoFromDto(Cargo cargo, CargoFormDto dto) {
        CargoType cargoType = cargoTypeRepository.findById(dto.getCargoTypeId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono typu ładunku"));

        cargo.setName(dto.getName());
        cargo.setCargoType(cargoType);
        cargo.setPackagesCount(dto.getPackagesCount());
        cargo.setEstimatedValue(dto.getEstimatedValue());

        if (dto.getWarehouseId() != null) {
            Warehouse warehouse = warehouseRepository.findById(dto.getWarehouseId())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono magazynu"));
            cargo.setWarehouse(warehouse);
        } else {
            cargo.setWarehouse(null);
        }
    }
}

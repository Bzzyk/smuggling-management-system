package pl.edu.pb.smuggling.cargo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.cargo.dto.WarehouseFormDto;
import pl.edu.pb.smuggling.cargo.model.Warehouse;
import pl.edu.pb.smuggling.cargo.repository.WarehouseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public Warehouse getWarehouseById(Integer id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono magazynu o ID: " + id));
    }

    @Transactional
    public void createWarehouse(WarehouseFormDto dto) {
        Warehouse warehouse = new Warehouse();
        updateWarehouseFromDto(warehouse, dto);
        warehouseRepository.save(warehouse);
    }

    @Transactional
    public void updateWarehouse(Integer id, WarehouseFormDto dto) {
        Warehouse warehouse = getWarehouseById(id);
        updateWarehouseFromDto(warehouse, dto);
        warehouseRepository.save(warehouse);
    }

    @Transactional
    public void deleteWarehouse(Integer id) {
        if (!warehouseRepository.existsById(id)) {
            throw new IllegalArgumentException("Nie znaleziono magazynu o ID: " + id);
        }
        warehouseRepository.deleteById(id);
    }

    private void updateWarehouseFromDto(Warehouse warehouse, WarehouseFormDto dto) {
        warehouse.setName(dto.getName());
        warehouse.setLocation(dto.getLocation());
        warehouse.setMaxCapacity(dto.getMaxCapacity());
        warehouse.setActive(dto.isActive());
    }
}

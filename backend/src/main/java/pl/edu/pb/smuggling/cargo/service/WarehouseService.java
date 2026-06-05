package pl.edu.pb.smuggling.cargo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.cargo.dto.WarehouseFormDto;
import pl.edu.pb.smuggling.cargo.model.Warehouse;
import pl.edu.pb.smuggling.cargo.repository.WarehouseRepository;
import pl.edu.pb.smuggling.cargo.repository.WarehouseStockRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    public List<Warehouse> findAll() {
        return warehouseRepository.findAll();
    }

    public List<WarehouseCapacityView> findAllWithCapacityUsage() {
        return findAll().stream()
                .map(this::toCapacityView)
                .collect(Collectors.toList());
    }

    public Warehouse getWarehouseById(Integer id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono magazynu o ID: " + id));
    }

    public WarehouseCapacityView getWarehouseCapacityView(Integer id) {
        return toCapacityView(getWarehouseById(id));
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

    private WarehouseCapacityView toCapacityView(Warehouse warehouse) {
        Integer usedCapacityValue = warehouseStockRepository.sumQuantityByWarehouseId(warehouse.getId());
        int usedCapacity = usedCapacityValue != null ? usedCapacityValue : 0;
        int maxCapacity = warehouse.getMaxCapacity();
        int freeCapacity = maxCapacity - usedCapacity;
        int usagePercentage = maxCapacity > 0
                ? (int) Math.round((usedCapacity * 100.0) / maxCapacity)
                : 0;

        return new WarehouseCapacityView(warehouse, usedCapacity, freeCapacity, usagePercentage);
    }

    public record WarehouseCapacityView(
            Warehouse warehouse,
            int usedCapacity,
            int freeCapacity,
            int usagePercentage
    ) {
    }
}

package pl.edu.pb.smuggling.cargo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.cargo.repository.WarehouseRepository;

@Service
@RequiredArgsConstructor
public class WarehouseService {
    private final WarehouseRepository warehouseRepository;
}

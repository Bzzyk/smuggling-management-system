package pl.edu.pb.smuggling.cargo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.cargo.repository.CargoRepository;

@Service
@RequiredArgsConstructor
public class CargoService {
    private final CargoRepository cargoRepository;

    public java.util.List<pl.edu.pb.smuggling.cargo.model.Cargo> findAll() {
        return cargoRepository.findAll();
    }
}

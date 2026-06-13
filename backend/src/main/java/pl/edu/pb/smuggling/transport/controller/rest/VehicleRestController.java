package pl.edu.pb.smuggling.transport.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pb.smuggling.transport.dto.VehicleFormDto;
import pl.edu.pb.smuggling.transport.dto.rest.VehicleDto;
import pl.edu.pb.smuggling.transport.model.VehicleType;
import pl.edu.pb.smuggling.transport.service.VehicleService;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleRestController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public ResponseEntity<Page<VehicleDto>> getVehicles(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size,
                                                        @RequestParam(defaultValue = "registrationNumber") String sort,
                                                        @RequestParam(defaultValue = "asc") String dir) {
        return ResponseEntity.ok(vehicleService.getVehiclesPage(page, Math.max(size, 1), sort, dir).map(VehicleDto::fromEntity));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getVehicle(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(VehicleDto.fromEntity(vehicleService.getVehicleById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping
    public ResponseEntity<VehicleDto> createVehicle(@Valid @RequestBody VehicleFormDto request) {
        try {
            vehicleService.createVehicle(request);
            return ResponseEntity.status(201).body(VehicleDto.fromEntity(vehicleService.getVehicleByRegistrationNumber(request.getRegistrationNumber())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> updateVehicle(@PathVariable Integer id,
                                                    @Valid @RequestBody VehicleFormDto request) {
        try {
            vehicleService.updateVehicle(id, request);
            return ResponseEntity.ok(VehicleDto.fromEntity(vehicleService.getVehicleById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateVehicle(@PathVariable Integer id) {
        try {
            vehicleService.deactivateVehicle(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/types")
    public ResponseEntity<List<VehicleType>> getVehicleTypes() {
        return ResponseEntity.ok(Arrays.asList(VehicleType.values()));
    }
}

package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.cargo.model.Cargo;
import pl.edu.pb.smuggling.cargo.repository.CargoRepository;
import pl.edu.pb.smuggling.common.service.AuditLogService;
import pl.edu.pb.smuggling.transport.model.SmugglerAssignment;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.repository.SmugglerAssignmentRepository;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransportAssignmentService {

    private final TransportRepository transportRepository;
    private final SmugglerAssignmentRepository assignmentRepository;
    private final CargoRepository cargoRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AuditLogService auditLogService;

    public List<SmugglerAssignment> getAssignmentsForTransport(Integer transportId) {
        return assignmentRepository.findByTransportId(transportId);
    }

    public List<Cargo> getCargosForTransport(Integer transportId) {
        return cargoRepository.findByTransportId(transportId);
    }

    @Transactional
    public void assignVehicle(Integer transportId, Integer vehicleId) {
        Transport transport = getTransportById(transportId);
        Map<String, Object> oldState = transportToMap(transport);

        jdbcTemplate.update("CALL assign_vehicle_to_transport(?, ?)", transportId, vehicleId);

        Map<String, Object> newState = new HashMap<>();
        newState.put("vehicleId", vehicleId);
        auditLogService.logAction("transports", transportId, "ASSIGN_VEHICLE", oldState, newState);
    }

    @Transactional
    public void assignCargo(Integer transportId, Integer cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ladunku o ID: " + cargoId));
        Map<String, Object> oldState = cargoToMap(cargo);

        jdbcTemplate.update("CALL assign_cargo_to_transport(?, ?)", transportId, cargoId);

        Map<String, Object> newState = new HashMap<>(oldState);
        newState.put("transportId", transportId);
        auditLogService.logAction("cargo", cargoId, "ASSIGN_TRANSPORT", oldState, newState);
    }

    @Transactional
    public void unassignCargo(Integer transportId, Integer cargoId) {
        Cargo cargo = cargoRepository.findById(cargoId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono ladunku o ID: " + cargoId));
        if (cargo.getTransport() == null || !cargo.getTransport().getId().equals(transportId)) {
            throw new IllegalArgumentException("Ten ladunek nie jest przypisany do tego transportu.");
        }

        Map<String, Object> oldState = cargoToMap(cargo);
        cargo.setTransport(null);
        cargoRepository.save(cargo);
        auditLogService.logAction("cargo", cargoId, "UNASSIGN_TRANSPORT", oldState, cargoToMap(cargo));
    }

    @Transactional
    public void assignSmuggler(Integer transportId, Integer smugglerId, String note) {
        jdbcTemplate.update("CALL assign_smuggler_to_transport(?, ?, ?)", transportId, smugglerId, note);

        Map<String, Object> newState = new HashMap<>();
        newState.put("transportId", transportId);
        newState.put("smugglerId", smugglerId);
        newState.put("note", note);
        auditLogService.logAction("smuggler_assignments", transportId, "ASSIGN_SMUGGLER", null, newState);
    }

    @Transactional
    public void unassignSmuggler(Integer assignmentId) {
        SmugglerAssignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
        Map<String, Object> oldState = assignmentToMap(assignment);
        assignmentRepository.deleteById(assignmentId);
        auditLogService.logAction("smuggler_assignments", assignmentId, "DELETE", oldState, null);
    }

    private Transport getTransportById(Integer id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono transportu o ID: " + id));
    }

    private Map<String, Object> transportToMap(Transport transport) {
        if (transport == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", transport.getOrder() != null ? transport.getOrder().getId() : null);
        map.put("route", transport.getRoute() != null ? transport.getRoute().getName() : null);
        map.put("vehicle", transport.getVehicle() != null ? transport.getVehicle().getRegistrationNumber() : null);
        map.put("status", transport.getStatus() != null ? transport.getStatus().getName() : null);
        map.put("startLocation", transport.getStartLocation());
        map.put("destination", transport.getDestination());
        map.put("transportDate", transport.getTransportDate());
        map.put("plannedArrivalDate", transport.getPlannedArrivalDate());
        map.put("description", transport.getDescription());
        return map;
    }

    private Map<String, Object> assignmentToMap(SmugglerAssignment assignment) {
        if (assignment == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("transportId", assignment.getTransport() != null ? assignment.getTransport().getId() : null);
        map.put("smugglerId", assignment.getSmuggler() != null ? assignment.getSmuggler().getUserId() : null);
        map.put("note", assignment.getNote());
        map.put("active", assignment.isActive());
        return map;
    }

    private Map<String, Object> cargoToMap(Cargo cargo) {
        if (cargo == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", cargo.getName());
        map.put("packagesCount", cargo.getPackagesCount());
        map.put("estimatedValue", cargo.getEstimatedValue());
        map.put("orderId", cargo.getOrder() != null ? cargo.getOrder().getId() : null);
        map.put("transportId", cargo.getTransport() != null ? cargo.getTransport().getId() : null);
        map.put("warehouseId", cargo.getWarehouse() != null ? cargo.getWarehouse().getId() : null);
        return map;
    }
}

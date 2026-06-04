package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;
import pl.edu.pb.smuggling.transport.dto.TransportFormDto;
import pl.edu.pb.smuggling.transport.model.Route;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.model.TransportStatus;
import pl.edu.pb.smuggling.transport.model.Vehicle;
import pl.edu.pb.smuggling.transport.repository.RouteRepository;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;
import pl.edu.pb.smuggling.transport.repository.TransportStatusRepository;
import pl.edu.pb.smuggling.transport.repository.VehicleRepository;
import pl.edu.pb.smuggling.transport.model.SmugglerAssignment;
import pl.edu.pb.smuggling.transport.repository.SmugglerAssignmentRepository;
import pl.edu.pb.smuggling.user.model.SmugglerProfile;
import pl.edu.pb.smuggling.user.repository.SmugglerProfileRepository;

import pl.edu.pb.smuggling.common.service.AuditLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
    private final SmugglingOrderRepository smugglingOrderRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final TransportStatusRepository transportStatusRepository;
    private final SmugglerAssignmentRepository assignmentRepository;
    private final SmugglerProfileRepository smugglerProfileRepository;
    private final AuditLogService auditLogService;

    public List<Transport> getAllTransports() {
        return transportRepository.findAll();
    }

    public List<SmugglingOrder> getAllOrders() {
        return smugglingOrderRepository.findAll();
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<TransportStatus> getAllTransportStatuses() {
        return transportStatusRepository.findAll();
    }

    public Transport getTransportById(Integer id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono transportu o ID: " + id));
    }

    @Transactional
    public void createTransport(TransportFormDto dto) {
        Transport transport = new Transport();
        updateTransportFromDto(transport, dto);
        transport = transportRepository.save(transport);
        auditLogService.logAction("transports", transport.getId(), "CREATE", null, transportToMap(transport));
    }

    @Transactional
    public void updateTransport(Integer id, TransportFormDto dto) {
        Transport transport = getTransportById(id);
        Map<String, Object> oldState = transportToMap(transport);
        updateTransportFromDto(transport, dto);
        transportRepository.save(transport);
        auditLogService.logAction("transports", transport.getId(), "UPDATE", oldState, transportToMap(transport));
    }

    @Transactional
    public void deleteTransport(Integer id) {
        Transport transport = getTransportById(id);
        Map<String, Object> oldState = transportToMap(transport);
        transportRepository.delete(transport);
        auditLogService.logAction("transports", transport.getId(), "DELETE", oldState, null);
    }

    private void updateTransportFromDto(Transport transport, TransportFormDto dto) {
        SmugglingOrder order = smugglingOrderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zlecenia"));
        transport.setOrder(order);

        if (dto.getRouteId() != null) {
            Route route = routeRepository.findById(dto.getRouteId())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trasy"));
            transport.setRoute(route);
        } else {
            transport.setRoute(null);
        }

        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu"));
            transport.setVehicle(vehicle);
        } else {
            transport.setVehicle(null);
        }

        TransportStatus status = transportStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono statusu transportu"));
        transport.setStatus(status);

        transport.setStartLocation(dto.getStartLocation());
        transport.setDestination(dto.getDestination());
        transport.setTransportDate(dto.getTransportDate());
        transport.setPlannedArrivalDate(dto.getPlannedArrivalDate());
        transport.setDescription(dto.getDescription());
    }

    public List<SmugglerAssignment> getAssignmentsForTransport(Integer transportId) {
        return assignmentRepository.findByTransportId(transportId);
    }

    public List<SmugglerProfile> getAllSmugglers() {
        return smugglerProfileRepository.findAll();
    }

    @Transactional
    public void assignSmuggler(Integer transportId, Integer smugglerId, String note) {
        Transport transport = getTransportById(transportId);
        SmugglerProfile smuggler = smugglerProfileRepository.findById(smugglerId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono profilu przemytnika"));

        boolean alreadyAssigned = getAssignmentsForTransport(transportId).stream()
                .anyMatch(a -> a.getSmuggler().getUserId().equals(smugglerId));
        if (alreadyAssigned) {
            throw new IllegalArgumentException("Ten przemytnik jest już przypisany do tego transportu.");
        }

        SmugglerAssignment assignment = new SmugglerAssignment();
        assignment.setTransport(transport);
        assignment.setSmuggler(smuggler);
        assignment.setNote(note);
        assignment.setActive(true);
        assignment = assignmentRepository.save(assignment);
        auditLogService.logAction("smuggler_assignments", assignment.getId(), "CREATE", null, assignmentToMap(assignment));
    }

    @Transactional
    public void unassignSmuggler(Integer assignmentId) {
        SmugglerAssignment assignment = assignmentRepository.findById(assignmentId).orElse(null);
        Map<String, Object> oldState = assignmentToMap(assignment);
        assignmentRepository.deleteById(assignmentId);
        auditLogService.logAction("smuggler_assignments", assignmentId, "DELETE", oldState, null);
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
}

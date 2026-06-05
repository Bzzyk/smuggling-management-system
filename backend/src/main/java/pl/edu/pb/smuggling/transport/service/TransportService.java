package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.common.service.AuditLogService;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;
import pl.edu.pb.smuggling.transport.dto.TransportFormDto;
import pl.edu.pb.smuggling.transport.model.Route;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.model.TransportStatus;
import pl.edu.pb.smuggling.transport.repository.RouteRepository;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;
import pl.edu.pb.smuggling.transport.repository.TransportStatusRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransportService {

    private static final String STATUS_PLANNED = "ZAPLANOWANY";
    private static final String STATUS_ON_ROAD = "W_DRODZE";

    private final TransportRepository transportRepository;
    private final SmugglingOrderRepository smugglingOrderRepository;
    private final RouteRepository routeRepository;
    private final TransportStatusRepository transportStatusRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AuditLogService auditLogService;

    public List<Transport> getAllTransports() {
        return transportRepository.findAll();
    }

    public SmugglingOrder getOrderById(Integer id) {
        return smugglingOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zlecenia o ID: " + id));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Transport getTransportById(Integer id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono transportu o ID: " + id));
    }

    @Transactional
    public Transport createTransport(TransportFormDto dto) {
        Transport transport = new Transport();
        updateTransportFromDto(transport, dto);

        TransportStatus plannedStatus = transportStatusRepository.findByName(STATUS_PLANNED)
                .orElseThrow(() -> new IllegalArgumentException("Brak statusu ZAPLANOWANY w slowniku"));
        transport.setStatus(plannedStatus);

        transport = transportRepository.save(transport);
        auditLogService.logAction("transports", transport.getId(), "CREATE", null, transportToMap(transport));
        return transport;
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

    @Transactional
    public void startTransport(Integer transportId) {
        Transport transport = getTransportById(transportId);
        Map<String, Object> oldState = transportToMap(transport);

        jdbcTemplate.update("CALL change_transport_status(?, ?)", transportId, STATUS_ON_ROAD);

        Map<String, Object> newState = new HashMap<>();
        newState.put("status", STATUS_ON_ROAD);
        auditLogService.logAction("transports", transportId, "CHANGE_STATUS", oldState, newState);
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

        transport.setStartLocation(dto.getStartLocation());
        transport.setDestination(dto.getDestination());
        transport.setTransportDate(dto.getTransportDate());
        transport.setPlannedArrivalDate(dto.getPlannedArrivalDate());
        transport.setDescription(dto.getDescription());
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
}

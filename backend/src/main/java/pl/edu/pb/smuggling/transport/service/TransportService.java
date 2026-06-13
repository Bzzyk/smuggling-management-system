package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDeniedException;
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
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransportService {

    private static final String STATUS_PLANNED = "ZAPLANOWANY";
    private static final String STATUS_ON_ROAD = "W_DRODZE";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_BOSS = "BOSS";
    private static final String ROLE_SMUGGLER = "SMUGGLER";

    private final TransportRepository transportRepository;
    private final SmugglingOrderRepository smugglingOrderRepository;
    private final RouteRepository routeRepository;
    private final TransportStatusRepository transportStatusRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final AuditLogService auditLogService;

    public List<Transport> getAllTransports() {
        return transportRepository.findAll();
    }

    public List<Transport> getVisibleTransports(String username) {
        User user = getUserByUsername(username);
        if (hasRole(user, ROLE_ADMIN)) {
            return transportRepository.findAll();
        }
        if (hasRole(user, ROLE_BOSS)) {
            return transportRepository.findVisibleForBoss(user.getId());
        }
        if (hasRole(user, ROLE_SMUGGLER)) {
            return transportRepository.findVisibleForSmuggler(user.getId());
        }
        return List.of();
    }

    public Page<Transport> getVisibleTransports(String username, int page, int size, String sort, String dir, String statusName, String dateFilter) {
        User user = getUserByUsername(username);
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                size,
                Sort.by(getDirection(dir), getTransportSortProperty(sort))
        );
        DateRange dateRange = getDateRange(dateFilter);
        String normalizedStatus = normalizeFilter(statusName);

        return transportRepository.findVisibleTransports(
                user.getId(),
                hasRole(user, ROLE_ADMIN),
                hasRole(user, ROLE_BOSS),
                hasRole(user, ROLE_SMUGGLER),
                normalizedStatus != null,
                normalizedStatus,
                dateRange.from() != null,
                dateRange.from(),
                dateRange.to() != null,
                dateRange.to(),
                pageable
        );
    }

    public SmugglingOrder getOrderById(Integer id) {
        return smugglingOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zlecenia o ID: " + id));
    }

    public List<Route> getAllRoutes() {
        return routeRepository.findByActiveTrue();
    }

    public List<TransportStatus> getAllStatuses() {
        return transportStatusRepository.findAll();
    }

    public Transport getTransportById(Integer id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono transportu o ID: " + id));
    }

    public Transport getVisibleTransportById(Integer id, String username) {
        User user = getUserByUsername(username);
        Transport transport = getTransportById(id);
        if (!canViewTransport(user, transport)) {
            throw new AccessDeniedException("Brak dostepu do tego transportu.");
        }
        return transport;
    }

    public Transport getManageableTransportById(Integer id, String username) {
        User user = getUserByUsername(username);
        Transport transport = getTransportById(id);
        if (!canManageTransport(user, transport)) {
            throw new AccessDeniedException("Brak uprawnien do zarzadzania tym transportem.");
        }
        return transport;
    }

    public void assertCanManageTransport(Integer id, String username) {
        getManageableTransportById(id, username);
    }

    public void assertCanEditPlannedTransport(Integer id, String username) {
        Transport transport = getManageableTransportById(id, username);
        if (transport.getStatus() == null || !STATUS_PLANNED.equals(transport.getStatus().getName())) {
            throw new IllegalArgumentException("Transport mozna edytowac tylko w statusie ZAPLANOWANY.");
        }
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
        changeTransportStatus(transportId, STATUS_ON_ROAD);
    }

    @Transactional
    public void changeTransportStatus(Integer transportId, String statusName) {
        Transport transport = getTransportById(transportId);
        Map<String, Object> oldState = transportToMap(transport);

        jdbcTemplate.update("CALL change_transport_status(?, ?)", transportId, statusName);

        Map<String, Object> newState = new HashMap<>();
        newState.put("status", statusName);
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

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono uzytkownika: " + username));
    }

    private boolean canViewTransport(User user, Transport transport) {
        if (hasRole(user, ROLE_ADMIN)) {
            return true;
        }
        if (hasRole(user, ROLE_BOSS)) {
            return isBossTransport(user, transport);
        }
        if (hasRole(user, ROLE_SMUGGLER)) {
            return transportRepository.findVisibleForSmuggler(user.getId()).stream()
                    .anyMatch(visibleTransport -> visibleTransport.getId().equals(transport.getId()));
        }
        return false;
    }

    private boolean canManageTransport(User user, Transport transport) {
        if (hasRole(user, ROLE_ADMIN)) {
            return true;
        }
        return hasRole(user, ROLE_BOSS) && isBossTransport(user, transport);
    }

    private boolean isBossTransport(User user, Transport transport) {
        return transport.getOrder() != null
                && ((transport.getOrder().getCreatedBy() != null
                && user.getId().equals(transport.getOrder().getCreatedBy().getId()))
                || (transport.getOrder().getResponsibleUser() != null
                && user.getId().equals(transport.getOrder().getResponsibleUser().getId())));
    }

    private boolean hasRole(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(role -> roleName.equals(role.getName()));
    }

    private Sort.Direction getDirection(String dir) {
        return "asc".equalsIgnoreCase(dir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    private String getTransportSortProperty(String sort) {
        return switch (sort) {
            case "transportDate", "startLocation", "destination" -> sort;
            default -> "transportDate";
        };
    }

    private String normalizeFilter(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private DateRange getDateRange(String dateFilter) {
        LocalDate today = LocalDate.now();
        return switch (dateFilter == null ? "ALL" : dateFilter) {
            case "TODAY" -> new DateRange(today, today);
            case "NEXT_7" -> new DateRange(today, today.plusDays(7));
            case "NEXT_30" -> new DateRange(today, today.plusDays(30));
            case "FUTURE" -> new DateRange(today, null);
            default -> new DateRange(null, null);
        };
    }

    private record DateRange(LocalDate from, LocalDate to) {
    }
}

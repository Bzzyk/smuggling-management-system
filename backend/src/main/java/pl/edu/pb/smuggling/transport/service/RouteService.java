package pl.edu.pb.smuggling.transport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.transport.dto.RouteFormDto;
import pl.edu.pb.smuggling.transport.model.Route;
import pl.edu.pb.smuggling.transport.model.RouteDifficultyLevel;
import pl.edu.pb.smuggling.transport.repository.RouteDifficultyLevelRepository;
import pl.edu.pb.smuggling.transport.repository.RouteRepository;

import pl.edu.pb.smuggling.common.service.AuditLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final RouteDifficultyLevelRepository difficultyLevelRepository;
    private final AuditLogService auditLogService;

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public List<RouteDifficultyLevel> getAllDifficultyLevels() {
        return difficultyLevelRepository.findAll();
    }

    public Route getRouteById(Integer id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono trasy o ID: " + id));
    }

    @Transactional
    public void createRoute(RouteFormDto dto) {
        if (routeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Trasa o takiej nazwie już istnieje w systemie");
        }

        Route route = new Route();
        updateRouteFromDto(route, dto);
        route = routeRepository.save(route);
        auditLogService.logAction("routes", route.getId(), "CREATE", null, routeToMap(route));
    }

    @Transactional
    public void updateRoute(Integer id, RouteFormDto dto) {
        Route route = getRouteById(id);
        
        if (!route.getName().equals(dto.getName()) && routeRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Trasa o takiej nazwie już istnieje w systemie");
        }

        Map<String, Object> oldState = routeToMap(route);
        updateRouteFromDto(route, dto);
        routeRepository.save(route);
        auditLogService.logAction("routes", route.getId(), "UPDATE", oldState, routeToMap(route));
    }

    @Transactional
    public void deleteRoute(Integer id) {
        Route route = getRouteById(id);
        Map<String, Object> oldState = routeToMap(route);
        routeRepository.delete(route);
        auditLogService.logAction("routes", route.getId(), "DELETE", oldState, null);
    }

    private void updateRouteFromDto(Route route, RouteFormDto dto) {
        route.setName(dto.getName());
        route.setStartPoint(dto.getStartPoint());
        route.setEndPoint(dto.getEndPoint());
        route.setDistanceKm(dto.getDistanceKm());
        route.setDescription(dto.getDescription());
        
        RouteDifficultyLevel difficulty = difficultyLevelRepository.findById(dto.getDifficultyLevelId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono poziomu trudności"));
        route.setDifficultyLevel(difficulty);
    }

    private Map<String, Object> routeToMap(Route route) {
        if (route == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("name", route.getName());
        map.put("startPoint", route.getStartPoint());
        map.put("endPoint", route.getEndPoint());
        map.put("distanceKm", route.getDistanceKm());
        map.put("description", route.getDescription());
        map.put("difficultyLevel", route.getDifficultyLevel() != null ? route.getDifficultyLevel().getName() : null);
        return map;
    }
}

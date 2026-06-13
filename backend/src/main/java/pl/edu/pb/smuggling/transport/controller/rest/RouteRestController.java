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
import pl.edu.pb.smuggling.transport.dto.RouteFormDto;
import pl.edu.pb.smuggling.transport.dto.rest.RouteDifficultyLevelDto;
import pl.edu.pb.smuggling.transport.dto.rest.RouteDto;
import pl.edu.pb.smuggling.transport.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteRestController {

    private final RouteService routeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public ResponseEntity<Page<RouteDto>> getRoutes(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestParam(defaultValue = "name") String sort,
                                                    @RequestParam(defaultValue = "asc") String dir) {
        return ResponseEntity.ok(routeService.getRoutesPage(page, Math.max(size, 1), sort, dir).map(RouteDto::fromEntity));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public ResponseEntity<RouteDto> getRoute(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(RouteDto.fromEntity(routeService.getRouteById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping
    public ResponseEntity<RouteDto> createRoute(@Valid @RequestBody RouteFormDto request) {
        try {
            routeService.createRoute(request);
            return ResponseEntity.status(201).body(RouteDto.fromEntity(routeService.getRouteByName(request.getName())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}")
    public ResponseEntity<RouteDto> updateRoute(@PathVariable Integer id,
                                                @Valid @RequestBody RouteFormDto request) {
        try {
            routeService.updateRoute(id, request);
            return ResponseEntity.ok(RouteDto.fromEntity(routeService.getRouteById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateRoute(@PathVariable Integer id) {
        try {
            routeService.deactivateRoute(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/difficulty-levels")
    public ResponseEntity<List<RouteDifficultyLevelDto>> getDifficultyLevels() {
        return ResponseEntity.ok(routeService.getAllDifficultyLevels().stream()
                .map(RouteDifficultyLevelDto::fromEntity)
                .toList());
    }
}

package pl.edu.pb.smuggling.transport.controller.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pb.smuggling.transport.dto.TransportFormDto;
import pl.edu.pb.smuggling.transport.dto.rest.AssignSmugglerRequest;
import pl.edu.pb.smuggling.transport.dto.rest.ChangeTransportStatusRequest;
import pl.edu.pb.smuggling.transport.dto.rest.SmugglerAssignmentDto;
import pl.edu.pb.smuggling.transport.dto.rest.TransportDto;
import pl.edu.pb.smuggling.transport.dto.rest.TransportStatusDto;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.service.TransportAssignmentService;
import pl.edu.pb.smuggling.transport.service.TransportEstimateService;
import pl.edu.pb.smuggling.transport.service.TransportService;

import java.util.List;

@RestController
@RequestMapping("/api/transports")
@RequiredArgsConstructor
public class TransportRestController {

    private final TransportService transportService;
    private final TransportAssignmentService transportAssignmentService;
    private final TransportEstimateService transportEstimateService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public ResponseEntity<Page<TransportDto>> getTransports(@AuthenticationPrincipal UserDetails userDetails,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size,
                                                            @RequestParam(defaultValue = "transportDate") String sort,
                                                            @RequestParam(defaultValue = "desc") String dir,
                                                            @RequestParam(required = false) String status,
                                                            @RequestParam(defaultValue = "ALL") String dateFilter) {
        Page<Transport> transports = transportService.getVisibleTransports(
                userDetails.getUsername(),
                Math.max(page, 0),
                Math.max(size, 1),
                sort,
                dir,
                status,
                dateFilter
        );
        return ResponseEntity.ok(transports.map(TransportDto::fromEntity));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public ResponseEntity<TransportDto> getTransport(@PathVariable Integer id,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(TransportDto.fromEntity(transportService.getVisibleTransportById(id, userDetails.getUsername())));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping
    public ResponseEntity<TransportDto> createTransport(@Valid @RequestBody TransportFormDto request) {
        try {
            Transport transport = transportService.createTransport(request);
            return ResponseEntity.status(201).body(TransportDto.fromEntity(transport));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}")
    public ResponseEntity<TransportDto> updateTransport(@PathVariable Integer id,
                                                        @Valid @RequestBody TransportFormDto request,
                                                        @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportService.updateTransport(id, request);
            return ResponseEntity.ok(TransportDto.fromEntity(transportService.getTransportById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelTransport(@PathVariable Integer id,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanManageTransport(id, userDetails.getUsername());
            transportService.changeTransportStatus(id, "ANULOWANY");
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}/status")
    public ResponseEntity<TransportDto> changeStatus(@PathVariable Integer id,
                                                     @Valid @RequestBody ChangeTransportStatusRequest request,
                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanManageTransport(id, userDetails.getUsername());
            transportService.changeTransportStatus(id, request.getStatusName());
            return ResponseEntity.ok(TransportDto.fromEntity(transportService.getTransportById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}/vehicle/{vehicleId}")
    public ResponseEntity<TransportDto> assignVehicle(@PathVariable Integer id,
                                                      @PathVariable Integer vehicleId,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.assignVehicle(id, vehicleId);
            return ResponseEntity.ok(TransportDto.fromEntity(transportService.getTransportById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}/cargo/{cargoId}")
    public ResponseEntity<TransportDto> assignCargo(@PathVariable Integer id,
                                                    @PathVariable Integer cargoId,
                                                    @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.assignCargo(id, cargoId);
            return ResponseEntity.ok(TransportDto.fromEntity(transportService.getTransportById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @DeleteMapping("/{id}/cargo/{cargoId}")
    public ResponseEntity<TransportDto> unassignCargo(@PathVariable Integer id,
                                                      @PathVariable Integer cargoId,
                                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.unassignCargo(id, cargoId);
            return ResponseEntity.ok(TransportDto.fromEntity(transportService.getTransportById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/smugglers")
    public ResponseEntity<List<SmugglerAssignmentDto>> assignSmuggler(@PathVariable Integer id,
                                                                      @Valid @RequestBody AssignSmugglerRequest request,
                                                                      @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.assignSmuggler(id, request.getSmugglerId(), request.getNote());
            return ResponseEntity.ok(getAssignmentDtos(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @DeleteMapping("/{id}/smugglers/{assignmentId}")
    public ResponseEntity<List<SmugglerAssignmentDto>> unassignSmuggler(@PathVariable Integer id,
                                                                       @PathVariable Integer assignmentId,
                                                                       @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.unassignSmuggler(assignmentId);
            return ResponseEntity.ok(getAssignmentDtos(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (org.springframework.dao.DataAccessException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}/smugglers")
    public ResponseEntity<List<SmugglerAssignmentDto>> getAssignments(@PathVariable Integer id,
                                                                     @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.getVisibleTransportById(id, userDetails.getUsername());
            return ResponseEntity.ok(getAssignmentDtos(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}/estimate")
    public ResponseEntity<TransportEstimateService.TransportEstimate> getEstimate(@PathVariable Integer id,
                                                                                 @AuthenticationPrincipal UserDetails userDetails) {
        try {
            transportService.getVisibleTransportById(id, userDetails.getUsername());
            return ResponseEntity.ok(transportEstimateService.getTransportEstimate(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/statuses")
    public ResponseEntity<List<TransportStatusDto>> getStatuses() {
        return ResponseEntity.ok(transportService.getAllStatuses().stream()
                .map(TransportStatusDto::fromEntity)
                .toList());
    }

    private List<SmugglerAssignmentDto> getAssignmentDtos(Integer transportId) {
        return transportAssignmentService.getAssignmentsForTransport(transportId).stream()
                .map(SmugglerAssignmentDto::fromEntity)
                .toList();
    }
}

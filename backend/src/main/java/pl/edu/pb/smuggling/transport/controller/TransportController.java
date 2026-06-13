package pl.edu.pb.smuggling.transport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.pb.smuggling.transport.dto.AvailableCargoDto;
import pl.edu.pb.smuggling.transport.dto.AvailableSmugglerDto;
import pl.edu.pb.smuggling.transport.dto.AvailableVehicleDto;
import pl.edu.pb.smuggling.transport.dto.TransportFormDto;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.model.VehicleType;
import pl.edu.pb.smuggling.transport.service.TransportAssignmentService;
import pl.edu.pb.smuggling.transport.service.TransportAvailabilityService;
import pl.edu.pb.smuggling.transport.service.TransportEstimateService;
import pl.edu.pb.smuggling.transport.service.TransportService;

import java.math.BigDecimal;

@Controller
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportController {

    private static final int PICKER_PAGE_SIZE = 10;

    private final TransportService transportService;
    private final TransportAssignmentService transportAssignmentService;
    private final TransportAvailabilityService transportAvailabilityService;
    private final TransportEstimateService transportEstimateService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public String listTransports(@AuthenticationPrincipal UserDetails userDetails,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "transportDate") String sort,
                                 @RequestParam(defaultValue = "desc") String dir,
                                 @RequestParam(required = false) String status,
                                 @RequestParam(defaultValue = "ALL") String dateFilter,
                                 Model model) {
        Page<Transport> transportPage = transportService.getVisibleTransports(
                userDetails.getUsername(),
                safePage(page),
                10,
                sort,
                dir,
                status,
                dateFilter
        );
        model.addAttribute("transportPage", transportPage);
        model.addAttribute("transports", transportPage.getContent());
        model.addAttribute("statuses", transportService.getAllStatuses());
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("reverseDir", "asc".equalsIgnoreCase(dir) ? "desc" : "asc");
        model.addAttribute("status", status);
        model.addAttribute("dateFilter", dateFilter);
        return "transports/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(@RequestParam(required = false) Integer orderId,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (orderId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Najpierw wybierz zlecenie, dla ktorego planujesz transport.");
            return "redirect:/orders";
        }

        TransportFormDto form = new TransportFormDto();
        form.setOrderId(orderId);
        model.addAttribute("transportForm", form);
        populateFormDictionaries(orderId, model);
        return "transports/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createTransport(@Valid @ModelAttribute("transportForm") TransportFormDto form,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(form.getOrderId(), model);
            return "transports/form";
        }
        try {
            Transport transport = transportService.createTransport(form);
            redirectAttributes.addFlashAttribute("successMessage", "Utworzono szkic transportu. Teraz przypisz ladunek, pojazd i ekipe.");
            return "redirect:/transports/" + transport.getId();
        } catch (IllegalArgumentException e) {
            result.reject("error.transport", e.getMessage());
            populateFormDictionaries(form.getOrderId(), model);
            return "transports/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id,
                               @AuthenticationPrincipal UserDetails userDetails,
                               Model model) {
        transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
        Transport transport = transportService.getManageableTransportById(id, userDetails.getUsername());
        TransportFormDto dto = new TransportFormDto();
        dto.setId(transport.getId());
        dto.setOrderId(transport.getOrder().getId());
        dto.setRouteId(transport.getRoute() != null ? transport.getRoute().getId() : null);
        dto.setStartLocation(transport.getStartLocation());
        dto.setDestination(transport.getDestination());
        dto.setTransportDate(transport.getTransportDate());
        dto.setPlannedArrivalDate(transport.getPlannedArrivalDate());
        dto.setDescription(transport.getDescription());

        model.addAttribute("transportForm", dto);
        populateFormDictionaries(dto.getOrderId(), model);
        return "transports/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateTransport(@PathVariable Integer id,
                                  @Valid @ModelAttribute("transportForm") TransportFormDto form,
                                  BindingResult result,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(form.getOrderId(), model);
            return "transports/form";
        }
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportService.updateTransport(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Zaktualizowano parametry planu transportu.");
            return "redirect:/transports/" + id;
        } catch (IllegalArgumentException e) {
            result.reject("error.transport", e.getMessage());
            populateFormDictionaries(form.getOrderId(), model);
            return "transports/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public String showDetails(@PathVariable Integer id,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        Transport transport = transportService.getVisibleTransportById(id, userDetails.getUsername());
        model.addAttribute("transport", transport);
        model.addAttribute("assignments", transportAssignmentService.getAssignmentsForTransport(id));
        model.addAttribute("cargos", transportAssignmentService.getCargosForTransport(id));
        model.addAttribute("cargoPackagesTotal", transportEstimateService.getCargoPackagesTotal(id));
        model.addAttribute("vehicleCapacityOk", transportEstimateService.isVehicleCapacityEnough(transport));
        model.addAttribute("transportEstimate", transportEstimateService.getTransportEstimate(id));
        return "transports/details";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/vehicles")
    public String selectVehicle(@PathVariable Integer id,
                                @RequestParam(required = false) String vehicleType,
                                @RequestParam(defaultValue = "0") int page,
                                @AuthenticationPrincipal UserDetails userDetails,
                                Model model) {
        transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
        Transport transport = transportService.getManageableTransportById(id, userDetails.getUsername());
        Page<AvailableVehicleDto> vehiclePage = transportAvailabilityService.getAssignableVehicles(id, vehicleType, safePage(page), PICKER_PAGE_SIZE);
        model.addAttribute("transport", transport);
        model.addAttribute("vehiclePage", vehiclePage);
        model.addAttribute("vehicles", vehiclePage.getContent());
        model.addAttribute("cargoPackagesTotal", transportEstimateService.getCargoPackagesTotal(id));
        model.addAttribute("vehicleTypes", VehicleType.values());
        model.addAttribute("selectedVehicleType", vehicleType);
        return "transports/select-vehicle";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/vehicles/{vehicleId}/assign")
    public String assignVehicle(@PathVariable Integer id,
                                @PathVariable Integer vehicleId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.assignVehicle(id, vehicleId);
            redirectAttributes.addFlashAttribute("successMessage", "Pojazd zostal przypisany przez procedure bazodanowa.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Nie mozna przypisac pojazdu."));
        }
        return "redirect:/transports/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/cargos")
    public String selectCargo(@PathVariable Integer id,
                              @RequestParam(defaultValue = "0") int page,
                              @AuthenticationPrincipal UserDetails userDetails,
                              Model model) {
        transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
        Transport transport = transportService.getManageableTransportById(id, userDetails.getUsername());
        Page<AvailableCargoDto> cargoPage = transportAvailabilityService.getAssignableCargos(id, safePage(page), PICKER_PAGE_SIZE);
        model.addAttribute("transport", transport);
        model.addAttribute("cargoPage", cargoPage);
        model.addAttribute("cargos", cargoPage.getContent());
        return "transports/select-cargo";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/cargos/{cargoId}/assign")
    public String assignCargo(@PathVariable Integer id,
                              @PathVariable Integer cargoId,
                              @AuthenticationPrincipal UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.assignCargo(id, cargoId);
            redirectAttributes.addFlashAttribute("successMessage", "Ladunek zostal przypisany do transportu.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Nie mozna przypisac ladunku."));
        }
        return "redirect:/transports/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/cargos/{cargoId}/unassign")
    public String unassignCargo(@PathVariable Integer id,
                                @PathVariable Integer cargoId,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.unassignCargo(id, cargoId);
            redirectAttributes.addFlashAttribute("successMessage", "Ladunek zostal odpiety od transportu.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transports/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/smugglers")
    public String selectSmuggler(@PathVariable Integer id,
                                 @RequestParam(required = false) String experienceLevel,
                                 @RequestParam(required = false) BigDecimal minSuccessRate,
                                 @RequestParam(defaultValue = "0") int page,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 Model model) {
        transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
        Transport transport = transportService.getManageableTransportById(id, userDetails.getUsername());
        Page<AvailableSmugglerDto> smugglerPage = transportAvailabilityService.getAssignableSmugglers(
                experienceLevel,
                minSuccessRate,
                safePage(page),
                PICKER_PAGE_SIZE
        );
        model.addAttribute("transport", transport);
        model.addAttribute("smugglerPage", smugglerPage);
        model.addAttribute("smugglers", smugglerPage.getContent());
        model.addAttribute("experienceLevels", new String[]{"JUNIOR", "REGULAR", "SENIOR", "EXPERT"});
        model.addAttribute("selectedExperienceLevel", experienceLevel);
        model.addAttribute("selectedMinSuccessRate", minSuccessRate);
        return "transports/select-smuggler";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/smugglers/{smugglerId}/assign")
    public String assignSmugglerFromPicker(@PathVariable Integer id,
                                           @PathVariable Integer smugglerId,
                                           @RequestParam(required = false) String note,
                                           @AuthenticationPrincipal UserDetails userDetails,
                                           RedirectAttributes redirectAttributes) {
        return assignSmuggler(id, smugglerId, note, userDetails, redirectAttributes);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/assign")
    public String assignSmuggler(@PathVariable Integer id,
                                 @RequestParam Integer smugglerId,
                                 @RequestParam(required = false) String note,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.assignSmuggler(id, smugglerId, note);
            redirectAttributes.addFlashAttribute("successMessage", "Przemytnik zostal przypisany przez procedure bazodanowa.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Wystapil blad bazy danych podczas przypisywania."));
        }
        return "redirect:/transports/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/unassign/{assignmentId}")
    public String unassignSmuggler(@PathVariable Integer id,
                                   @PathVariable Integer assignmentId,
                                   @AuthenticationPrincipal UserDetails userDetails,
                                   RedirectAttributes redirectAttributes) {
        try {
            transportService.assertCanEditPlannedTransport(id, userDetails.getUsername());
            transportAssignmentService.unassignSmuggler(assignmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Usunieto przypisanie z transportu.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Wystapil blad bazy danych podczas usuwania przypisania."));
        }
        return "redirect:/transports/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/start")
    public String startTransport(@PathVariable Integer id,
                                 @AuthenticationPrincipal UserDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        return changeTransportStatus(id, "W_DRODZE", userDetails, redirectAttributes);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/status")
    public String changeTransportStatus(@PathVariable Integer id,
                                        @RequestParam String statusName,
                                        @AuthenticationPrincipal UserDetails userDetails,
                                        RedirectAttributes redirectAttributes) {
        try {
            transportService.assertCanManageTransport(id, userDetails.getUsername());
            transportService.changeTransportStatus(id, statusName);
            redirectAttributes.addFlashAttribute("successMessage", "Status transportu zmienila procedura bazodanowa.");
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Nie mozna zmienic statusu transportu."));
        }
        return "redirect:/transports/" + id;
    }

    private void populateFormDictionaries(Integer orderId, Model model) {
        if (orderId != null) {
            model.addAttribute("selectedOrder", transportService.getOrderById(orderId));
        }
        model.addAttribute("routes", transportService.getAllRoutes());
    }

    private int safePage(int page) {
        return Math.max(page, 0);
    }

    private String extractDbErrorMessage(Exception e, String defaultMsg) {
        Throwable rootCause = org.springframework.core.NestedExceptionUtils.getRootCause(e);
        String message = rootCause != null ? rootCause.getMessage() : e.getMessage();
        if (message != null && message.contains("ERROR: ")) {
            message = message.substring(message.indexOf("ERROR: ") + 7);
            if (message.contains("\n")) {
                message = message.substring(0, message.indexOf("\n"));
            }
            return message;
        }
        return defaultMsg;
    }
}

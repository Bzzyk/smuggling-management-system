package pl.edu.pb.smuggling.transport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
import pl.edu.pb.smuggling.transport.dto.TransportFormDto;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.service.TransportService;

@Controller
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportController {

    private final TransportService transportService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public String listTransports(Model model) {
        model.addAttribute("transports", transportService.getAllTransports());
        return "transports/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(@RequestParam(required = false) Integer orderId, Model model) {
        TransportFormDto form = new TransportFormDto();
        if (orderId != null) {
            form.setOrderId(orderId);
        }
        model.addAttribute("transportForm", form);
        populateFormDictionaries(model);
        return "transports/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createTransport(@Valid @ModelAttribute("transportForm") TransportFormDto form,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "transports/form";
        }
        try {
            transportService.createTransport(form);
            redirectAttributes.addFlashAttribute("successMessage", "Transport został pomyślnie utworzony.");
            return "redirect:/transports";
        } catch (IllegalArgumentException e) {
            result.reject("error.transport", e.getMessage());
            populateFormDictionaries(model);
            return "transports/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Transport transport = transportService.getTransportById(id);
        TransportFormDto dto = new TransportFormDto();
        dto.setId(transport.getId());
        dto.setOrderId(transport.getOrder().getId());
        dto.setRouteId(transport.getRoute() != null ? transport.getRoute().getId() : null);
        dto.setVehicleId(transport.getVehicle() != null ? transport.getVehicle().getId() : null);
        dto.setStatusId(transport.getStatus().getId());
        dto.setStartLocation(transport.getStartLocation());
        dto.setDestination(transport.getDestination());
        dto.setTransportDate(transport.getTransportDate());
        dto.setPlannedArrivalDate(transport.getPlannedArrivalDate());
        dto.setDescription(transport.getDescription());
        
        model.addAttribute("transportForm", dto);
        populateFormDictionaries(model);
        return "transports/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateTransport(@PathVariable Integer id,
                                  @Valid @ModelAttribute("transportForm") TransportFormDto form,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "transports/form";
        }
        try {
            transportService.updateTransport(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Zaktualizowano dane transportu.");
            return "redirect:/transports";
        } catch (IllegalArgumentException e) {
            result.reject("error.transport", e.getMessage());
            populateFormDictionaries(model);
            return "transports/form";
        }
    }

    private void populateFormDictionaries(Model model) {
        model.addAttribute("orders", transportService.getAllOrders());
        model.addAttribute("routes", transportService.getAllRoutes());
        model.addAttribute("vehicles", transportService.getAllVehicles());
        model.addAttribute("statuses", transportService.getAllTransportStatuses());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public String showDetails(@PathVariable Integer id, Model model) {
        Transport transport = transportService.getTransportById(id);
        model.addAttribute("transport", transport);
        model.addAttribute("assignments", transportService.getAssignmentsForTransport(id));
        model.addAttribute("allSmugglers", transportService.getAllSmugglers());
        return "transports/details";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/assign")
    public String assignSmuggler(@PathVariable Integer id,
                                 @RequestParam Integer smugglerId,
                                 @RequestParam(required = false) String note,
                                 RedirectAttributes redirectAttributes) {
        try {
            transportService.assignSmuggler(id, smugglerId, note);
            redirectAttributes.addFlashAttribute("successMessage", "Przemytnik został przypisany.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Wystąpił błąd bazy danych podczas przypisywania."));
        }
        return "redirect:/transports/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/unassign/{assignmentId}")
    public String unassignSmuggler(@PathVariable Integer id,
                                   @PathVariable Integer assignmentId,
                                   RedirectAttributes redirectAttributes) {
        try {
            transportService.unassignSmuggler(assignmentId);
            redirectAttributes.addFlashAttribute("successMessage", "Usunięto przypisanie z transportu.");
        } catch (org.springframework.dao.DataAccessException e) {
            redirectAttributes.addFlashAttribute("errorMessage", extractDbErrorMessage(e, "Wystąpił błąd bazy danych podczas usuwania przypisania."));
        }
        return "redirect:/transports/" + id;
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

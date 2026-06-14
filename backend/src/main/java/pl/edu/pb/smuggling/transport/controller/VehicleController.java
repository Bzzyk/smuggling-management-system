package pl.edu.pb.smuggling.transport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
import pl.edu.pb.smuggling.transport.dto.VehicleFormDto;
import pl.edu.pb.smuggling.transport.model.Vehicle;
import pl.edu.pb.smuggling.transport.model.VehicleType;
import pl.edu.pb.smuggling.transport.service.VehicleService;

@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public String listVehicles(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "registrationNumber") String sort,
                               @RequestParam(defaultValue = "asc") String dir,
                               @RequestParam(required = false) String registrationNumber,
                               @RequestParam(defaultValue = "ALL") String status,
                               Model model) {
        Page<Vehicle> vehiclePage = vehicleService.getVehiclesPage(page, 10, sort, dir, registrationNumber, status);
        model.addAttribute("vehiclePage", vehiclePage);
        model.addAttribute("vehicles", vehiclePage.getContent());
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("registrationNumber", registrationNumber);
        model.addAttribute("status", status);
        model.addAttribute("reverseDir", "asc".equalsIgnoreCase(dir) ? "desc" : "asc");
        return "vehicles/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("vehicleForm", new VehicleFormDto());
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "vehicles/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createVehicle(@Valid @ModelAttribute("vehicleForm") VehicleFormDto form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("vehicleTypes", VehicleType.values());
            return "vehicles/form";
        }
        try {
            vehicleService.createVehicle(form);
            redirectAttributes.addFlashAttribute("successMessage", "Pojazd został pomyślnie dodany.");
            return "redirect:/vehicles";
        } catch (IllegalArgumentException e) {
            result.rejectValue("registrationNumber", "error.vehicle", e.getMessage());
            model.addAttribute("vehicleTypes", VehicleType.values());
            return "vehicles/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        VehicleFormDto dto = new VehicleFormDto();
        dto.setId(vehicle.getId());
        dto.setRegistrationNumber(vehicle.getRegistrationNumber());
        dto.setBrand(vehicle.getBrand());
        dto.setModel(vehicle.getModel());
        dto.setVehicleType(vehicle.getVehicleType());
        dto.setLoadCapacity(vehicle.getLoadCapacity());
        dto.setAvailable(vehicle.getAvailable());
        
        model.addAttribute("vehicleForm", dto);
        model.addAttribute("vehicleTypes", VehicleType.values());
        return "vehicles/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateVehicle(@PathVariable Integer id,
                                @Valid @ModelAttribute("vehicleForm") VehicleFormDto form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("vehicleTypes", VehicleType.values());
            return "vehicles/form";
        }
        try {
            vehicleService.updateVehicle(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Dane pojazdu zostały zaktualizowane.");
            return "redirect:/vehicles";
        } catch (IllegalArgumentException e) {
            result.rejectValue("registrationNumber", "error.vehicle", e.getMessage());
            model.addAttribute("vehicleTypes", VehicleType.values());
            return "vehicles/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/deactivate")
    public String deactivateVehicle(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        vehicleService.deactivateVehicle(id);
        redirectAttributes.addFlashAttribute("successMessage", "Pojazd zostal wycofany z floty.");
        return "redirect:/vehicles";
    }
}

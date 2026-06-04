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
    public String listVehicles(Model model) {
        model.addAttribute("vehicles", vehicleService.getAllVehicles());
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
}

package pl.edu.pb.smuggling.cargo.controller;

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
import pl.edu.pb.smuggling.cargo.dto.WarehouseFormDto;
import pl.edu.pb.smuggling.cargo.model.Warehouse;
import pl.edu.pb.smuggling.cargo.service.WarehouseService;

@Controller
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping
    public String listWarehouses(Model model) {
        model.addAttribute("warehouseViews", warehouseService.findAllWithCapacityUsage());
        return "warehouses/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("warehouseForm", new WarehouseFormDto());
        return "warehouses/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createWarehouse(@Valid @ModelAttribute("warehouseForm") WarehouseFormDto form,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "warehouses/form";
        }
        warehouseService.createWarehouse(form);
        redirectAttributes.addFlashAttribute("successMessage", "Magazyn został utworzony.");
        return "redirect:/warehouses";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping("/{id}")
    public String showWarehouseDetails(@PathVariable Integer id, Model model) {
        model.addAttribute("warehouseView", warehouseService.getWarehouseCapacityView(id));
        return "warehouses/details";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        WarehouseFormDto dto = new WarehouseFormDto();
        dto.setId(warehouse.getId());
        dto.setName(warehouse.getName());
        dto.setLocation(warehouse.getLocation());
        dto.setMaxCapacity(warehouse.getMaxCapacity());
        dto.setActive(warehouse.isActive());

        model.addAttribute("warehouseForm", dto);
        return "warehouses/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateWarehouse(@PathVariable Integer id,
                                  @Valid @ModelAttribute("warehouseForm") WarehouseFormDto form,
                                  BindingResult result,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "warehouses/form";
        }
        warehouseService.updateWarehouse(id, form);
        redirectAttributes.addFlashAttribute("successMessage", "Magazyn został zaktualizowany.");
        return "redirect:/warehouses/" + id;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/delete")
    public String deleteWarehouse(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            warehouseService.deleteWarehouse(id);
            redirectAttributes.addFlashAttribute("successMessage", "Magazyn został usunięty.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie można usunąć magazynu.");
        }
        return "redirect:/warehouses";
    }
}

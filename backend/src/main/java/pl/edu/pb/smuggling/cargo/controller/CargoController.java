package pl.edu.pb.smuggling.cargo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.pb.smuggling.cargo.dto.CargoFormDto;
import pl.edu.pb.smuggling.cargo.model.Cargo;
import pl.edu.pb.smuggling.cargo.service.CargoService;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;

@Controller
@RequestMapping("/cargos")
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(new BigDecimal(text.replace(",", ".")));
                }
            }
        });
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping
    public String listCargos(Model model) {
        model.addAttribute("cargos", cargoService.findAll());
        return "cargos/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("cargoForm", new CargoFormDto());
        populateFormDictionaries(model);
        return "cargos/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createCargo(@Valid @ModelAttribute("cargoForm") CargoFormDto form,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "cargos/form";
        }
        try {
            cargoService.createCargo(form);
            redirectAttributes.addFlashAttribute("successMessage", "Ładunek został utworzony.");
            return "redirect:/cargos";
        } catch (IllegalArgumentException e) {
            result.reject("error.cargo", e.getMessage());
            populateFormDictionaries(model);
            return "cargos/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}")
    public String showCargoDetails(@PathVariable Integer id, Model model) {
        model.addAttribute("cargo", cargoService.getCargoById(id));
        return "cargos/details";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Cargo cargo = cargoService.getCargoById(id);
        CargoFormDto dto = new CargoFormDto();
        dto.setId(cargo.getId());
        dto.setName(cargo.getName());
        dto.setCargoTypeId(cargo.getCargoType().getId());
        dto.setPackagesCount(cargo.getPackagesCount());
        dto.setEstimatedValue(cargo.getEstimatedValue());
        dto.setWarehouseId(cargo.getWarehouse() != null ? cargo.getWarehouse().getId() : null);

        model.addAttribute("cargoForm", dto);
        populateFormDictionaries(model);
        return "cargos/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateCargo(@PathVariable Integer id,
                              @Valid @ModelAttribute("cargoForm") CargoFormDto form,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "cargos/form";
        }
        try {
            cargoService.updateCargo(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Ładunek został zaktualizowany.");
            return "redirect:/cargos/" + id;
        } catch (IllegalArgumentException e) {
            result.reject("error.cargo", e.getMessage());
            populateFormDictionaries(model);
            return "cargos/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/delete")
    public String deleteCargo(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            cargoService.deleteCargo(id);
            redirectAttributes.addFlashAttribute("successMessage", "Ładunek został usunięty.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Nie można usunąć ładunku.");
        }
        return "redirect:/cargos";
    }

    private void populateFormDictionaries(Model model) {
        model.addAttribute("cargoTypes", cargoService.getAllCargoTypes());
        model.addAttribute("warehouses", cargoService.getAllWarehouses());
    }
}

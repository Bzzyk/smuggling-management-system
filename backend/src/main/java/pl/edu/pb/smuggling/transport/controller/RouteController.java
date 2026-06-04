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
import pl.edu.pb.smuggling.transport.dto.RouteFormDto;
import pl.edu.pb.smuggling.transport.model.Route;
import pl.edu.pb.smuggling.transport.service.RouteService;

@Controller
@RequestMapping("/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public String listRoutes(Model model) {
        model.addAttribute("routes", routeService.getAllRoutes());
        return "routes/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("routeForm", new RouteFormDto());
        model.addAttribute("difficultyLevels", routeService.getAllDifficultyLevels());
        return "routes/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createRoute(@Valid @ModelAttribute("routeForm") RouteFormDto form,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("difficultyLevels", routeService.getAllDifficultyLevels());
            return "routes/form";
        }
        try {
            routeService.createRoute(form);
            redirectAttributes.addFlashAttribute("successMessage", "Trasa została pomyślnie dodana.");
            return "redirect:/routes";
        } catch (IllegalArgumentException e) {
            result.rejectValue("name", "error.route", e.getMessage());
            model.addAttribute("difficultyLevels", routeService.getAllDifficultyLevels());
            return "routes/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Route route = routeService.getRouteById(id);
        RouteFormDto dto = new RouteFormDto();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setStartPoint(route.getStartPoint());
        dto.setEndPoint(route.getEndPoint());
        dto.setDistanceKm(route.getDistanceKm());
        dto.setDifficultyLevelId(route.getDifficultyLevel().getId());
        dto.setDescription(route.getDescription());
        
        model.addAttribute("routeForm", dto);
        model.addAttribute("difficultyLevels", routeService.getAllDifficultyLevels());
        return "routes/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateRoute(@PathVariable Integer id,
                              @Valid @ModelAttribute("routeForm") RouteFormDto form,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("difficultyLevels", routeService.getAllDifficultyLevels());
            return "routes/form";
        }
        try {
            routeService.updateRoute(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Dane trasy zostały zaktualizowane.");
            return "redirect:/routes";
        } catch (IllegalArgumentException e) {
            result.rejectValue("name", "error.route", e.getMessage());
            model.addAttribute("difficultyLevels", routeService.getAllDifficultyLevels());
            return "routes/form";
        }
    }
}

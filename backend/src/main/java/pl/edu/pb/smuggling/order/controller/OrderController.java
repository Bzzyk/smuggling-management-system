package pl.edu.pb.smuggling.order.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pl.edu.pb.smuggling.order.dto.OrderFormDto;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;
import pl.edu.pb.smuggling.order.service.OrderService;
import pl.edu.pb.smuggling.user.model.User;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final pl.edu.pb.smuggling.user.repository.UserRepository userRepository;

    @org.springframework.web.bind.annotation.InitBinder
    public void initBinder(org.springframework.web.bind.WebDataBinder binder) {
        binder.registerCustomEditor(java.math.BigDecimal.class, new java.beans.PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(new java.math.BigDecimal(text.replace(",", ".")));
                }
            }
        });
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("orderForm", new OrderFormDto());
        populateFormDictionaries(model);
        return "orders/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/create")
    public String createOrder(@Valid @ModelAttribute("orderForm") OrderFormDto form,
                              BindingResult result,
                              Model model,
                              @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            System.out.println("====== VALIDATION ERRORS ======");
            result.getAllErrors().forEach(err -> System.out.println(err));
            System.out.println("===============================");
            populateFormDictionaries(model);
            return "orders/form";
        }
        try {
            User creator = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            orderService.createOrder(form, creator);
            redirectAttributes.addFlashAttribute("successMessage", "Zlecenie zostało utworzone.");
            return "redirect:/orders";
        } catch (IllegalArgumentException e) {
            result.reject("error.order", e.getMessage());
            populateFormDictionaries(model);
            return "orders/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        SmugglingOrder order = orderService.getOrderById(id);
        OrderFormDto dto = new OrderFormDto();
        dto.setId(order.getId());
        dto.setTitle(order.getTitle());
        dto.setDescription(order.getDescription());
        dto.setPlannedDate(order.getPlannedDate());
        dto.setStatusId(order.getStatus().getId());
        dto.setResponsibleUserId(order.getResponsibleUser() != null ? order.getResponsibleUser().getId() : null);
        dto.setEstimatedProfit(order.getEstimatedProfit());

        model.addAttribute("orderForm", dto);
        populateFormDictionaries(model);
        return "orders/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping("/{id}/edit")
    public String updateOrder(@PathVariable Integer id,
                              @Valid @ModelAttribute("orderForm") OrderFormDto form,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "orders/form";
        }
        try {
            orderService.updateOrder(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Zlecenie zostało zaktualizowane.");
            return "redirect:/orders/" + id;
        } catch (IllegalArgumentException e) {
            result.reject("error.order", e.getMessage());
            populateFormDictionaries(model);
            return "orders/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public String showOrderDetails(@PathVariable Integer id, Model model) {
        SmugglingOrder order = orderService.getOrderById(id);
        model.addAttribute("order", order);
        model.addAttribute("transports", orderService.getTransportsForOrder(id));
        return "orders/details";
    }

    private void populateFormDictionaries(Model model) {
        model.addAttribute("statuses", orderService.getAllStatuses());
        model.addAttribute("responsibleUsers", orderService.getAvailableResponsibleUsers());
    }
}

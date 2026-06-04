package pl.edu.pb.smuggling.payment.controller;

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
import pl.edu.pb.smuggling.payment.dto.PaymentFormDto;
import pl.edu.pb.smuggling.payment.model.Payment;
import pl.edu.pb.smuggling.payment.service.PaymentService;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

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

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping
    public String listPayments(Model model) {
        model.addAttribute("payments", paymentService.findAll());
        return "payments/list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("paymentForm", new PaymentFormDto());
        populateFormDictionaries(model);
        return "payments/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @PostMapping("/create")
    public String createPayment(@Valid @ModelAttribute("paymentForm") PaymentFormDto form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "payments/form";
        }
        try {
            paymentService.createPayment(form);
            redirectAttributes.addFlashAttribute("successMessage", "Płatność została utworzona.");
            return "redirect:/payments";
        } catch (IllegalArgumentException e) {
            result.reject("error.payment", e.getMessage());
            populateFormDictionaries(model);
            return "payments/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping("/{id}")
    public String showPaymentDetails(@PathVariable Integer id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        model.addAttribute("payment", payment);
        return "payments/details";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Payment payment = paymentService.getPaymentById(id);
        PaymentFormDto dto = new PaymentFormDto();
        dto.setId(payment.getId());
        dto.setOrderId(payment.getOrder().getId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentType(payment.getPaymentType());
        dto.setStatusId(payment.getStatus().getId());
        dto.setPaymentDate(payment.getPaymentDate());
        dto.setDescription(payment.getDescription());

        model.addAttribute("paymentForm", dto);
        populateFormDictionaries(model);
        return "payments/form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @PostMapping("/{id}/edit")
    public String updatePayment(@PathVariable Integer id,
                                @Valid @ModelAttribute("paymentForm") PaymentFormDto form,
                                BindingResult result,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            populateFormDictionaries(model);
            return "payments/form";
        }
        try {
            paymentService.updatePayment(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Płatność została zaktualizowana.");
            return "redirect:/payments/" + id;
        } catch (IllegalArgumentException e) {
            result.reject("error.payment", e.getMessage());
            populateFormDictionaries(model);
            return "payments/form";
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'ACCOUNTANT')")
    @PostMapping("/{id}/delete")
    public String deletePayment(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Płatność została usunięta.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payments";
    }

    private void populateFormDictionaries(Model model) {
        model.addAttribute("orders", paymentService.getAllOrders());
        model.addAttribute("statuses", paymentService.getAllStatuses());
        model.addAttribute("paymentTypes", List.of("KOSZT", "PRZYCHOD", "PROWIZJA"));
    }
}

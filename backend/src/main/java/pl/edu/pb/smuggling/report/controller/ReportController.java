package pl.edu.pb.smuggling.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.report.service.ReportService;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping
    public String reportsIndex() {
        return "redirect:/reports/profit";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping("/profit")
    public String profitReport(Model model) {
        model.addAttribute("rows", reportService.getProfitReport());
        return "reports/profit";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'ACCOUNTANT')")
    @GetMapping("/warehouse-stock")
    public String warehouseStockReport(Model model) {
        model.addAttribute("rows", reportService.getWarehouseStockReport());
        return "reports/warehouse-stock";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/risk")
    public String riskReport(Model model) {
        model.addAttribute("rows", reportService.getRiskReport());
        return "reports/risk";
    }
}

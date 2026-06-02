package pl.edu.pb.smuggling.cargo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.cargo.service.WarehouseService;

@Controller
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;

    @org.springframework.web.bind.annotation.GetMapping
    public String listWarehouses(org.springframework.ui.Model model) {
        model.addAttribute("warehouses", warehouseService.findAll());
        return "warehouses/list";
    }
}

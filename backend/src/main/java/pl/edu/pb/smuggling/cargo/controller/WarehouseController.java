package pl.edu.pb.smuggling.cargo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.RequestMapping;
import pl.edu.pb.smuggling.cargo.service.WarehouseService;

@Controller
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {
    private final WarehouseService warehouseService;
}

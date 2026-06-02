package pl.edu.pb.smuggling.cargo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.RequestMapping;
import pl.edu.pb.smuggling.cargo.service.CargoService;

@Controller
@RequestMapping("/cargos")
@RequiredArgsConstructor
public class CargoController {
    private final CargoService cargoService;
}

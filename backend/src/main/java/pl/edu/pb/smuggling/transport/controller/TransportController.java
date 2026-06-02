package pl.edu.pb.smuggling.transport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.transport.service.TransportService;

@Controller
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportController {
    private final TransportService transportService;

    @org.springframework.web.bind.annotation.GetMapping
    public String listTransports(org.springframework.ui.Model model) {
        model.addAttribute("transports", transportService.findAll());
        return "transports/list";
    }
}

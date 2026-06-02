package pl.edu.pb.smuggling.transport.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.RequestMapping;
import pl.edu.pb.smuggling.transport.service.TransportService;

@Controller
@RequestMapping("/transports")
@RequiredArgsConstructor
public class TransportController {
    private final TransportService transportService;
}

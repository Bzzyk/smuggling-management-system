package pl.edu.pb.smuggling.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.RequestMapping;
import pl.edu.pb.smuggling.order.service.OrderService;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
}

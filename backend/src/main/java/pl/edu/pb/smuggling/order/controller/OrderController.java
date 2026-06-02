package pl.edu.pb.smuggling.order.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.edu.pb.smuggling.order.service.OrderService;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @org.springframework.web.bind.annotation.GetMapping
    public String listOrders(org.springframework.ui.Model model) {
        model.addAttribute("orders", orderService.findAll());
        return "orders/list";
    }
}

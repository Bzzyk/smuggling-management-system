package pl.edu.pb.smuggling.order.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pb.smuggling.order.dto.OrderDto;
import pl.edu.pb.smuggling.order.dto.OrderFormDto;
import pl.edu.pb.smuggling.order.dto.OrderStatusDto;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;
import pl.edu.pb.smuggling.order.service.OrderService;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderRestController {
    private final OrderService orderService;
    private final SmugglingOrderRepository orderRepository;
    private final UserRepository userRepository;

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.findAll().stream()
                .map(OrderDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS', 'SMUGGLER')")
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(OrderDto.fromEntity(orderService.getOrderById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PostMapping
    public ResponseEntity<Void> createOrder(@Valid @RequestBody OrderFormDto request,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            User creator = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
            orderService.createOrder(request, creator);
            return ResponseEntity.status(201).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> updateOrder(@PathVariable Integer id,
                                                @Valid @RequestBody OrderFormDto request) {
        try {
            orderService.updateOrder(id, request);
            return ResponseEntity.ok(OrderDto.fromEntity(orderService.getOrderById(id)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        if (!orderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        orderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'BOSS')")
    @GetMapping("/statuses")
    public ResponseEntity<List<OrderStatusDto>> getOrderStatuses() {
        List<OrderStatusDto> statuses = orderService.getAllStatuses().stream()
                .map(OrderStatusDto::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(statuses);
    }
}

package pl.edu.pb.smuggling.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final SmugglingOrderRepository orderRepository;

    public java.util.List<pl.edu.pb.smuggling.order.model.SmugglingOrder> findAll() {
        return orderRepository.findAll();
    }
}

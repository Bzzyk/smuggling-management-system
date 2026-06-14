package pl.edu.pb.smuggling.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.order.dto.OrderFormDto;
import pl.edu.pb.smuggling.order.model.OrderStatus;
import pl.edu.pb.smuggling.order.repository.OrderStatusRepository;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;
import pl.edu.pb.smuggling.transport.model.Transport;
import pl.edu.pb.smuggling.transport.repository.TransportRepository;
import pl.edu.pb.smuggling.user.model.User;
import pl.edu.pb.smuggling.user.repository.UserRepository;

import pl.edu.pb.smuggling.common.service.AuditLogService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final SmugglingOrderRepository orderRepository;
    private final OrderStatusRepository statusRepository;
    private final UserRepository userRepository;
    private final TransportRepository transportRepository;
    private final AuditLogService auditLogService;

    public List<pl.edu.pb.smuggling.order.model.SmugglingOrder> findAll() {
        return orderRepository.findAll();
    }

    public Page<pl.edu.pb.smuggling.order.model.SmugglingOrder> findAll(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    public pl.edu.pb.smuggling.order.model.SmugglingOrder getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zlecenia o ID: " + id));
    }

    public List<OrderStatus> getAllStatuses() {
        return statusRepository.findAll();
    }

    public List<User> getAvailableResponsibleUsers() {
        return userRepository.findAll();
    }

    public List<Transport> getTransportsForOrder(Integer orderId) {
        return transportRepository.findByOrderId(orderId);
    }

    @Transactional
    public void createOrder(OrderFormDto dto, User creator) {
        pl.edu.pb.smuggling.order.model.SmugglingOrder order = new pl.edu.pb.smuggling.order.model.SmugglingOrder();
        order.setCreatedBy(creator);
        updateOrderFromDto(order, dto);
        order = orderRepository.save(order);
        auditLogService.logAction("orders", order.getId(), "CREATE", null, orderToMap(order));
    }

    @Transactional
    public void updateOrder(Integer id, OrderFormDto dto) {
        pl.edu.pb.smuggling.order.model.SmugglingOrder order = getOrderById(id);
        Map<String, Object> oldState = orderToMap(order);
        updateOrderFromDto(order, dto);
        orderRepository.save(order);
        auditLogService.logAction("orders", order.getId(), "UPDATE", oldState, orderToMap(order));
    }

    private void updateOrderFromDto(pl.edu.pb.smuggling.order.model.SmugglingOrder order, OrderFormDto dto) {
        order.setTitle(dto.getTitle());
        order.setDescription(dto.getDescription());
        order.setPlannedDate(dto.getPlannedDate());
        order.setEstimatedProfit(dto.getEstimatedProfit());

        OrderStatus status = statusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono statusu zlecenia"));
        order.setStatus(status);

        if (dto.getResponsibleUserId() != null) {
            User respUser = userRepository.findById(dto.getResponsibleUserId())
                    .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono osoby odpowiedzialnej"));
            order.setResponsibleUser(respUser);
        } else {
            order.setResponsibleUser(null);
        }
    }

    private Map<String, Object> orderToMap(pl.edu.pb.smuggling.order.model.SmugglingOrder order) {
        if (order == null) return null;
        Map<String, Object> map = new HashMap<>();
        map.put("title", order.getTitle());
        map.put("description", order.getDescription());
        map.put("plannedDate", order.getPlannedDate());
        map.put("estimatedProfit", order.getEstimatedProfit());
        map.put("status", order.getStatus() != null ? order.getStatus().getName() : null);
        map.put("responsibleUser", order.getResponsibleUser() != null ? order.getResponsibleUser().getUsername() : null);
        return map;
    }
}

package pl.edu.pb.smuggling.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;
import pl.edu.pb.smuggling.order.repository.SmugglingOrderRepository;
import pl.edu.pb.smuggling.payment.dto.PaymentFormDto;
import pl.edu.pb.smuggling.payment.model.Payment;
import pl.edu.pb.smuggling.payment.model.PaymentStatus;
import pl.edu.pb.smuggling.payment.repository.PaymentRepository;
import pl.edu.pb.smuggling.payment.repository.PaymentStatusRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final SmugglingOrderRepository orderRepository;

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment getPaymentById(Integer id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono płatności o ID: " + id));
    }

    public List<PaymentStatus> getAllStatuses() {
        return paymentStatusRepository.findAll();
    }

    public List<SmugglingOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void createPayment(PaymentFormDto dto) {
        Payment payment = new Payment();
        updatePaymentFromDto(payment, dto);
        paymentRepository.save(payment);
    }

    @Transactional
    public void updatePayment(Integer id, PaymentFormDto dto) {
        Payment payment = getPaymentById(id);
        updatePaymentFromDto(payment, dto);
        paymentRepository.save(payment);
    }

    @Transactional
    public void deletePayment(Integer id) {
        if (!paymentRepository.existsById(id)) {
            throw new IllegalArgumentException("Nie znaleziono płatności o ID: " + id);
        }
        paymentRepository.deleteById(id);
    }

    private void updatePaymentFromDto(Payment payment, PaymentFormDto dto) {
        SmugglingOrder order = orderRepository.findById(dto.getOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono zlecenia"));
        PaymentStatus status = paymentStatusRepository.findById(dto.getStatusId())
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono statusu płatności"));

        payment.setOrder(order);
        payment.setAmount(dto.getAmount());
        payment.setPaymentType(dto.getPaymentType());
        payment.setStatus(status);
        payment.setPaymentDate(dto.getPaymentDate());
        payment.setDescription(dto.getDescription());
    }
}

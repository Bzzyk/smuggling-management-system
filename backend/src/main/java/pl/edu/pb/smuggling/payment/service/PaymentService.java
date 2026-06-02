package pl.edu.pb.smuggling.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pb.smuggling.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
}

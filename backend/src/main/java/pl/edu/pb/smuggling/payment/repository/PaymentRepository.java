package pl.edu.pb.smuggling.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pb.smuggling.payment.model.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}

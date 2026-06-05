package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pb.smuggling.transport.model.Transport;

public interface TransportRepository extends JpaRepository<Transport, Integer> {
    java.util.List<Transport> findByOrderId(Integer orderId);

    @Query("""
            SELECT t
            FROM Transport t
            WHERE t.order.createdBy.id = :userId
               OR t.order.responsibleUser.id = :userId
            """)
    java.util.List<Transport> findVisibleForBoss(@Param("userId") Integer userId);

    @Query("""
            SELECT DISTINCT t
            FROM Transport t
            JOIN SmugglerAssignment sa ON sa.transport = t
            WHERE sa.smuggler.userId = :userId
            """)
    java.util.List<Transport> findVisibleForSmuggler(@Param("userId") Integer userId);
}

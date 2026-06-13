package pl.edu.pb.smuggling.transport.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.edu.pb.smuggling.transport.model.Transport;

import java.time.LocalDate;

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
              AND sa.active = true
            """)
    java.util.List<Transport> findVisibleForSmuggler(@Param("userId") Integer userId);

    @Query(
            value = """
                    SELECT DISTINCT t
                    FROM Transport t
                    LEFT JOIN SmugglerAssignment sa ON sa.transport = t
                    WHERE (
                        :admin = true
                        OR (:boss = true AND (
                            t.order.createdBy.id = :userId
                            OR t.order.responsibleUser.id = :userId
                        ))
                        OR (:smuggler = true AND sa.smuggler.userId = :userId AND sa.active = true)
                    )
                    AND (:statusFilterEnabled = false OR t.status.name = :statusName)
                    AND (:dateFromFilterEnabled = false OR t.transportDate >= :dateFrom)
                    AND (:dateToFilterEnabled = false OR t.transportDate <= :dateTo)
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT t)
                    FROM Transport t
                    LEFT JOIN SmugglerAssignment sa ON sa.transport = t
                    WHERE (
                        :admin = true
                        OR (:boss = true AND (
                            t.order.createdBy.id = :userId
                            OR t.order.responsibleUser.id = :userId
                        ))
                        OR (:smuggler = true AND sa.smuggler.userId = :userId AND sa.active = true)
                    )
                    AND (:statusFilterEnabled = false OR t.status.name = :statusName)
                    AND (:dateFromFilterEnabled = false OR t.transportDate >= :dateFrom)
                    AND (:dateToFilterEnabled = false OR t.transportDate <= :dateTo)
                    """
    )
    Page<Transport> findVisibleTransports(
            @Param("userId") Integer userId,
            @Param("admin") boolean admin,
            @Param("boss") boolean boss,
            @Param("smuggler") boolean smuggler,
            @Param("statusFilterEnabled") boolean statusFilterEnabled,
            @Param("statusName") String statusName,
            @Param("dateFromFilterEnabled") boolean dateFromFilterEnabled,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateToFilterEnabled") boolean dateToFilterEnabled,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );
}

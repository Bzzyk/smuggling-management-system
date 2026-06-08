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
                        OR (:smuggler = true AND sa.smuggler.userId = :userId)
                    )
                    AND (:statusName IS NULL OR t.status.name = :statusName)
                    AND (:dateFrom IS NULL OR t.transportDate >= :dateFrom)
                    AND (:dateTo IS NULL OR t.transportDate <= :dateTo)
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
                        OR (:smuggler = true AND sa.smuggler.userId = :userId)
                    )
                    AND (:statusName IS NULL OR t.status.name = :statusName)
                    AND (:dateFrom IS NULL OR t.transportDate >= :dateFrom)
                    AND (:dateTo IS NULL OR t.transportDate <= :dateTo)
                    """
    )
    Page<Transport> findVisibleTransports(
            @Param("userId") Integer userId,
            @Param("admin") boolean admin,
            @Param("boss") boolean boss,
            @Param("smuggler") boolean smuggler,
            @Param("statusName") String statusName,
            @Param("dateFrom") LocalDate dateFrom,
            @Param("dateTo") LocalDate dateTo,
            Pageable pageable
    );
}

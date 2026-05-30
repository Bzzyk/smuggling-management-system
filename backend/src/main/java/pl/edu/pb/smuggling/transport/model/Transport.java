package pl.edu.pb.smuggling.transport.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transports")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private SmugglingOrder order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id")
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private TransportStatus status;

    @Column(name = "start_location", nullable = false, length = 100)
    private String startLocation;

    @Column(nullable = false, length = 100)
    private String destination;

    @Column(name = "transport_date", nullable = false)
    private LocalDate transportDate;

    @Column(name = "planned_arrival_date")
    private LocalDate plannedArrivalDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

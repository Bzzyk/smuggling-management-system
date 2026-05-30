package pl.edu.pb.smuggling.cargo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "warehouse_stock")
public class WarehouseStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", nullable = false)
    private Cargo cargo;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    void prePersist() {
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}

package pl.edu.pb.smuggling.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "smuggler_profiles")
public class SmugglerProfile {

    @Id
    @Column(name = "user_id")
    private Integer userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "experience_level", nullable = false, length = 20)
    private String experienceLevel;

    @Column(name = "completed_transports_count", nullable = false)
    private int completedTransportsCount;

    @Column(name = "failed_transports_count", nullable = false)
    private int failedTransportsCount;

    @Column(nullable = false)
    private boolean active = true;

    @Column(columnDefinition = "TEXT")
    private String notes;
}

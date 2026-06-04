package pl.edu.pb.smuggling.order.dto;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.order.model.SmugglingOrder;
import pl.edu.pb.smuggling.user.model.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderDto {
    private Integer id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private LocalDate plannedDate;
    private LocalDateTime completedAt;
    private OrderStatusDto status;
    private UserSummaryDto createdBy;
    private UserSummaryDto responsibleUser;
    private BigDecimal estimatedProfit;

    public static OrderDto fromEntity(SmugglingOrder order) {
        if (order == null) {
            return null;
        }
        return OrderDto.builder()
                .id(order.getId())
                .title(order.getTitle())
                .description(order.getDescription())
                .createdAt(order.getCreatedAt())
                .plannedDate(order.getPlannedDate())
                .completedAt(order.getCompletedAt())
                .status(OrderStatusDto.fromEntity(order.getStatus()))
                .createdBy(UserSummaryDto.fromEntity(order.getCreatedBy()))
                .responsibleUser(UserSummaryDto.fromEntity(order.getResponsibleUser()))
                .estimatedProfit(order.getEstimatedProfit())
                .build();
    }

    @Data
    @Builder
    public static class UserSummaryDto {
        private Integer id;
        private String username;
        private String firstName;
        private String lastName;

        public static UserSummaryDto fromEntity(User user) {
            if (user == null) {
                return null;
            }
            return UserSummaryDto.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .build();
        }
    }
}

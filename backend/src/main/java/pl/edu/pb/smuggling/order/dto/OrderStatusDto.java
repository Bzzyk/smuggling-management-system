package pl.edu.pb.smuggling.order.dto;

import lombok.Builder;
import lombok.Data;
import pl.edu.pb.smuggling.order.model.OrderStatus;

@Data
@Builder
public class OrderStatusDto {
    private Integer id;
    private String name;
    private String description;

    public static OrderStatusDto fromEntity(OrderStatus status) {
        if (status == null) {
            return null;
        }
        return OrderStatusDto.builder()
                .id(status.getId())
                .name(status.getName())
                .description(status.getDescription())
                .build();
    }
}

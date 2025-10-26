package microopshub.orderservice.common.dto;

import java.util.List;

public record CreateOrderRequest(Long userId, List<OrderItemRequest> items) {
    public record OrderItemRequest(Long productId, int quantity) {
    }
}

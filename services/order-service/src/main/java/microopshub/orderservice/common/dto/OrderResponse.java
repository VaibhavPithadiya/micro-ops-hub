package microopshub.orderservice.common.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(Long orderId, Long userId, BigDecimal totalPrice, Instant createdAt, List<Item> items) implements ApiResponse {
    public record Item(Long productId, int quantity, BigDecimal unitPrice) {
    }
}

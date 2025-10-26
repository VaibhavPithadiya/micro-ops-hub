package microopshub.orderservice.common.dto;

import microopshub.orderservice.entity.Product;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String description, BigDecimal price,
                              Integer stock) implements ApiResponse {
    public ProductResponse(Product product) {
        this(product.getId(), product.getName(), product.getDescription(), product.getPrice(), product.getStock());
    }
}

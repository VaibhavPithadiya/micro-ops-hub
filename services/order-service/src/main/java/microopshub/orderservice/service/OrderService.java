package microopshub.orderservice.service;

import microopshub.orderservice.common.dto.CreateOrderRequest;
import microopshub.orderservice.common.dto.OrderResponse;
import microopshub.orderservice.common.exception.FunctionalException;
import microopshub.orderservice.common.exception.ProductNotFoundException;
import microopshub.orderservice.common.exception.UserNotFoundException;
import microopshub.orderservice.configuration.AppConfig;
import microopshub.orderservice.entity.Order;
import microopshub.orderservice.entity.OrderItem;
import microopshub.orderservice.entity.Product;
import microopshub.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final RestTemplate restTemplate;
    private final AppConfig appConfig;

    public OrderService(OrderRepository orderRepository, ProductService productService, RestTemplate restTemplate, AppConfig appConfig) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        this.restTemplate = restTemplate;
        this.appConfig = appConfig;
    }

    public OrderResponse placeOrder(CreateOrderRequest orderRequest) throws UserNotFoundException {
        // Validate user exists via user-service
        String userServiceUrl = appConfig.userService().url() + "/users/" + orderRequest.userId();
        try {
            var userResponse = restTemplate.getForObject(userServiceUrl, Object.class);
            if (userResponse == null) throw new UserNotFoundException("User not found.");
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("User not found.");
        }

        Order order = new Order();
        order.setUserId(orderRequest.userId());

        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        List<OrderItem> items = orderRequest.items().stream().map(itemReq -> {
            try {
                Product product = productService.getProductById(itemReq.productId());

                if (product.getStock() < itemReq.quantity()) {
                    throw new FunctionalException("Insufficient stock for product: " + product.getName());
                }

                // Reduce stock and save
                product.setStock(product.getStock() - itemReq.quantity());
                productService.saveProduct(product); // <-- persist stock change

                // Create order item
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProductId(product.getId());
                item.setQuantity(itemReq.quantity());
                item.setUnitPrice(product.getPrice());

                // Add to total
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.quantity()));
                total.updateAndGet(t -> t.add(itemTotal));

                return item;
            } catch (ProductNotFoundException | FunctionalException exception) {
                throw new RuntimeException(exception);
            }
        }).toList();

        order.setItems(items);
        order.setTotalPrice(total.get());

        Order saved = orderRepository.save(order);
        return toDto(saved);
    }


    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::toDto)
                .toList();
    }

    private OrderResponse toDto(Order order) {
        var items = order.getItems().stream()
                .map(i -> new OrderResponse.Item(i.getProductId(), i.getQuantity(), i.getUnitPrice()))
                .toList();
        return new OrderResponse(
                order.getId(), order.getUserId(),
                order.getTotalPrice(), order.getCreatedAt(), items
        );
    }
}

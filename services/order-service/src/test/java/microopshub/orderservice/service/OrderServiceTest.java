package microopshub.orderservice.service;

import microopshub.orderservice.common.dto.CreateOrderRequest;
import microopshub.orderservice.common.dto.OrderResponse;
import microopshub.orderservice.common.exception.ProductNotFoundException;
import microopshub.orderservice.common.exception.UserNotFoundException;
import microopshub.orderservice.configuration.AppConfig;
import microopshub.orderservice.entity.Order;
import microopshub.orderservice.entity.OrderItem;
import microopshub.orderservice.entity.Product;
import microopshub.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductService productService;

    @Mock
    private AppConfig appConfig;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void placeOrder_shouldCreateOrderAndReturnResponse() throws UserNotFoundException, ProductNotFoundException {
        // Mock data
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new CreateOrderRequest.OrderItemRequest(1L, 2)));

        when(appConfig.userService()).thenReturn(new AppConfig.UserService("http://localhost"));

        // Mock external service calls
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object()); // Mock user validation
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(10));
        product.setStock(100);
        when(productService.getProductById(1L)).thenReturn(product);

        // Mock saving the order
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(1L);
        savedOrder.setTotalPrice(BigDecimal.valueOf(20)); // 2 * product price

        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.valueOf(10));
        savedOrder.setItems(List.of(item));

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        // Call the method
        OrderResponse response = orderService.placeOrder(request);

        // Assertions
        assertNotNull(response);
        assertEquals(1L, response.orderId());
        assertEquals(1L, response.userId());
        assertEquals(BigDecimal.valueOf(20), response.totalPrice());
        assertEquals(1, response.items().size());
        assertEquals(1L, response.items().get(0).productId());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void placeOrder_userNotFound_shouldThrowException() {
        // Mock data
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new CreateOrderRequest.OrderItemRequest(1L, 2)));

        when(appConfig.userService()).thenReturn(new AppConfig.UserService("http://localhost"));

        // Simulate user not found
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(null);

        // Call the method and verify exception
        assertThrows(UserNotFoundException.class, () -> orderService.placeOrder(request));
    }

    @Test
    void placeOrder_productNotFound_shouldThrowException() throws ProductNotFoundException {
        // Mock data
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new CreateOrderRequest.OrderItemRequest(1L, 2)));

        // Simulate user being found
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        // Simulate product not found
        when(productService.getProductById(1L)).thenThrow(new ProductNotFoundException("Product not found"));

        // Call the method and verify exception
        assertThrows(RuntimeException.class, () -> orderService.placeOrder(request));
    }

    @Test
    void placeOrder_insufficientStock_shouldThrowException() throws ProductNotFoundException {
        // Mock data
        CreateOrderRequest request = new CreateOrderRequest(1L, List.of(new CreateOrderRequest.OrderItemRequest(1L, 200)));

        // Simulate user being found
        when(restTemplate.getForObject(anyString(), eq(Object.class))).thenReturn(new Object());

        // Simulate product with insufficient stock
        Product product = new Product();
        product.setId(1L);
        product.setPrice(BigDecimal.valueOf(10));
        product.setStock(100); // Only 100 in stock
        when(productService.getProductById(1L)).thenReturn(product);

        // Call the method and verify exception
        assertThrows(RuntimeException.class, () -> orderService.placeOrder(request));
    }

    @Test
    void getOrdersByUserId_shouldReturnOrders() {
        // Mock data
        OrderItem item1 = new OrderItem();
        item1.setProductId(101L);
        item1.setQuantity(2);
        item1.setUnitPrice(BigDecimal.valueOf(10));

        OrderItem item2 = new OrderItem();
        item2.setProductId(102L);
        item2.setQuantity(1);
        item2.setUnitPrice(BigDecimal.valueOf(30));

        Order order1 = new Order();
        order1.setId(1L);
        order1.setUserId(1L);
        order1.setTotalPrice(BigDecimal.valueOf(20));
        order1.setItems(List.of(item1));

        Order order2 = new Order();
        order2.setId(2L);
        order2.setUserId(1L);
        order2.setTotalPrice(BigDecimal.valueOf(30));
        order2.setItems(List.of(item2));

        when(orderRepository.findByUserId(1L)).thenReturn(Arrays.asList(order1, order2));

        // Call the method
        List<OrderResponse> orders = orderService.getOrdersByUserId(1L);

        // Assertions
        assertEquals(2, orders.size());

        // Check order 1
        assertEquals(1L, orders.get(0).userId());
        assertEquals(1, orders.get(0).items().size());
        assertEquals(101L, orders.get(0).items().get(0).productId());

        // Check order 2
        assertEquals(1L, orders.get(1).userId());
        assertEquals(1, orders.get(1).items().size());
        assertEquals(102L, orders.get(1).items().get(0).productId());

        verify(orderRepository, times(1)).findByUserId(1L);
    }
}
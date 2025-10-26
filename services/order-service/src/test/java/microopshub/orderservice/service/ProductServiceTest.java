package microopshub.orderservice.service;

import microopshub.orderservice.common.dto.ProductRequest;
import microopshub.orderservice.common.dto.ProductResponse;
import microopshub.orderservice.common.exception.ProductNotFoundException;
import microopshub.orderservice.entity.Product;
import microopshub.orderservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_shouldSaveAndReturnProductResponse() {
        ProductRequest request = new ProductRequest("Test Product", "Test Description", BigDecimal.valueOf(10.5), 100);

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName(request.name());
        savedProduct.setDescription(request.description());
        savedProduct.setPrice(request.price());
        savedProduct.setStock(request.stock());

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.addProduct(request);

        assertNotNull(response);
        assertEquals(savedProduct.getId(), response.id());
        assertEquals(savedProduct.getName(), response.name());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAllProducts_shouldReturnListOfProductResponses() {
        Product product1 = new Product();
        product1.setId(1L);
        product1.setName("Product 1");

        Product product2 = new Product();
        product2.setId(2L);
        product2.setName("Product 2");

        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));

        List<ProductResponse> responses = productService.getAllProducts();

        assertEquals(2, responses.size());
        assertEquals("Product 1", responses.get(0).name());
        assertEquals("Product 2", responses.get(1).name());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductById_existingId_shouldReturnProduct() throws ProductNotFoundException {
        Product product = new Product();
        product.setId(1L);
        product.setName("Existing Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(product.getId(), result.getId());
        assertEquals(product.getName(), result.getName());
    }

    @Test
    void getProductById_nonExistingId_shouldThrowException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void saveProduct_shouldCallRepositorySave() {
        Product product = new Product();
        product.setId(1L);
        product.setName("To Save");

        when(productRepository.save(product)).thenReturn(product);

        Product saved = productService.saveProduct(product);

        assertEquals(product, saved);
        verify(productRepository, times(1)).save(product);
    }
}
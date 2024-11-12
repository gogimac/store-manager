package ro.ing.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ro.ing.api.entity.Product;
import ro.ing.api.exception.ProductNotFoundException;
import ro.ing.api.exception.UnauthorizedAccessException;
import ro.ing.api.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OpenAiService aiService;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product = new Product();
        product.setId(1L);
        product.setName("Sample Product");
        product.setPrice(BigDecimal.valueOf(19.99));
        product.setDescription("Sample Description");
    }

    @Test
    void testAddProduct() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.addProduct(product);

        assertNotNull(savedProduct);
        assertEquals(product.getName(), savedProduct.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testFindProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.findProduct(1L);

        assertTrue(foundProduct.isPresent());
        assertEquals(product.getName(), foundProduct.get().getName());
    }

    @Test
    void testFindProductNotFound() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.findProduct(2L);

        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testUpdateProductPartially() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        product.setDescription("Updated Description");
        productService.updateProductPartially(1L, Collections.singletonMap("description", "Updated Description"));

        assertEquals("Updated Description", product.getDescription());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testUpdateProductPartiallyThrowsException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProductPartially(1L, Collections.singletonMap("description", "Updated Description"));
        });
    }

    @Test
    void testGetAllProducts_withPagination() {
        // Given
        Product product = new Product();
        product.setName("Sample Product");
        product.setPrice(BigDecimal.valueOf(19.99));
        product.setDescription("A sample description");

        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findAll(any(PageRequest.class))).thenReturn(productPage);

        // When
        Page<Product> products = productService.getAllProducts(PageRequest.of(0, 10));

        // Then
        assertEquals(1, products.getTotalElements(), "Total elements should be 1");
        assertEquals(1, products.getContent().size(), "Page content size should be 1");
        assertEquals("Sample Product", products.getContent().get(0).getName(), "Product name should match");
    }

    @Test
    void testChangePrice() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.changePrice(1L, BigDecimal.valueOf(25.99));

        assertEquals(BigDecimal.valueOf(25.99), updatedProduct.getPrice());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void testChangePriceThrowsException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> {
            productService.changePrice(1L, BigDecimal.valueOf(25.99));
        });
    }

    @Test
    void testGetAllProducts() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<Product> products = productService.getAllProducts();

        assertEquals(1, products.size());
        assertEquals(product.getName(), products.get(0).getName());
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Mocked authority as a string (e.g., "ADMIN")
        String authority = "ADMIN";

        // Act
        productService.deleteProduct(1L, authority);

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProductThrowsException() {
        when(productRepository.existsById(anyLong())).thenReturn(false);
        String authority = "USER";
        assertThrows(UnauthorizedAccessException.class, () -> {
            productService.deleteProduct(1L, authority);
        });
    }

    @Test
    void testSearchProducts() {
        when(productRepository.findByNameContainingIgnoreCase(anyString())).thenReturn(List.of(product));

        List<Product> products = productService.searchProducts("Sample", null, null);

        assertEquals(1, products.size());
        assertEquals("Sample Product", products.get(0).getName());
    }

    @Test
    void testAddProductWithDescription() {
        when(aiService.generateText(anyString())).thenReturn("Generated Description");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        product.setDescription(null);
        Product savedProduct = productService.addProductWithDescription(product);

        assertEquals("Generated Description", savedProduct.getDescription());
        verify(aiService, times(1)).generateText(anyString());
        verify(productRepository, times(1)).save(product);
    }
}

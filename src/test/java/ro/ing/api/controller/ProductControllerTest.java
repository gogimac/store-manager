package ro.ing.api.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import ro.ing.api.entity.Product;
import ro.ing.api.exception.DatabaseOperationException;
import ro.ing.api.exception.ProductAlreadyExistsException;
import ro.ing.api.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product sampleProduct;

    @BeforeEach
    public void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Sample Product");
        sampleProduct.setPrice(BigDecimal.valueOf(19.99));
        sampleProduct.setDescription("Sample Description");
    }

    @Test
    public void testAddProduct() throws Exception {
        Mockito.when(productService.addProduct(any(Product.class))).thenReturn(sampleProduct);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Product\", \"price\": 19.99, \"description\": \"Sample Description\"}").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sample Product"));
    }



    @Test
    public void testAddProduct_ProductAlreadyExists_ThrowsException() throws Exception {
        // Arrange: mock the service to throw ProductAlreadyExistsException
        Mockito.when(productService.addProduct(any(Product.class)))
                .thenThrow(new ProductAlreadyExistsException("Product with name 'Sample Product' already exists."));

        // Act & Assert: perform the request and expect a 409 Conflict status and appropriate error message
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Product\", \"price\": 19.99, \"description\": \"Sample Description\"}")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Product with name 'Sample Product' already exists."));
    }

    @Test
    public void testUnauthorizedAccess() throws Exception {
        // Attempt to access the add product endpoint without authentication
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Product\", \"price\": 19.99, \"description\": \"Sample Description\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testDatabaseOperationException() throws Exception {
        // Arrange: Mock the service to throw a DatabaseOperationException
        Mockito.when(productService.addProduct(any(Product.class)))
                .thenThrow(new DatabaseOperationException("Database operation failed"));

        // Act & Assert: Expect a 500 Internal Server Error with JSON response
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Product\", \"price\": 19.99, \"description\": \"Sample Description\"}")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.error").value("Database operation failed"));
    }

    @Test
    public void testUnauthorizedAccessWithInvalidCredentials() throws Exception {
        // Attempt to access the add product endpoint with invalid credentials
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Product\", \"price\": 19.99, \"description\": \"Sample Description\"}")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("invalidUser", "invalidPassword")))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void testAddProduct_ValidProduct() throws Exception {
        // Arrange: set up a valid product
        Mockito.when(productService.addProduct(any(Product.class))).thenReturn(sampleProduct);

        // Act & Assert: test successful addition
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Sample Product\", \"price\": 19.99, \"description\": \"Sample Description\"}").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Sample Product"));
    }

    @Test
    public void testAddProduct_InvalidProduct_ThrowsException() throws Exception {
        // Act & Assert: expect an exception for an invalid product price
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Invalid Product\", \"price\": -5.00, \"description\": \"Negative price\"}")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Product price must be greater than or equal to zero."));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Arrange
        Mockito.when(productService.getAllProducts()).thenReturn(List.of(sampleProduct));

        // Act & Assert
        mockMvc.perform(get("/api/products/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isOk())  // Expecting 200 OK for GET request
                .andExpect(jsonPath("$[0].name").value("Sample Product"));
    }


    @Test
    public void testFindProductById() throws Exception {
        Mockito.when(productService.findProduct(1L)).thenReturn(Optional.of(sampleProduct));

        mockMvc.perform(get("/api/products/1").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Sample Product"));
    }

    @Test
    public void testFindProductById_NotFound() throws Exception {
        Mockito.when(productService.findProduct(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/products/1").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testChangePrice() throws Exception {
        BigDecimal newPrice = BigDecimal.valueOf(25.99);
        sampleProduct.setPrice(newPrice);
        Mockito.when(productService.changePrice(eq(1L), eq(newPrice))).thenReturn(sampleProduct);

        mockMvc.perform(put("/api/products/1/price")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"newPrice\": 25.99}").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(25.99));
    }

    @Test
    public void testUpdateProductPartially() throws Exception {
        sampleProduct.setDescription("Updated Description");
        Mockito.when(productService.updateProductPartially(eq(1L), any(Map.class))).thenReturn(sampleProduct);

        mockMvc.perform(patch("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\": \"Updated Description\"}").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Updated Description"));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/api/products/1")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testSearchProducts() throws Exception {
        Mockito.when(productService.searchProducts("Sample", null, null)).thenReturn(List.of(sampleProduct));

        mockMvc.perform(get("/api/products/search?name=Sample").with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sample Product"));
    }
}


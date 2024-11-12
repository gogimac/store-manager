package ro.ing.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ro.ing.api.entity.Product;
import ro.ing.api.exception.InvalidProductDataException;
import ro.ing.api.exception.ProductNotFoundException;
import ro.ing.api.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    /**
     * Constructor for ProductController.
     * @param productService service layer that handles product-related operations
     */
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Adds a new product.
     * @param product the product to be added
     * @return ResponseEntity with the saved product and HTTP status CREATED
     */
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        logger.info("Adding new product: {}", product.getName());
        if (product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidProductDataException("Product price must be greater than or equal to zero.");
        }

        Product savedProduct = productService.addProduct(product);
        logger.info("Product added successfully with ID: {}", savedProduct.getId());
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }

    /**
     * Retrieves all products with pagination.
     * @param page the page number (0-based)
     * @param size the number of items per page
     * @return ResponseEntity with a paginated list of products
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Retrieving products - Page: {}, Size: {}", page, size);

        if (page == 0 && size == 10) {
            // Default behavior: return all products
            List<Product> allProducts = productService.getAllProducts();
            logger.info("Retrieved all products without pagination, total: {}", allProducts.size());
            return ResponseEntity.ok(allProducts);
        } else {
            // Pagination behavior
            Pageable pageable = PageRequest.of(page, size);
            Page<Product> products = productService.getAllProducts(pageable);
            logger.info("Retrieved {} products in current page", products.getNumberOfElements());
            return ResponseEntity.ok(products);
        }
    }


    /**
     * Retrieves a product by its ID.
     * @param id the ID of the product to retrieve
     * @return ResponseEntity with the found product or an exception if not found
     * @throws ProductNotFoundException if the product is not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> findProduct(@PathVariable Long id) {
        logger.info("Retrieving product with ID: {}", id);
        Product product = productService.findProduct(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        logger.info("Product retrieved successfully: {}", product.getName());
        return ResponseEntity.ok(product);
    }

    /**
     * Updates the price of a specific product.
     * @param id the ID of the product to update
     * @param newPrice the new price to set for the product
     * @return ResponseEntity with the updated product
     */
    @PutMapping("/{id}/price")
    public ResponseEntity<Product> changePrice(@PathVariable Long id, @RequestBody Map<String, BigDecimal> request) {
        BigDecimal newPrice = request.get("newPrice");
        logger.info("Updating price of product with ID: {} to {}", id, newPrice);
        Product updatedProduct = productService.changePrice(id, newPrice);
        logger.info("Price updated successfully for product with ID: {}", id);
        return ResponseEntity.ok(updatedProduct);
    }


    /**
     * Partially updates a product's attributes.
     * @param id the ID of the product to update
     * @param updates a map containing the fields to update and their new values
     * @return ResponseEntity with the updated product
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> updateProductPartially(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        logger.info("Partially updating product with ID: {}", id);
        Product updatedProduct = productService.updateProductPartially(id, updates);
        logger.info("Product with ID: {} updated successfully", id);
        return ResponseEntity.ok(updatedProduct);
    }

    /**
     * Deletes a product by its ID.
     * @param id the ID of the product to delete
     * @return ResponseEntity with no content status
     * @throws ProductNotFoundException if the product is not found
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("USER"); // Default role if no authority is found

        logger.info("Deleting product with ID: {} by user with role: {}", id, userRole);
        productService.deleteProduct(id, userRole);
        logger.info("Product with ID: {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Searches for products based on optional criteria.
     * @param name optional name substring to search for
     * @param minPrice optional minimum price filter
     * @param maxPrice optional maximum price filter
     * @return ResponseEntity with a list of products matching the criteria
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        logger.info("Searching for products with criteria - Name: {}, Min Price: {}, Max Price: {}", name, minPrice, maxPrice);
        List<Product> products = productService.searchProducts(name, minPrice, maxPrice);
        logger.info("Found {} products matching the search criteria", products.size());
        return ResponseEntity.ok(products);
    }
}

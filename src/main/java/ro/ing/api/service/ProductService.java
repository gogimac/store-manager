package ro.ing.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.ing.api.entity.Product;
import ro.ing.api.exception.ProductAlreadyExistsException;
import ro.ing.api.exception.ProductNotFoundException;
import ro.ing.api.exception.UnauthorizedAccessException;
import ro.ing.api.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class ProductService {

    private static final Logger logger = Logger.getLogger(ProductService.class.getName());

    private final ProductRepository productRepository;
    private final OpenAiService aiService;

    /**
     * Constructor for ProductService.
     * @param productRepository the repository used for accessing product data
     * @param aiService the OpenAI service used for generating product descriptions
     */
    @Autowired
    public ProductService(ProductRepository productRepository, OpenAiService aiService) {
        this.productRepository = productRepository;
        this.aiService = aiService;
        logger.log(Level.INFO, "ProductService initialized");
    }

    /**
     * Adds a new product to the database.
     * @param product the product to be added
     * @return the saved product with an assigned ID
     */
    public Product addProduct(Product product) {
        logger.log(Level.INFO, "Adding new product: {0}", product.getName());
        if (productRepository.existsByName(product.getName())) {
            throw new ProductAlreadyExistsException("Product with name '" + product.getName() + "' already exists.");
        }

        Product savedProduct = productRepository.save(product);
        logger.log(Level.INFO, "Product added successfully with ID: {0}", savedProduct.getId());
        return savedProduct;
    }

    /**
     * Finds a product by its ID.
     * @param id the ID of the product to be retrieved
     * @return an Optional containing the found product, or empty if not found
     */
    public Optional<Product> findProduct(Long id) {
        logger.log(Level.INFO, "Finding product with ID: {0}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            logger.log(Level.INFO, "Product found with ID: {0}", id);
        } else {
            logger.log(Level.WARNING, "Product not found with ID: {0}", id);
        }
        return product;
    }

    /**
     * Partially updates a product's attributes.
     * @param id the ID of the product to update
     * @param updates a map containing the fields to update and their new values
     * @return the updated product
     * @throws ProductNotFoundException if the product with the specified ID is not found
     */
    public Product updateProductPartially(Long id, Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    product.setName((String) value);
                    break;
                case "description":
                    product.setDescription((String) value);
                    break;
                case "price":
                    product.setPrice(new BigDecimal(value.toString()));
                    break;
                // Add cases for other fields as needed
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        return productRepository.save(product);
    }

    /**
     * Retrieves a paginated list of products.
     * @param pageable the pagination information
     * @return a page of products
     */
    // Method with pagination support
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    /**
     * Updates the price of an existing product.
     * @param id the ID of the product to update
     * @param newPrice the new price to set for the product
     * @return the updated product with the new price
     * @throws ProductNotFoundException if the product with the specified ID is not found
     */
    public Product changePrice(Long id, BigDecimal newPrice) {
        logger.log(Level.INFO, "Changing price of product with ID: {0} to {1}", new Object[]{id, newPrice});
        Product product = productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.log(Level.SEVERE, "Product not found with ID: {0}", id);
                    return new ProductNotFoundException("Product not found");
                });
        product.setPrice(newPrice);
        Product updatedProduct = productRepository.save(product);
        logger.log(Level.INFO, "Price updated successfully for product with ID: {0}", updatedProduct.getId());
        return updatedProduct;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * Deletes a product by its ID.
     * @param id the ID of the product to delete
     * @throws ProductNotFoundException if the product with the specified ID does not exist
     */
    public void deleteProduct(Long id,  String userRol) {
        logger.log(Level.INFO, "Deleting product with ID: {0}", id);
        if (!userRol.equals("ADMIN")) {
            throw new UnauthorizedAccessException("You do not have permission to delete products.");
        }
        if (!productRepository.existsById(id)) {
            logger.log(Level.SEVERE, "Product not found with ID: {0}", id);
            throw new ProductNotFoundException("Product not found");
        }

        productRepository.deleteById(id);
        logger.log(Level.INFO, "Product deleted successfully with ID: {0}", id);
    }

    /**
     * Searches for products based on optional criteria: name, minimum price, and maximum price.
     * @param name optional name substring to search for (case-insensitive)
     * @param minPrice optional minimum price for filtering results
     * @param maxPrice optional maximum price for filtering results
     * @return a list of products matching the specified criteria
     */
    public List<Product> searchProducts(String name, BigDecimal minPrice, BigDecimal maxPrice) {
        logger.log(Level.INFO, "Searching products with criteria - Name: {0}, Min Price: {1}, Max Price: {2}",
                new Object[]{name, minPrice, maxPrice});

        List<Product> products;
        if (name != null && minPrice != null && maxPrice != null) {
            products = productRepository.findByNameAndPriceBetween(name, minPrice, maxPrice);
        } else if (name != null) {
            products = productRepository.findByNameContainingIgnoreCase(name);
        } else if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            products = productRepository.findAll();
        }

        logger.log(Level.INFO, "Found {0} products matching criteria", products.size());
        return products;
    }

    /**
     * Adds a new product to the store with an AI-generated description.
     * @param product The product to be added
     * @return The saved product with the AI-generated description
     */
    public Product addProductWithDescription(Product product) {
        logger.log(Level.INFO, "Generating description for product: {0}", product.getName());
        String prompt = "Generate a compelling description for a product named " + product.getName();
        String description = aiService.generateText(prompt);
        product.setDescription(description);

        Product savedProduct = productRepository.save(product);
        logger.log(Level.INFO, "Product with AI-generated description added successfully. ID: {0}", savedProduct.getId());
        return savedProduct;
    }
}

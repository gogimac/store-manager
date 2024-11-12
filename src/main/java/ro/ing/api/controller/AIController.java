package ro.ing.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.ing.api.exception.ProductNotFoundException;
import ro.ing.api.entity.Product;
import ro.ing.api.service.OpenAiService;
import ro.ing.api.service.ProductService;
import ro.ing.api.service.RealTimeLoggerService;

@RestController
@RequestMapping("/api/ai/products")
public class AIController {

    private static final Logger logger = LoggerFactory.getLogger(AIController.class);

    private final OpenAiService openAiService;
    private final ProductService productService;
    private final RealTimeLoggerService realTimeLoggerService;

    /**
     * Constructor for AIController, injecting necessary services for product operations and AI functionality.
     * @param openAiService OpenAI service for AI-based operations
     * @param productService Service layer handling product-related operations
     * @param realTimeLoggerService Service for broadcasting logs to WebSocket clients
     */
    @Autowired
    public AIController(OpenAiService openAiService, ProductService productService, RealTimeLoggerService realTimeLoggerService) {
        this.openAiService = openAiService;
        this.productService = productService;
        this.realTimeLoggerService = realTimeLoggerService;
    }

    /**
     * Adds a new product to the store with an AI-generated description.
     * @param product The product object to be added, with basic details provided in the request body
     * @return ResponseEntity containing the saved Product object with an AI-generated description and HTTP status
     */
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        logger.info("Received request to add product with AI-generated description.");
        realTimeLoggerService.sendLog("Received request to add product with AI-generated description.");
        Product savedProduct = productService.addProductWithDescription(product);
        logger.info("Product saved successfully with ID: {}", savedProduct.getId());
        realTimeLoggerService.sendLog("Product saved successfully with ID: " + savedProduct.getId());
        return ResponseEntity.ok(savedProduct);
    }

    /**
     * Retrieves a product by its unique ID, utilizing AI-enhanced methods for data handling.
     * @param id The unique identifier of the product to be retrieved
     * @return ResponseEntity containing the Product object if found, or throws ProductNotFoundException if not found
     * @throws ProductNotFoundException if the product with the specified ID does not exist in the database
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        logger.info("Received request to fetch product with ID: {}", id);
        realTimeLoggerService.sendLog("Received request to fetch product with ID: " + id);
        Product product = productService.findProduct(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        logger.info("Product found with ID: {}", id);
        realTimeLoggerService.sendLog("Product found with ID: " + id);
        return ResponseEntity.ok(product);
    }

    /**
     * Endpoint to generate AI-based text given a prompt.
     * @param prompt The input prompt for generating an AI response
     * @return generated text as ResponseEntity
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateText(@RequestParam String prompt) {
        logger.info("Received AI text generation request with prompt: {}", prompt);
        realTimeLoggerService.sendLog("Received AI text generation request with prompt: " + prompt);
        String aiResponse = openAiService.generateText(prompt);
        logger.info("AI text generated successfully.");
        realTimeLoggerService.sendLog("AI text generated successfully.");
        return ResponseEntity.ok(aiResponse);
    }
}

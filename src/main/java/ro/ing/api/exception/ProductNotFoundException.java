/**
 * ProductNotFoundException is a custom exception used to indicate that a specific product was not found.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This exception is thrown when a requested product cannot be located in the database.
 * It is annotated with @ResponseStatus to automatically return a 404 Not Found status code
 * when the exception is thrown, enhancing readability and simplifying error handling in RESTful APIs.
 */

package ro.ing.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)  // Returns 404 status code when this exception is thrown
public class ProductNotFoundException extends RuntimeException {

    /**
     * Constructs a new ProductNotFoundException with a specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public ProductNotFoundException(String message) {
        super(message);
    }
}

/**
 * ProductAlreadyExistsException is thrown when there is an attempt to add a product
 * that already exists in the database.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This exception is annotated with @ResponseStatus to automatically return a 409 Conflict status code
 * when the exception is thrown, which indicates that the request could not be completed due to a conflict.
 */

package ro.ing.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)  // Returns 409 status code when this exception is thrown
public class ProductAlreadyExistsException extends RuntimeException {

    /**
     * Constructs a new ProductAlreadyExistsException with a specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}

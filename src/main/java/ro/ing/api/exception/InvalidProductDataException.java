/**
 * InvalidProductDataException is thrown when the provided product data fails validation checks.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This exception is annotated with @ResponseStatus to automatically return a 400 Bad Request status code
 * when the exception is thrown, indicating that the request could not be processed due to client error.
 */

package ro.ing.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)  // Returns 400 status code when this exception is thrown
public class InvalidProductDataException extends RuntimeException {

    /**
     * Constructs a new InvalidProductDataException with a specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public InvalidProductDataException(String message) {
        super(message);
    }
}

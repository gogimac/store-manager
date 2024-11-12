/**
 * DatabaseOperationException is thrown when there is a failure during a database operation.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This exception is annotated with @ResponseStatus to automatically return a 500 Internal Server Error status code
 * when the exception is thrown, signaling that the server encountered an error it could not recover from.
 */

package ro.ing.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)  // Returns 500 status code when this exception is thrown
public class DatabaseOperationException extends RuntimeException {

    /**
     * Constructs a new DatabaseOperationException with a specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public DatabaseOperationException(String message) {
        super(message);
    }
}

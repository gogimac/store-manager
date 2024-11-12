/**
 * UnauthorizedAccessException is thrown when an unauthorized user attempts to access a restricted resource.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This exception is annotated with @ResponseStatus to automatically return a 403 Forbidden status code
 * when the exception is thrown, indicating that the server understands the request but refuses to authorize it.
 */

package ro.ing.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)  // Returns 403 status code when this exception is thrown
public class UnauthorizedAccessException extends RuntimeException {

    /**
     * Constructs a new UnauthorizedAccessException with a specified detail message.
     * @param message the detail message explaining the reason for the exception
     */
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}

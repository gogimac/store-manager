/**
 * StoreManagementApplication serves as the entry point for the Store Management API.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This class initializes and launches the Spring Boot application, enabling auto-configuration,
 * component scanning, and JPA repository support for managing product data in the database.
 */

package ro.ing.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication  // Enables Spring Boot's autoconfiguration and component scanning
@EnableJpaRepositories(basePackages = "ro.ing.api.repository")  // Enables JPA repositories in the specified package
@EnableJpaAuditing
public class StoreManagementApplication {

    /**
     * The main method serves as the entry point for the Spring Boot application.
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(StoreManagementApplication.class, args);  // Launches the application
    }
}

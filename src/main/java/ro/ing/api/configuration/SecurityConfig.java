/**
 * SecurityConfig class configures the security settings for the Store Management API.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the purpose of the Chapter Lead Backend position at ING.
 *
 * This configuration sets up basic authentication with in-memory users for demonstration purposes,
 * including specific authorization requirements for different API endpoints.
 *
 * Key Features:
 * - Configures HTTP security settings with role-based access control.
 * - Provides in-memory user details for authentication.
 */

package ro.ing.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configures the security filter chain for HTTP security.
     * - Disables CSRF protection for simplicity (not recommended for production).
     * - Restricts access to `/api/products/**` endpoints to users with the `ADMIN` role.
     * - Requires authentication for all other requests.
     * - Enables HTTP Basic authentication.
     *
     * @param http HttpSecurity instance for configuring web-based security
     * @return SecurityFilterChain configured filter chain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfigurer -> csrfConfigurer.disable()) // Disable CSRF for simplicity; not recommended in production.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/products/**").hasRole("ADMIN") // Restrict product management to ADMIN role.
                        .requestMatchers("/api/ai/products/**").hasAnyRole("USER", "ADMIN") // Allow both USER and ADMIN roles for AI endpoints.
                        .anyRequest().authenticated() // Require authentication for all other requests.
                )
                .httpBasic(httpBasicConfigurer -> {}); // Enable basic HTTP authentication.
        return http.build();
    }


    /**
     * Provides an in-memory user details service for authentication.
     * - Creates a `user` with the `USER` role.
     * - Creates an `admin` with both `USER` and `ADMIN` roles.
     * - Passwords are stored using plain encoding (`noop`) for demonstration purposes.
     *
     * @return UserDetailsService an in-memory user details manager with predefined users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("USER", "ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }
}

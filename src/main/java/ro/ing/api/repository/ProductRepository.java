/**
 * ProductRepository provides database access methods for the Product entity in the Store Management API.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This repository interface extends JpaRepository, leveraging Spring Data JPA's built-in methods for CRUD operations
 * and adding custom query methods for specific search capabilities.
 */

package ro.ing.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.ing.api.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    /**
     * Finds products by a name substring, ignoring case.
     * @param name partial or full name of the product
     * @return list of products with names that contain the specified substring, case-insensitive
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Finds products within a specified price range.
     * @param minPrice minimum price of the product
     * @param maxPrice maximum price of the product
     * @return list of products with prices between minPrice and maxPrice
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Finds products by a name substring within a specified price range.
     * @param name partial or full name of the product
     * @param minPrice minimum price of the product
     * @param maxPrice maximum price of the product
     * @return list of products with names containing the specified substring and prices within the specified range
     */
    List<Product> findByNameAndPriceBetween(String name, BigDecimal minPrice, BigDecimal maxPrice);
}

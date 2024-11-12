/**
 * Product entity represents a product in the Store Management API, including its ID, name, price, and description.
 * Developed by Goga Octavian Gabriel on 11.11.2024 for the Chapter Lead Backend position at ING.
 *
 * This entity is mapped to a database table using JPA annotations, enabling CRUD operations on product data.
 * Lombok annotations (@Getter, @Setter) are used for automatic generation of getter and setter methods.
 */

package ro.ing.api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Product {

    /**
     * Unique identifier for each product.
     * Generated automatically by the database using the IDENTITY strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of the product, which should be descriptive and unique within the catalog.
     */
    private String name;

    /**
     * Price of the product, represented as a BigDecimal for precision.
     */
    private BigDecimal price;

    /**
     * Description of the product, providing additional details.
     */
    private String description;

    /**
     * Timestamp indicating when the product was created.
     * <p>
     * Automatically set when the entity is persisted for the first time.
     * </p>
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    /**
     * Timestamp indicating the last time the product was updated.
     * <p>
     * Automatically updated whenever the entity is modified.
     * </p>
     */
    @LastModifiedDate
    private LocalDateTime updatedDate;
}

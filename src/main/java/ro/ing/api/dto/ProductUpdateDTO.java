package ro.ing.api.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;

/**
 * Data Transfer Object for updating Product entities.
 * Contains fields that can be updated: name, price, and description.
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ProductUpdateDTO {

    private String name;
    private BigDecimal price;
    private String description;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}

package com.alejandroacg.dataverseappshowcase.repositories;

import com.alejandroacg.dataverseappshowcase.models.Franchise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface providing CRUD operations and query helpers
 * for {@link Franchise} entities.
 *
 * <p>Spring Data JPA automatically implements query methods following
 * naming conventions, allowing the application to retrieve franchises
 * by specific attributes without custom SQL.</p>
 */
@Repository
public interface FranchiseRepository extends JpaRepository<Franchise, Long> {

    /**
     * Retrieves a {@link Franchise} by its English name, ignoring case sensitivity.
     *
     * @param name the English name to search for
     * @return an {@link Optional} with the matching Franchise if found,
     *         or empty if no match exists
     */
    Optional<Franchise> findByEnglishNameIgnoreCase(String name);

    /**
     * Retrieves a {@link Franchise} by its original name, ignoring case sensitivity.
     *
     * @param name the original name to search for
     * @return an {@link Optional} with the matching Franchise if found,
     *         or empty if no match exists
     */
    Optional<Franchise> findByOriginalNameIgnoreCase(String name);
}

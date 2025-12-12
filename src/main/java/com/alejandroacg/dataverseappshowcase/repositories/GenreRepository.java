package com.alejandroacg.dataverseappshowcase.repositories;

import com.alejandroacg.dataverseappshowcase.models.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface providing CRUD and query operations for {@link Genre} entities.
 *
 * <p>This interface leverages Spring Data JPA to automatically generate
 * common persistence methods and supports custom query definitions through
 * method naming conventions.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Retrieves a {@link Genre} by name, ignoring case sensitivity.
     *
     * @param name the genre name to look up
     * @return an {@link Optional} containing the matching Genre if found,
     *         or empty if no match exists
     */
    Optional<Genre> findByNameIgnoreCase(String name);
}

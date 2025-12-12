package com.alejandroacg.dataverseappshowcase.services;

import com.alejandroacg.dataverseappshowcase.models.Genre;
import com.alejandroacg.dataverseappshowcase.repositories.GenreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.alejandroacg.dataverseappshowcase.utils.EntityType.GENRE;

/**
 * Service layer providing CRUD and lookup operations for {@link Genre} entities.
 *
 * <p>This class abstracts the persistence layer (Spring Data JPA) and exposes
 * a clean, business-oriented API to the rest of the application. All validation
 * beyond simple uniqueness checks is handled at the controller/UI level.
 *
 * <p>The service supports:
 * <ul>
 *     <li>Creation of new Genre records</li>
 *     <li>Updating existing Genre entries</li>
 *     <li>Paged retrieval for search interfaces</li>
 *     <li>Uniqueness checks by name</li>
 *     <li>Deletion and ID-based lookup</li>
 * </ul>
 */
@Service
public class GenreService {

    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    /**
     * Creates and persists a new {@link Genre} entity.
     *
     * @param name  the display name of the genre
     * @param color the associated hex color code
     * @return the persisted Genre instance
     */
    public Genre createGenre(String name, String color) {
        Genre.GenreBuilder builder = Genre.builder()
                .name(name)
                .color(color);

        return genreRepository.save(builder.build());
    }

    /**
     * Updates an existing {@link Genre} entity.
     *
     * @param id    the ID of the genre to modify
     * @param name  the new name
     * @param color the new associated color
     * @return the updated persisted entity
     * @throws IllegalArgumentException if no genre exists with the given ID
     */
    public Genre updateGenre(Long id, String name, String color) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(GENRE + " not found with id: " + id));

        genre.setName(name);
        genre.setColor(color);

        return genreRepository.save(genre);
    }

    /**
     * Checks whether a Genre with the given name already exists (case insensitive).
     *
     * @param name the name to check
     * @return true if a genre with this name already exists
     */
    public boolean existsByName(String name) {
        return genreRepository.findByNameIgnoreCase(name).isPresent();
    }

    /**
     * Retrieves a paginated list of all Genre entries.
     *
     * <p>Used primarily by search interfaces in the UI.
     *
     * @param pageable pagination and sorting parameters
     * @return a Page containing Genre entities
     */
    public Page<Genre> getAllGenresPage(Pageable pageable) {
        return genreRepository.findAll(pageable);
    }

    /**
     * Attempts to find a Genre by its unique ID.
     *
     * @param id the ID of the Genre
     * @return an Optional containing the Genre if found
     */
    public Optional<Genre> findById(Long id) {
        return genreRepository.findById(id);
    }

    /**
     * Deletes the Genre entry with the given ID.
     *
     * @param id the ID of the Genre to remove
     */
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}

package com.alejandroacg.dataverseappshowcase.services;

import com.alejandroacg.dataverseappshowcase.models.Franchise;
import com.alejandroacg.dataverseappshowcase.repositories.FranchiseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.alejandroacg.dataverseappshowcase.utils.EntityType.FRANCHISE;

/**
 * Service layer providing CRUD, lookup, and pagination operations
 * for {@link Franchise} entities.
 *
 * <p>This class encapsulates the persistence logic behind a clean and
 * business-oriented API, allowing controllers and UI layers to remain
 * decoupled from repository implementations.
 *
 * <p>Supported operations include:
 * <ul>
 *     <li>Creating new Franchise entries</li>
 *     <li>Updating existing ones</li>
 *     <li>Checking uniqueness of English and original names</li>
 *     <li>Paginated retrieval for search interfaces</li>
 *     <li>ID-based lookup and deletion</li>
 * </ul>
 */
@Service
public class FranchiseService {

    private final FranchiseRepository franchiseRepository;

    @Autowired
    public FranchiseService(FranchiseRepository franchiseRepository) {
        this.franchiseRepository = franchiseRepository;
    }

    /**
     * Creates and persists a new {@link Franchise} entity.
     *
     * @param englishName       the English name of the franchise
     * @param originalName      the original or native name
     * @param description       optional descriptive text
     * @param profileImagePath  path to the stored profile image, if any
     * @return the persisted Franchise instance
     */
    public Franchise createFranchise(
            String englishName, String originalName, String description, String profileImagePath) {
        Franchise.FranchiseBuilder builder = Franchise.builder()
                .englishName(englishName)
                .originalName(originalName)
                .description(description)
                .profileImagePath(profileImagePath);

        return franchiseRepository.save(builder.build());
    }

    /**
     * Updates the fields of an existing {@link Franchise}.
     *
     * @param id                the unique ID of the franchise
     * @param englishName       new English name
     * @param originalName      new original name
     * @param description       new description
     * @param profileImagePath  new image path, or null to keep existing
     * @return the updated and saved Franchise object
     * @throws IllegalArgumentException if no franchise exists with the given ID
     */
    public Franchise updateFranchise(
            Long id, String englishName, String originalName, String description, String profileImagePath) {
        Franchise franchise = franchiseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(FRANCHISE + " not found with id: " + id));

        franchise.setEnglishName(englishName);
        franchise.setOriginalName(originalName);
        franchise.setDescription(description);
        franchise.setProfileImagePath(profileImagePath);

        return franchiseRepository.save(franchise);
    }

    /**
     * Checks whether a franchise with the given English name already exists.
     *
     * @param name the name to check
     * @return true if a matching franchise exists (case-insensitive)
     */
    public boolean existsByEnglishName(String name) {
        return franchiseRepository.findByEnglishNameIgnoreCase(name).isPresent();
    }

    /**
     * Checks whether a franchise with the given original name already exists.
     *
     * @param name the original name
     * @return true if a matching franchise exists (case-insensitive)
     */
    public boolean existsByOriginalName(String name) {
        return franchiseRepository.findByOriginalNameIgnoreCase(name).isPresent();
    }

    /**
     * Retrieves a paginated list of all Franchise entries.
     *
     * @param pageable pagination and sorting descriptor
     * @return a Page of Franchise entities
     */
    public Page<Franchise> getAllFranchisesPage(Pageable pageable) {
        return franchiseRepository.findAll(pageable);
    }

    /**
     * Retrieves a Franchise by ID.
     *
     * @param id the identifier of the Franchise
     * @return an Optional containing the entity if found
     */
    public Optional<Franchise> findById(Long id) {
        return franchiseRepository.findById(id);
    }

    /**
     * Removes the Franchise entry with the given ID.
     *
     * @param id the ID of the Franchise to delete
     */
    public void deleteFranchise(Long id) {
        franchiseRepository.deleteById(id);
    }
}

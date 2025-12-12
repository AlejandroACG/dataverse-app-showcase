package com.alejandroacg.dataverseappshowcase.search;

import com.alejandroacg.dataverseappshowcase.utils.EntityType;

/**
 * Factory responsible for instantiating the appropriate
 * {@link SearchHandler} implementation for a given {@link EntityType}.
 *
 * <p>This class centralizes all handler creation logic, ensuring that
 * search controllers remain fully decoupled from concrete handler
 * implementations. Whenever a new searchable entity is added to the
 * application, its corresponding handler should be registered here.
 */
public class SearchHandlerFactory {

    /**
     * Returns the concrete {@link SearchHandler} associated with the
     * specified entity type.
     *
     * @param type the entity type requesting a search handler
     * @return a corresponding SearchHandler implementation
     * @throws IllegalArgumentException if no SearchHandler exists for the given type
     */
    public static SearchHandler getHandler(EntityType type) {
        return switch (type) {
            case FRANCHISE -> new FranchiseSearchHandler();
            case GENRE -> new GenreSearchHandler();

            // Add additional handlers here for future entity types:
            // case MEDIUM -> new MediumSearchHandler();
            // case CHARACTER -> new CharacterSearchHandler();

            default -> throw new IllegalArgumentException("No SearchHandler implemented for: " + type);
        };
    }
}

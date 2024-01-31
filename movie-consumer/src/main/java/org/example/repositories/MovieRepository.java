package org.example.repositories;

import org.example.entities.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing Movie entities.
 */
public interface MovieRepository extends CrudRepository<Movie, Integer> {

    /**
     * Finds movies by exact title.
     *
     * @param title The title to search for.
     * @return List of movies with the specified title.
     */
    List<Movie> findByTitle(@Param("title") String title);

    /**
     * Finds movies by exact genres.
     *
     * @param genres The genres to search for.
     * @return List of movies with the specified genres.
     */
    List<Movie> findByGenres(@Param("genres") String title);

    /**
     * Finds movies by title containing the specified substring (case-insensitive).
     *
     * @param title The substring to search for in movie titles.
     * @return List of movies with titles containing the specified substring.
     */
    List<Movie> findByTitleContainingIgnoreCase(String title);

    /**
     * Finds movies by genres containing the specified substring (case-insensitive).
     *
     * @param genres The substring to search for in movie genres.
     * @return List of movies with genres containing the specified substring.
     */
    List<Movie> findByGenresContainingIgnoreCase(String genres);

    /**
     * Finds a movie by its ID.
     *
     * @param Id The ID of the movie.
     * @return An Optional containing the movie with the specified ID, or empty if not found.
     */
    Optional<Movie> findById(Integer Id);
}

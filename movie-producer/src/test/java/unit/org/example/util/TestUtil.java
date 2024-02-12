package org.example.util;

import org.example.records.Movie;
import org.example.records.MovieType;

/**
 * Utility class for testing purposes.
 */
public class TestUtil {

    /**
     * Generates a sample Movie record for testing.
     *
     * @return A Movie object with valid values.
     */
    public static Movie movieNewRecord() {
        return new Movie(1, MovieType.NEW, "Inception", "Sci-Fi");
    }

    /**
     * Generates a Movie record with invalid values for testing.
     *
     * @return A Movie object with null and empty values.
     */
    public static Movie movieRecordWithInvalidValues() {
        return new Movie(null, null, "", "");
    }

    /**
     * Generates a sample Movie record for testing movie update.
     *
     * @return A Movie object with valid values for an update operation.
     */
    public static Movie movieUpdateRecord() {
        return new Movie(2, MovieType.UPDATE, "Updated Movie", "Action");
    }

    /**
     * Generates a sample Movie record for testing movie deletion.
     *
     * @return A Movie object with valid values for a delete operation.
     */
    public static Movie movieDeleteRecord() {
        return new Movie(3, MovieType.DELETE, "Deleted Movie", "Drama");
    }

    /**
     * Generates a sample Movie record for testing searching movies by title.
     *
     * @return A Movie object with valid values for searching by title.
     */
    public static Movie movieSearchByTitleRecord() {
        return new Movie(-1, MovieType.GET, "Search Movie Title", "");
    }

    /**
     * Generates a sample Movie record for testing searching movies by genres.
     *
     * @return A Movie object with valid values for searching by genres.
     */
    public static Movie movieSearchByGenresRecord() {
        return new Movie(-2, MovieType.GET, "", "Search Movie Genres");
    }
}

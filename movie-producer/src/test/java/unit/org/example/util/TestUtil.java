package org.example.util;

import org.example.records.Movie;

/**
 * Utility class for testing purposes.
 */
public class TestUtil {

    /**
     * Generates a sample Movie record for testing.
     *
     * @return A Movie object with valid values.
     */
    public static Movie movieRecord() {
        return new Movie(1, "Inception", "Sci-Fi");
    }

    /**
     * Generates a Movie record with invalid values for testing.
     *
     * @return A Movie object with null and empty values.
     */
    public static Movie movieRecordWithInvalidValues() {
        return new Movie(null, "", "");
    }
}

package org.example.repositories;

import org.example.entities.Movie;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing Movie entities.
 */
public interface MovieRepository extends CrudRepository<Movie, Integer> {
}

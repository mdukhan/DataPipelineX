package org.example.repositories;

import org.example.entities.Movie;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

public interface MovieRepository extends CrudRepository<Movie, Integer> {
}

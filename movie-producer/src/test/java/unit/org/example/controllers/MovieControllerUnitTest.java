package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.records.Movie;
import org.example.services.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.example.util.TestUtil;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for the {@link MovieController} class.
 */
@WebMvcTest(MovieController.class)
class MovieControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectmapper;

    @MockBean
    MovieService movieService;

    /**
     * Test case for successful movie addition via POST request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void postMovie() throws Exception {

        Movie movie = TestUtil.movieRecord();

        String json = objectmapper.writeValueAsString(movie);
        when(movieService.sendMovie(isA(Movie.class))).thenReturn(null);

        //expect
        mockMvc.perform(post("/add/movie")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    /**
     * Test case for handling 4xx client errors during movie addition via POST request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void postMovie_4xx() throws Exception {
        //given

        Movie movie = TestUtil.movieRecordWithInvalidValues();

        String json = objectmapper.writeValueAsString(movie);
        when(movieService.sendMovie(isA(Movie.class))).thenReturn(null);
        //expect
        String expectedErrorMessage = "Please pass the MovieId";
        mockMvc.perform(post("/add/movie")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }
}
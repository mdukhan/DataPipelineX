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

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.mock.web.MockMultipartFile;

/**
 * Unit tests for the {@link MovieController} class.
 */
@WebMvcTest(MovieController.class)
class MovieControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MovieService movieService;

    /**
     * Test case for successful movie addition via POST request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void postMovie() throws Exception {

        Movie movie = TestUtil.movieNewRecord();

        String json = objectMapper.writeValueAsString(movie);
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

        String json = objectMapper.writeValueAsString(movie);
        when(movieService.sendMovie(isA(Movie.class))).thenReturn(null);
        //expect
        String expectedErrorMessage = "Please pass the MovieId";
        mockMvc.perform(post("/add/movie")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string(expectedErrorMessage));
    }

    /**
     * Test case for successful movie upload from CSV file via POST request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void postMoviesFromCSV() throws Exception {
        // Given
        Movie movie = TestUtil.movieNewRecord();
        String csvContent = "2,Jumanji (1995),Adventure|Children|Fantasy";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", csvContent.getBytes());
        // Expect
        when(movieService.parseMoviesCsvLine(any(String.class))).thenReturn(movie);
        mockMvc.perform(
                        multipart("/api/upload-csv-file") // Use multipart here for file upload
                                .file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(content().string("File processed successfully"));
    }


    /**
     * Test case for handling 4xx client errors during movie upload from CSV file via POST request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void postMoviesFromCSV_4xx() throws Exception {
        // Given
        String emptyString = "";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", emptyString.getBytes());
        // Expect
        mockMvc.perform(
                        multipart("/api/upload-csv-file").file(file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please select a file to upload"));
    }

    /**
     * Test case for handling movie update via PUT request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void updateMovie() throws Exception {
        // Given
        Movie movie = TestUtil.movieUpdateRecord();
        String json = objectMapper.writeValueAsString(movie);
        when(movieService.sendMovie(isA(Movie.class))).thenReturn(null);
        // Expect
        mockMvc.perform(put("/update/movie")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * Test case for handling movie deletion via DELETE request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void deleteMovie() throws Exception {
        // Given
        Movie movie = TestUtil.movieDeleteRecord();
        String json = objectMapper.writeValueAsString(movie);
        when(movieService.sendMovie(isA(Movie.class))).thenReturn(CompletableFuture.completedFuture(null));
        // Expect
        mockMvc.perform(delete("/delete/movie")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    /**
     * Test case for searching movies by genres via GET request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void searchMoviesByGenres() throws Exception {
        // Given
        when(movieService.getJsonResponse()).thenReturn("{}");
        // Expect
        mockMvc.perform(get("/get/movies/genres/Action")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }

    /**
     * Test case for searching movies by title via GET request.
     *
     * @throws Exception If an error occurs during the test.
     */
    @Test
    void searchMoviesByTitle() throws Exception {
        // Given
        when(movieService.getJsonResponse()).thenReturn("{}");
        // Expect
        mockMvc.perform(get("/get/movies/title/New Movie")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("{}"));
    }
}
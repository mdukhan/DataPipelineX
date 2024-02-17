package org.example.controllers;

import org.example.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@RestController
public class RatingController {


    private RatingService ratingService;

    public RatingController(RatingService ratingService){
        this.ratingService=ratingService;
    }

    @PostMapping("/api/upload-csv-file/ratings")
    public ResponseEntity<String> postRatingsFromCSV(@RequestParam("file") MultipartFile csvFile) {
        if (csvFile.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(csvFile.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ratingService.sendRatingRecord(ratingService.parseRatingCsvLine(line));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body("File processed successfully");
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing the file");
        }
    }
}

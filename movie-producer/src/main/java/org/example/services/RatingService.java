package org.example.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.producers.RecordsProducer;
import org.example.records.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class RatingService {


    private final RecordsProducer recordsProducer;

    public RatingService(RecordsProducer recordsProducer) {
        this.recordsProducer = recordsProducer;
    }


    public Rating parseRatingCsvLine(String csvLine) {
        String[] values = csvLine.split(",");
        // String regex = "[a-zA-Z]";
        //if (!values[0].matches(regex)) {
        if (values.length >= 3) {
            return new Rating(Integer.parseInt(values[0]), Integer.parseInt(values[1]), Double.parseDouble(values[2]), Integer.parseInt(values[3]));
        } else {
            // Handle invalid CSV lines appropriately
            throw new IllegalArgumentException("Invalid CSV line: " + csvLine);
        }
    }


    public void sendRatingRecord(Rating rating) throws JsonProcessingException {
        recordsProducer.sendRatingRecord(rating);
    }
}

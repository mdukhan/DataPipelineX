package org.example.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Rating {

    @Id
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "movieId")
    private Movie Movie;

    private Double rating;

    private Integer timeStamp;
}

package org.example.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Movie {

    @Id
    @GeneratedValue
    private Integer Id;
    @Enumerated(EnumType.STRING)
    private MovieType movieType;
    private String title;
    private String genres;
}

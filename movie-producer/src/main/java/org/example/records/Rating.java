package org.example.records;

import jakarta.validation.constraints.NotBlank;

public record Rating(
        @NotBlank
        Integer userId,
        @NotBlank
        Integer movieId,
        @NotBlank
        Double rating,
        @NotBlank
        Integer timeStamp
                      ) {
}

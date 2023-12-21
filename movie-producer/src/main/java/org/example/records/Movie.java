package org.example.records;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Movie(
        @NotNull
        Integer Id,
        @NotBlank
        String title,
        @NotBlank
        String genres
) {
}

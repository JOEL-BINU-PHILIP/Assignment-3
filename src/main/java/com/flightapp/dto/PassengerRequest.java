package com.flightapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String gender;

    @NotNull
    private Integer age;

    private String seatNumber;

    private String meal; // "VEG" or "NON-VEG"
}

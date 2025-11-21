package com.flightapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightInventoryRequest {
    @NotBlank
    @Pattern(
    	    regexp = "^[A-Z0-9]{3,10}$",
    	    message = "Flight number must be 3–10 characters (uppercase letters or digits)"
    	)
    private String flightNumber;

    @NotBlank
    private String fromPlace;

    @NotBlank
    private String toPlace;

    @NotNull
    private LocalDateTime departureTime;

    @NotNull
    private LocalDateTime arrivalTime;

    @NotNull
    private Double price;

    @NotNull
    @Min(1)
    private Integer totalSeats;

    // Airline info
    @NotBlank
    private String airlineName;

    private String airlineLogoUrl;
}


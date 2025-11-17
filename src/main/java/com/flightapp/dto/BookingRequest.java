package com.flightapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    @Email
    @NotBlank
    private String email;

    @NotNull
    @Min(1)
    private Integer numberOfSeats;

    @NotEmpty
    private List<PassengerRequest> passengers;
}

package com.flightapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder   // ⭐ REQUIRED FOR builder()
public class PassengerRequest {

    @NotBlank(message = "Passenger name cannot be blank")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Min(value = 1, message = "Age must be positive")
    private int age;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    @NotBlank(message = "Meal preference is required")
    private String meal;
}

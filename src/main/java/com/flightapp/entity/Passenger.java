package com.flightapp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Passenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // PRIMARY KEY -> REQUIRED!!

    @NotBlank(message = "Passenger name cannot be blank")
    private String name;

    @NotBlank(message = "Gender is required")
    private String gender;

    @Min(value = 1, message = "Age must be greater than 0")
    private int age;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    @NotBlank(message = "Meal preference is required")
    private String meal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;
}

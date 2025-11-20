package com.flightapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

@Entity
@Table(name = "flights")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Flight code / number
    private String flightNumber;

    private String fromPlace;
    private String toPlace;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private Double price;

    private Integer totalSeats;
    private Integer availableSeats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    private Airline airline;
}

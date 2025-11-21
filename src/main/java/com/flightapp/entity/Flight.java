package com.flightapp.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("flights")
public class Flight {
    @Id
    private Long id;

    private String flightNumber;
    private String fromPlace;
    private String toPlace;

    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;

    private Double price;

    private Integer totalSeats;
    private Integer availableSeats;

    // store airline id rather than object relation in R2DBC approach
    private Long airlineId;
}

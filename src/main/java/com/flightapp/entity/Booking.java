package com.flightapp.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("bookings")
public class Booking {
    @Id
    private Long id;

    private String pnr;
    private String email;

    private Long flightId;

    private Integer seatsBooked;
    private LocalDateTime bookingTime;

    private Boolean canceled;
    private LocalDateTime canceledAt;
}


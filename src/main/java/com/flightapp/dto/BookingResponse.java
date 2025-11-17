package com.flightapp.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private String pnr;
    private String email;
    private Long flightId;
    private Integer seatsBooked;
    private LocalDateTime bookingTime;
    private Boolean canceled;
    private List<PassengerRequest> passengers;
}

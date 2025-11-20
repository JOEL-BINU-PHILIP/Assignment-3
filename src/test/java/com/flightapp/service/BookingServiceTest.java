package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.PassengerRequest;
import com.flightapp.entity.*;
import com.flightapp.exception.ApiException;
import com.flightapp.repository.*;
import com.flightapp.service.impl.BookingServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock FlightRepository flightRepository;
    @Mock PassengerRepository passengerRepository;

    @InjectMocks BookingServiceImpl bookingService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void bookTicket_success() {

        Long flightId = 1L;

        Airline airline = Airline.builder()
                .id(1L)
                .name("AirIndia")
                .logoUrl("logo.png")
                .build();

        Flight flight = Flight.builder()
                .id(flightId)
                .airline(airline)
                .availableSeats(10)
                .totalSeats(10)
                .price(5000.0)
                .fromPlace("BLR")
                .toPlace("BOM")
                .departureTime(LocalDateTime.now().plusDays(3))
                .arrivalTime(LocalDateTime.now().plusDays(3).plusHours(2))
                .build();

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));

        when(bookingRepository.save(any())).thenAnswer(i -> {
            Booking b = (Booking) i.getArguments()[0];
            b.setId(100L);
            return b;
        });

        BookingRequest req = BookingRequest.builder()
                .email("abc@example.com")
                .numberOfSeats(2)
                .passengers(List.of(
                        PassengerRequest.builder().name("P1").gender("M").age(25).meal("VEG").seatNumber("1A").build(),
                        PassengerRequest.builder().name("P2").gender("F").age(26).meal("NONVEG").seatNumber("1B").build()
                ))
                .build();

        var response = bookingService.bookTicket(flightId, req);

        assertNotNull(response.getPnr());
        assertEquals(2, response.getSeatsBooked());
        verify(flightRepository, times(1)).save(any());
    }

    @Test
    void bookTicket_insufficientSeats() {
        Long flightId = 1L;

        Flight flight = Flight.builder()
                .id(flightId)
                .availableSeats(1)
                .departureTime(LocalDateTime.now().plusDays(2))
                .build();

        when(flightRepository.findById(flightId)).thenReturn(Optional.of(flight));

        BookingRequest req = BookingRequest.builder()
                .email("abc@example.com")
                .numberOfSeats(2)
                .passengers(List.of())
                .build();

        ApiException ex = assertThrows(ApiException.class,
                () -> bookingService.bookTicket(flightId, req));

        assertTrue(ex.getMessage().contains("Not enough seats"));
    }
}

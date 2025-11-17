package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.PassengerRequest;
import com.flightapp.entity.Flight;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.impl.BookingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock BookingRepository bookingRepository;
    @Mock FlightRepository flightRepository;

    @InjectMocks BookingServiceImpl bookingService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void bookTicket_success() {
        Long flightId = 1L;
        Flight flight = Flight.builder()
                .id(flightId)
                .availableSeats(5)
                .departureTime(LocalDateTime.now().plusDays(5))
                .build();

        when(flightRepository.findById(flightId)).thenReturn(java.util.Optional.of(flight));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        BookingRequest req = BookingRequest.builder()
                .email("a@b.com")
                .numberOfSeats(2)
                .passengers(List.of(
                    PassengerRequest.builder().name("P1").gender("M").age(25).seatNumber("1A").meal("VEG").build(),
                    PassengerRequest.builder().name("P2").gender("F").age(23).seatNumber("1B").meal("NON-VEG").build()
                ))
                .build();

        var resp = bookingService.bookTicket(flightId, req);
        assertNotNull(resp.getPnr());
        assertEquals(2, resp.getSeatsBooked());
        verify(flightRepository, times(1)).save(any());
    }

    @Test
    void bookTicket_insufficientSeats() {
        Long flightId = 1L;
        Flight flight = Flight.builder().id(flightId).availableSeats(1).build();
        when(flightRepository.findById(flightId)).thenReturn(java.util.Optional.of(flight));

        BookingRequest req = BookingRequest.builder().email("a@b.com").numberOfSeats(2).passengers(List.of()).build();
        Exception ex = assertThrows(RuntimeException.class, () -> bookingService.bookTicket(flightId, req));
        assertTrue(ex.getMessage().toLowerCase().contains("not enough"));
    }
}

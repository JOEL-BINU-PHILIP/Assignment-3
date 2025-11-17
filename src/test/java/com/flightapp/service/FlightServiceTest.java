package com.flightapp.service;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.impl.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FlightServiceTest {

    @Mock FlightRepository flightRepository;
    @Mock AirlineRepository airlineRepository;

    @InjectMocks FlightServiceImpl flightService;

    @BeforeEach
    void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addInventory_createsAirlineAndFlight() {
        FlightInventoryRequest req = FlightInventoryRequest.builder()
                .flightNumber("AI101").fromPlace("A").toPlace("B")
                .departureTime(LocalDateTime.now().plusDays(2))
                .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .price(1000.0).totalSeats(100).airlineName("AirX")
                .build();

        when(airlineRepository.findByName("AirX")).thenReturn(java.util.Optional.empty());
        when(airlineRepository.save(any(Airline.class))).thenAnswer(i -> i.getArguments()[0]);
        when(flightRepository.save(any(Flight.class))).thenAnswer(i -> i.getArguments()[0]);

        Flight f = flightService.addInventory(req);
        assertEquals("AI101", f.getFlightNumber());
        verify(airlineRepository, times(1)).save(any());
        verify(flightRepository, times(1)).save(any());
    }
}

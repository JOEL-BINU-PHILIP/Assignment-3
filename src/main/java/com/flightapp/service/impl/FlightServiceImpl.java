package com.flightapp.service.impl;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightService;
import com.flightapp.exception.ApiException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    public FlightServiceImpl(FlightRepository flightRepository, AirlineRepository airlineRepository) {
        this.flightRepository = flightRepository;
        this.airlineRepository = airlineRepository;
    }

    @Override
    public Flight addInventory(FlightInventoryRequest request) {
        Airline airline = airlineRepository.findByName(request.getAirlineName())
                .orElseGet(() -> airlineRepository.save(Airline.builder()
                        .name(request.getAirlineName())
                        .logoUrl(request.getAirlineLogoUrl())
                        .build()));

        Flight flight = Flight.builder()
                .flightNumber(request.getFlightNumber())
                .fromPlace(request.getFromPlace())
                .toPlace(request.getToPlace())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .price(request.getPrice())
                .totalSeats(request.getTotalSeats())
                .availableSeats(request.getTotalSeats())
                .airline(airline)
                .build();

        return flightRepository.save(flight);
    }

    @Override
    public List<Flight> searchFlights(FlightSearchRequest req) {
        LocalDate date = req.getTravelDate();
        // convert to a LocalDateTime for the query (search by the date)
        LocalDateTime dt = LocalDateTime.of(date, LocalTime.MIDNIGHT);
        return flightRepository.search(req.getFromPlace(), req.getToPlace(), dt);
    }

    @Override
    public Flight getFlightById(Long id) {
        return flightRepository.findById(id).orElseThrow(() -> new ApiException("Flight not found: " + id));
    }
}

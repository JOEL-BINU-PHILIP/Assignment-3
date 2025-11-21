package com.flightapp.service.impl;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import com.flightapp.exception.ApiException;
import com.flightapp.repository.AirlineRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.FlightService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    public Mono<Flight> addInventory(FlightInventoryRequest request) {

        // Basic validations
        if (request.getDepartureTime().isAfter(request.getArrivalTime())) {
            return Mono.error(new ApiException("Departure time must be before arrival time"));
        }
        if (request.getTotalSeats() <= 0) {
            return Mono.error(new ApiException("Total seats must be > 0"));
        }
        if (request.getPrice() <= 0) {
            return Mono.error(new ApiException("Price must be > 0"));
        }
        if (request.getFlightNumber() == null || request.getFlightNumber().isBlank()) {
            return Mono.error(new ApiException("Flight number cannot be empty"));
        }

        // Find or create airline
        Mono<Airline> airlineMono = airlineRepository.findByName(request.getAirlineName())
                .switchIfEmpty(
                        airlineRepository.save(
                                Airline.builder()
                                        .name(request.getAirlineName())
                                        .logoUrl(request.getAirlineLogoUrl())
                                        .build()
                        )
                );

        // Create flight only after airline is available
        return airlineMono.flatMap(airline -> {
            Flight flight = Flight.builder()
                    .flightNumber(request.getFlightNumber())
                    .fromPlace(request.getFromPlace())
                    .toPlace(request.getToPlace())
                    .departureTime(request.getDepartureTime())
                    .arrivalTime(request.getArrivalTime())
                    .price(request.getPrice())
                    .totalSeats(request.getTotalSeats())
                    .availableSeats(request.getTotalSeats())
                    .airlineId(airline.getId())
                    .build();

            return flightRepository.save(flight);
        });
    }

    @Override
    public Flux<Flight> searchFlights(FlightSearchRequest req) {
        LocalDate date = req.getTravelDate();
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        return flightRepository.search(
                req.getFromPlace(),
                req.getToPlace(),
                start,
                end
        );
    }

    @Override
    public Mono<Flight> getFlightById(Long id) {
        return flightRepository.findById(id)
                .switchIfEmpty(Mono.error(new ApiException("Flight not found: " + id)));
    }
}

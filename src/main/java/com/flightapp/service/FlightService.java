package com.flightapp.service;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.entity.Flight;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FlightService {
    Mono<Flight> addInventory(FlightInventoryRequest request);
    Flux<Flight> searchFlights(FlightSearchRequest req);
    Mono<Flight> getFlightById(Long id);
}

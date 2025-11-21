package com.flightapp.repository;

import com.flightapp.entity.Flight;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface FlightRepository extends ReactiveCrudRepository<Flight, Long> {

    @Query("SELECT * FROM flights WHERE from_place = :fromPlace AND to_place = :toPlace AND departure_time BETWEEN :start AND :end")
    Flux<Flight> search(String fromPlace, String toPlace, LocalDateTime start, LocalDateTime end);

    Flux<Flight> findByFlightNumber(String flightNumber);
}

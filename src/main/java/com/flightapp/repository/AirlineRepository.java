package com.flightapp.repository;

import com.flightapp.entity.Airline;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AirlineRepository extends ReactiveCrudRepository<Airline, Long> {
    Mono<Airline> findByName(String name);
}

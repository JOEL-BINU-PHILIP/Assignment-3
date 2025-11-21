package com.flightapp.repository;

import com.flightapp.entity.Booking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface BookingRepository extends ReactiveCrudRepository<Booking, Long> {
    Mono<Booking> findByPnr(String pnr);
    Flux<Booking> findByEmail(String email);
}

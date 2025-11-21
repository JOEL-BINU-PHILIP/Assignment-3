package com.flightapp.repository;

import com.flightapp.entity.Passenger;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface PassengerRepository extends ReactiveCrudRepository<Passenger, Long> {

    Flux<Passenger> findByBookingId(Long bookingId);

}

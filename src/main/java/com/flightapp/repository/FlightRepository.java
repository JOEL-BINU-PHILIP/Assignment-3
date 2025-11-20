package com.flightapp.repository;

import com.flightapp.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Long> {

    @Query("""
           SELECT f FROM Flight f
           WHERE f.fromPlace = :fromPlace
             AND f.toPlace = :toPlace
             AND f.departureTime BETWEEN :start AND :end
           """)
    List<Flight> search(String fromPlace,
                        String toPlace,
                        LocalDateTime start,
                        LocalDateTime end);
}

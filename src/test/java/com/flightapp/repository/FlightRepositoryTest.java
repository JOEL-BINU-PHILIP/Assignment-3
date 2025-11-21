package com.flightapp.repository;

import com.flightapp.entity.Airline;
import com.flightapp.entity.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@ActiveProfiles("test")
class FlightRepositoryTest {

    @Autowired private FlightRepository flightRepository;
    @Autowired private AirlineRepository airlineRepository;

    @Test
    void saveAndSearchFlight_byRouteAndDate_shouldReturnFlight() {
        Airline airline = Airline.builder().name("MyAir").logoUrl("http://x").build();
        airline = airlineRepository.save(airline);

        LocalDateTime dep = LocalDateTime.now().plusDays(5)
                .withHour(9).withMinute(0).withSecond(0).withNano(0);

        Flight f = Flight.builder()
                .flightNumber("T100")
                .fromPlace("CityA")
                .toPlace("CityB")
                .departureTime(dep)
                .arrivalTime(dep.plusHours(2))
                .price(500.0)
                .totalSeats(50)
                .availableSeats(50)
                .airline(airline)
                .build();

        flightRepository.save(f);

        // FIX: Correct search range
        LocalDateTime start = dep.minusHours(1);
        LocalDateTime end   = dep.plusHours(1);

        List<Flight> found = flightRepository.search("CityA", "CityB", start, end);

        assertThat(found).isNotEmpty();
        assertThat(found.get(0).getFlightNumber()).isEqualTo("T100");
    }

}

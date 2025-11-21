package com.flightapp.service.impl;

import com.flightapp.dto.*;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.entity.Passenger;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.repository.PassengerRepository;
import com.flightapp.service.BookingService;
import com.flightapp.exception.ApiException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, FlightRepository flightRepository, PassengerRepository passengerRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
    }

    @Override
    public Mono<BookingResponse> bookTicket(Long flightId, BookingRequest request) {
        return flightRepository.findById(flightId)
                .switchIfEmpty(Mono.error(new ApiException("Flight not found")))
                .flatMap(flight -> {
                    if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
                        return Mono.error(new ApiException("Not enough seats available"));
                    }
                    if (request.getPassengers() == null || request.getPassengers().size() != request.getNumberOfSeats()) {
                        return Mono.error(new ApiException("Passenger list size must equal numberOfSeats"));
                    }
                    // Check unique seat numbers among provided passengers
                    long distinctSeats = request.getPassengers().stream()
                            .map(PassengerRequest::getSeatNumber).distinct().count();
                    if (distinctSeats != request.getPassengers().size()) {
                        return Mono.error(new ApiException("Duplicate seat numbers in request"));
                    }

                    String pnr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                    Booking booking = Booking.builder()
                            .pnr(pnr)
                            .email(request.getEmail())
                            .flightId(flight.getId())
                            .seatsBooked(request.getNumberOfSeats())
                            .bookingTime(LocalDateTime.now())
                            .canceled(false)
                            .build();

                    // Save booking first, then save passengers with booking id
                    return bookingRepository.save(booking)
                            .flatMap(savedBooking -> {
                                // create passenger entities
                                var passEntities = request.getPassengers().stream()
                                        .map(p -> Passenger.builder()
                                                .name(p.getName())
                                                .gender(p.getGender())
                                                .age(p.getAge())
                                                .seatNumber(p.getSeatNumber())
                                                .meal(p.getMeal())
                                                .bookingId(savedBooking.getId())
                                                .build())
                                        .collect(Collectors.toList());

                                return passengerRepository.saveAll(passEntities).collectList()
                                        .flatMap(list -> {
                                            // update flight seats and persist
                                            flight.setAvailableSeats(flight.getAvailableSeats() - request.getNumberOfSeats());
                                            return flightRepository.save(flight)
                                                    .then(Mono.just(toResponseWithPassengers(savedBooking, list)));
                                        });
                            });
                });
    }

    @Override
    public Mono<BookingResponse> getTicketByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new ApiException("PNR not found")))
                .flatMap(b -> passengerRepository.findByBookingId(b.getId()).collectList()
                        .map(pass -> toResponseWithPassengers(b, pass)));
    }

    @Override
    public Flux<BookingResponse> getBookingHistory(String email) {
        return bookingRepository.findByEmail(email)
                .flatMap(b -> passengerRepository.findByBookingId(b.getId()).collectList()
                        .map(pass -> toResponseWithPassengers(b, pass)));
    }

    @Override
    public Mono<Void> cancelBooking(String pnr) {
        return bookingRepository.findByPnr(pnr)
                .switchIfEmpty(Mono.error(new ApiException("PNR not found")))
                .flatMap(booking -> {
                    if (Boolean.TRUE.equals(booking.getCanceled())) {
                        return Mono.error(new ApiException("Already canceled"));
                    }
                    return flightRepository.findById(booking.getFlightId()).flatMap(flight -> {
                        LocalDateTime now = LocalDateTime.now();
                        if (!flight.getDepartureTime().isAfter(now.plusHours(24))) {
                            return Mono.error(new ApiException("Cannot cancel booking within 24 hours of departure"));
                        }
                        booking.setCanceled(true);
                        booking.setCanceledAt(LocalDateTime.now());
                        return bookingRepository.save(booking)
                                .flatMap(saved -> {
                                    flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatsBooked());
                                    return flightRepository.save(flight).then();
                                });
                    });
                });
    }

    private BookingResponse toResponseWithPassengers(Booking b, java.util.List<Passenger> passengers) {
        var passengerDtos = passengers.stream().map(p ->
                PassengerRequest.builder()
                        .name(p.getName())
                        .age(p.getAge())
                        .gender(p.getGender())
                        .seatNumber(p.getSeatNumber())
                        .meal(p.getMeal())
                        .build()
        ).collect(Collectors.toList());

        return BookingResponse.builder()
                .pnr(b.getPnr())
                .email(b.getEmail())
                .flightId(b.getFlightId())
                .seatsBooked(b.getSeatsBooked())
                .bookingTime(b.getBookingTime())
                .canceled(b.getCanceled())
                .passengers(passengerDtos)
                .build();
    }
}

package com.flightapp.service.impl;

import com.flightapp.dto.*;
import com.flightapp.entity.Booking;
import com.flightapp.entity.Flight;
import com.flightapp.entity.Passenger;
import com.flightapp.repository.BookingRepository;
import com.flightapp.repository.FlightRepository;
import com.flightapp.service.BookingService;
import com.flightapp.exception.ApiException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, FlightRepository flightRepository) {
        this.bookingRepository = bookingRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public BookingResponse bookTicket(Long flightId, BookingRequest request) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new ApiException("Flight not found"));

        if (flight.getAvailableSeats() < request.getNumberOfSeats()) {
            throw new ApiException("Not enough seats available");
        }

        // Create booking and passengers
        String pnr = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = Booking.builder()
                .pnr(pnr)
                .email(request.getEmail())
                .flight(flight)
                .seatsBooked(request.getNumberOfSeats())
                .bookingTime(LocalDateTime.now())
                .canceled(false)
                .build();

        List<Passenger> passengers = request.getPassengers().stream().map(p -> {
            Passenger passenger = Passenger.builder()
                    .name(p.getName())
                    .age(p.getAge())
                    .gender(p.getGender())
                    .seatNumber(p.getSeatNumber())
                    .meal(p.getMeal())
                    .booking(booking)
                    .build();
            return passenger;
        }).collect(Collectors.toList());

        booking.setPassengers(passengers);

        // persist booking (cascades passengers)
        Booking saved = bookingRepository.save(booking);

        // update seats
        flight.setAvailableSeats(flight.getAvailableSeats() - request.getNumberOfSeats());
        flightRepository.save(flight);

        return toResponse(saved);
    }

    @Override
    public BookingResponse getTicketByPnr(String pnr) {
        Booking b = bookingRepository.findByPnr(pnr).orElseThrow(() -> new ApiException("PNR not found"));
        return toResponse(b);
    }

    @Override
    public List<BookingResponse> getBookingHistory(String email) {
        return bookingRepository.findByEmail(email).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(String pnr) {
        Booking booking = bookingRepository.findByPnr(pnr).orElseThrow(() -> new ApiException("PNR not found"));

        if (booking.getCanceled() != null && booking.getCanceled()) {
            throw new ApiException("Already canceled");
        }

        Flight flight = booking.getFlight();
        // Only allow cancellation if more than 24 hours before departure
        LocalDateTime now = LocalDateTime.now();
        if (!flight.getDepartureTime().isAfter(now.plusHours(24))) {
            throw new ApiException("Cannot cancel booking within 24 hours of departure");
        }

        booking.setCanceled(true);
        booking.setCanceledAt(LocalDateTime.now());
        bookingRepository.save(booking);

        // release seats
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatsBooked());
        flightRepository.save(flight);
    }

    private BookingResponse toResponse(Booking b) {
        List<PassengerRequest> passengers = b.getPassengers().stream().map(p ->
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
                .flightId(b.getFlight() != null ? b.getFlight().getId() : null)
                .seatsBooked(b.getSeatsBooked())
                .bookingTime(b.getBookingTime())
                .canceled(b.getCanceled())
                .passengers(passengers)
                .build();
    }
}

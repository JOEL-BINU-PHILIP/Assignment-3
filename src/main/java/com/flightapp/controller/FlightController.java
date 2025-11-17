package com.flightapp.controller;

import com.flightapp.dto.*;
import com.flightapp.entity.Flight;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    // Add inventory/schedule of an existing Airline
    @PostMapping("/airline/inventory/add")
    public ResponseEntity<Flight> addInventory(@Valid @RequestBody FlightInventoryRequest req) {
        Flight created = flightService.addInventory(req);
        return ResponseEntity.ok(created);
    }

    // Search flights
    @PostMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(@Valid @RequestBody FlightSearchRequest req) {
        List<Flight> results = flightService.searchFlights(req);
        return ResponseEntity.ok(results);
    }

    // Book ticket
    @PostMapping("/booking/{flightId}")
    public ResponseEntity<BookingResponse> bookTicket(@PathVariable Long flightId,
                                                      @Valid @RequestBody BookingRequest req) {
        BookingResponse response = bookingService.bookTicket(flightId, req);
        return ResponseEntity.ok(response);
    }

    // Get booked ticket details by PNR
    @GetMapping("/ticket/{pnr}")
    public ResponseEntity<BookingResponse> getTicket(@PathVariable String pnr) {
        BookingResponse response = bookingService.getTicketByPnr(pnr);
        return ResponseEntity.ok(response);
    }

    // Get booking history by email
    @GetMapping("/booking/history/{email}")
    public ResponseEntity<List<BookingResponse>> bookingHistory(@PathVariable String email) {
        List<BookingResponse> list = bookingService.getBookingHistory(email);
        return ResponseEntity.ok(list);
    }

    // Cancel booking by pnr
    @DeleteMapping("/booking/cancel/{pnr}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String pnr) {
        bookingService.cancelBooking(pnr);
        return ResponseEntity.noContent().build();
    }
}

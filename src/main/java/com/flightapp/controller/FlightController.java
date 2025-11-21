package com.flightapp.controller;

import com.flightapp.dto.*;
import com.flightapp.entity.Flight;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {

    private final FlightService flightService;
    private final BookingService bookingService;

    public FlightController(FlightService flightService, BookingService bookingService) {
        this.flightService = flightService;
        this.bookingService = bookingService;
    }

    // Add inventory -> respond 201 CREATED
    @PostMapping("/airline/inventory/add")
    public Mono<ResponseEntity<Flight>> addInventory(@Valid @RequestBody Mono<FlightInventoryRequest> reqMono) {
        return reqMono.flatMap(flightService::addInventory)
                .map(f -> ResponseEntity.status(HttpStatus.CREATED).body(f));
    }

    // Search - returns Flux<Flight>
    @PostMapping("/search")
    public Flux<Flight> searchFlights(@Valid @RequestBody Mono<FlightSearchRequest> reqMono) {
        return reqMono.flatMapMany(flightService::searchFlights);
    }

    // Book ticket -> returns 201 Created with BookingResponse
    @PostMapping("/booking/{flightId}")
    public Mono<ResponseEntity<BookingResponse>> bookTicket(@PathVariable Long flightId,
                                                            @Valid @RequestBody Mono<BookingRequest> reqMono) {
        return reqMono.flatMap(req -> bookingService.bookTicket(flightId, req))
                .map(resp -> ResponseEntity.status(HttpStatus.CREATED).body(resp));
    }

    @GetMapping("/ticket/{pnr}")
    public Mono<BookingResponse> getTicket(@PathVariable String pnr) {
        return bookingService.getTicketByPnr(pnr);
    }

    @GetMapping("/booking/history/{email}")
    public Flux<BookingResponse> bookingHistory(@PathVariable String email) {
        return bookingService.getBookingHistory(email);
    }

    @DeleteMapping("/booking/cancel/{pnr}")
    public Mono<ResponseEntity<Void>> cancelBooking(@PathVariable String pnr) {
        return bookingService.cancelBooking(pnr)
                .thenReturn(ResponseEntity.noContent().<Void>build());
    }
}

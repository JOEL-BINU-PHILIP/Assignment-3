package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.BookingResponse;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

public interface BookingService {
    Mono<BookingResponse> bookTicket(Long flightId, BookingRequest request);
    Mono<BookingResponse> getTicketByPnr(String pnr);
    Flux<BookingResponse> getBookingHistory(String email);
    Mono<Void> cancelBooking(String pnr);
}

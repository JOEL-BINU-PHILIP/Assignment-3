package com.flightapp.service;

import com.flightapp.dto.BookingRequest;
import com.flightapp.entity.Booking;
import com.flightapp.dto.BookingResponse;
import java.util.List;

public interface BookingService {
    BookingResponse bookTicket(Long flightId, BookingRequest request);
    BookingResponse getTicketByPnr(String pnr);
    List<BookingResponse> getBookingHistory(String email);
    void cancelBooking(String pnr);
}

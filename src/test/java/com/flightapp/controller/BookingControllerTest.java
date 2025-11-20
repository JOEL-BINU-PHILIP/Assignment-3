package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.BookingRequest;
import com.flightapp.dto.PassengerRequest;
import com.flightapp.dto.BookingResponse;
import com.flightapp.service.BookingService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.flightapp.controller.FlightController.class)
class BookingControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private BookingService bookingService;
    @MockBean private com.flightapp.service.FlightService flightService; // required by controller

    @Test
    void bookTicket_returnsBookingResponse() throws Exception {
        BookingRequest req = BookingRequest.builder()
                .email("u@e.com")
                .numberOfSeats(1)
                .passengers(List.of(PassengerRequest.builder().name("p").gender("M").age(20).seatNumber("1A").meal("VEG").build()))
                .build();

        BookingResponse resp = BookingResponse.builder()
                .pnr("ABCD1234")
                .email("u@e.com")
                .flightId(1L)
                .seatsBooked(1)
                .build();

        when(bookingService.bookTicket(any(Long.class), any(BookingRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/api/v1.0/flight/booking/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("ABCD1234"))
                .andExpect(jsonPath("$.email").value("u@e.com"));
    }
}

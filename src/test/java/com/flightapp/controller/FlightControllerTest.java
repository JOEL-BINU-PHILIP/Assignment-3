package com.flightapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.entity.Flight;
import com.flightapp.service.BookingService;
import com.flightapp.service.FlightService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = com.flightapp.controller.FlightController.class)
class FlightControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private FlightService flightService;
    @MockBean private BookingService bookingService;

    @Test
    void addInventory_returns200_andFlight() throws Exception {
        FlightInventoryRequest req = FlightInventoryRequest.builder()
                .flightNumber("AI101")
                .fromPlace("A").toPlace("B")
                .departureTime(LocalDateTime.now().plusDays(2))
                .arrivalTime(LocalDateTime.now().plusDays(2).plusHours(2))
                .price(1000.0).totalSeats(100).airlineName("AirX")
                .build();

        Flight f = Flight.builder()
                .id(1L).flightNumber("AI101")
                .fromPlace("A").toPlace("B")
                .departureTime(req.getDepartureTime()).arrivalTime(req.getArrivalTime())
                .price(1000.0).totalSeats(100).availableSeats(100)
                .build();

        when(flightService.addInventory(any(FlightInventoryRequest.class))).thenReturn(f);

        mockMvc.perform(post("/api/v1.0/flight/airline/inventory/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.flightNumber").value("AI101"))
                .andExpect(jsonPath("$.availableSeats").value(100));
    }
}

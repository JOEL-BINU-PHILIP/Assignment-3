package com.flightapp.service;

import com.flightapp.dto.FlightInventoryRequest;
import com.flightapp.dto.FlightSearchRequest;
import com.flightapp.entity.Flight;
import java.util.List;

public interface FlightService {
    Flight addInventory(FlightInventoryRequest request);
    List<Flight> searchFlights(FlightSearchRequest req);
    Flight getFlightById(Long id);
}

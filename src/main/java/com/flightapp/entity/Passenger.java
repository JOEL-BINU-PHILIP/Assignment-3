package com.flightapp.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("passengers")
public class Passenger {
    @Id
    private Long id;

    private String name;
    private String gender;
    private Integer age;
    private String seatNumber;
    private String meal;

    private Long bookingId;
}

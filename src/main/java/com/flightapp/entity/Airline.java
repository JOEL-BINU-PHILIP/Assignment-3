package com.flightapp.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("airlines")
public class Airline {
    @Id
    private Long id;

    private String name;
    private String logoUrl;
}

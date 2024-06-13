package com.dinesh.ecustomer.customer;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document
@Builder
public class Customer {

    private String id;
    private String firstName;
    private String lastName;
    private String email;

    private Address address;

}

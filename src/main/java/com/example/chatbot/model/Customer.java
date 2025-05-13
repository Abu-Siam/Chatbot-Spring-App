package com.example.chatbot.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "customers")
@Data
public class Customer {

    @Id
    @Column(name = "Index")
    private Integer index;

    @Column(name = "Customer Id")
    private String customerId;

    @Column(name = "First Name")
    private String firstName;

    @Column(name = "Last Name")
    private String lastName;

    @Column(name = "Company")
    private String company;

    @Column(name = "City")
    private String city;

    @Column(name = "Country")
    private String country;

    @Column(name = "Phone 1")
    private String phone1;

    @Column(name = "Phone 2")
    private String phone2;

    @Column(name = "Email")
    private String email;

    @Column(name = "Subscription Date")
    private String subscriptionDate;

    @Column(name = "Website")
    private String website;

    // Getters and setters...
}

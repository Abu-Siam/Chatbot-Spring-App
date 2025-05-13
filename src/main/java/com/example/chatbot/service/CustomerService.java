package com.example.chatbot.service;

import org.springframework.stereotype.Service;
import com.example.chatbot.repo.CustomerRepository;
import com.example.chatbot.model.Customer;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    public List<Customer> getAllCustomers() {
        return repo.findAll();
    }
}

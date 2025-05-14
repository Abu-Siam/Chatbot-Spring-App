package com.example.chatbot.controller;
import com.example.chatbot.model.Customer;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.chatbot.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${fastapi.url}")
    private String fastApiUrl;


    @GetMapping("/customers")
    public String showCustomers(Model model) {
        model.addAttribute("customers", service.getAllCustomers());
        return "customers";  // maps to customers.html
    }


    @ResponseBody
    @PostMapping("/ask")
    public Map<String, String> askQuestion(@RequestBody Map<String, String> payload) {
        String question = payload.get("question");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("question", question);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(fastApiUrl+ "/ask", entity, Map.class);
            String answer = (String) response.getBody().get("answer");

            Map<String, String> result = new HashMap<>();
            result.put("answer", answer);
            return result;

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("answer", "Error: " + e.getMessage());
            return error;
        }
    }
    private static String escape(String field) {
        if (field == null) return "";
        return field.replace("\"", "\"\"");
    }
    @ResponseBody
    @PostMapping("/train")
    public Map<String, String> trainModel() {
        try {
            // Step 1: Export customers to a local CSV file
            List<Customer> customers = service.getAllCustomers();
            String filePath = "./customers.csv";
            File csvFile = new File(filePath);
            try (PrintWriter writer = new PrintWriter(csvFile)) {
                writer.println("Index,Customer Id,First Name,Last Name,Company,City,Country,Phone 1,Phone 2,Email,Subscription Date,Website");

                for (Customer c : customers) {
                    writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"%n",
                            c.getIndex(),
                            escape(c.getCustomerId()),
                            escape(c.getFirstName()),
                            escape(c.getLastName()),
                            escape(c.getCompany()),
                            escape(c.getCity()),
                            escape(c.getCountry()),
                            escape(c.getPhone1()),
                            escape(c.getPhone2()),
                            escape(c.getEmail()),
                            escape(c.getSubscriptionDate()),
                            escape(c.getWebsite())
                    );
                }
            }

            // Step 2: Upload the file to FastAPI using multipart/form-data
            FileSystemResource resource = new FileSystemResource(csvFile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", resource);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    fastApiUrl + "/upload-csv", requestEntity, Map.class
            );

            Map<String, String> result = new HashMap<>();
            result.put("message", (String) response.getBody().get("message"));
            return result;

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Training failed: " + e.getMessage());
            return error;
        }
    }
}
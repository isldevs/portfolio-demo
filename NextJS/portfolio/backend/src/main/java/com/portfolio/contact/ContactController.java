package com.portfolio.contact;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contact")
public class ContactController {
    private final ContactService service;
    public ContactController(ContactService service) { this.service = service; }

    @PostMapping
    ResponseEntity<Map<String, String>> create(@Valid @RequestBody ContactRequest request,
            @RequestHeader(value = "X-Client-IP", required = false) String clientIp) {
        service.submit(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Your message has been received. Thank you!"));
    }
}

package com.portfolio.contact;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {
    private final ContactRepository repository;
    private final ContactRateLimiter limiter;

    public ContactService(ContactRepository repository, ContactRateLimiter limiter) {
        this.repository = repository;
        this.limiter = limiter;
    }

    public void submit(ContactRequest request, String clientIp) {
        if (!limiter.allow(clientIp)) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too many messages. Try again later.");
        }
        // Hidden honeypot field: acknowledge bots without saving their messages.
        if (request.website() != null && !request.website().isBlank()) {
            return;
        }
        if (request.message().trim().length() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message must contain at least 10 characters.");
        }
        repository.save(new ContactMessage(
                request.name(), request.email(), request.subject(), request.message()));
    }
}

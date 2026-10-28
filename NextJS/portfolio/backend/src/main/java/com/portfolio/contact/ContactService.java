package com.portfolio.contact;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ContactService {
    private final ContactRepository repository;
    private final String hashSalt;

    public ContactService(ContactRepository repository, @Value("${app.ip-hash-salt}") String hashSalt) {
        if (hashSalt == null || hashSalt.length() < 32) {
            throw new IllegalStateException("IP_HASH_SALT must be at least 32 characters.");
        }
        this.repository = repository;
        this.hashSalt = hashSalt;
    }

    @Transactional
    public void submit(ContactRequest request, String clientIp) {
        // Honeypot; pretend success so spambots receive no useful feedback.
        if (request.website() != null && !request.website().isBlank()) return;

        String clientHash = hashClient(clientIp);
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        if (repository.countByClientHashAndCreatedAtAfter(clientHash, oneHourAgo) >= 5) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many messages. Please try again in an hour.");
        }
        repository.save(new ContactMessage(
                request.name().trim(), request.email().trim().toLowerCase(java.util.Locale.ROOT),
                request.subject().trim(), request.message().trim(), clientHash));
    }

    private String hashClient(String address) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] input = (hashSalt + ":" + (address == null ? "unknown" : address)).getBytes(StandardCharsets.UTF_8);
            return HexFormat.of().formatHex(sha256.digest(input));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is unavailable", ex);
        }
    }
}

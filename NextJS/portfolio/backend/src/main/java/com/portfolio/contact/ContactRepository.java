package com.portfolio.contact;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<ContactMessage, UUID> {
    long countByClientHashAndCreatedAtAfter(String clientHash, Instant after);
}

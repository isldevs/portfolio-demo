package com.portfolio.contact;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sender_name", nullable = false, length = 120)
    private String senderName;
    @Column(name = "sender_email", nullable = false, length = 180)
    private String senderEmail;
    @Column(nullable = false, length = 180)
    private String subject;
    @Column(nullable = false, columnDefinition = "text")
    private String message;
    @Column(name = "client_hash", nullable = false, length = 64)
    private String clientHash;
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ContactMessage() {}

    public ContactMessage(String senderName, String senderEmail, String subject, String message, String clientHash) {
        this.senderName = senderName;
        this.senderEmail = senderEmail;
        this.subject = subject;
        this.message = message;
        this.clientHash = clientHash;
    }

    @PrePersist
    void onCreate() { if (createdAt == null) createdAt = Instant.now(); }

    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }
}

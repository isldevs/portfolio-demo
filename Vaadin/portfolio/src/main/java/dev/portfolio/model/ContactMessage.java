package dev.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "contact_message")
public class ContactMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String senderName = "";

    @Column(nullable = false, length = 254)
    private String senderEmail = "";

    @Column(nullable = false, length = 160)
    private String subject = "";

    @Column(nullable = false, columnDefinition = "text")
    private String message = "";

    @Column(nullable = false)
    private java.time.OffsetDateTime receivedAt;

    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    public ContactMessage() {}

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String value) { this.senderName = value; }

    public String getSenderEmail() { return senderEmail; }
    public void setSenderEmail(String value) { this.senderEmail = value; }

    public String getSubject() { return subject; }
    public void setSubject(String value) { this.subject = value; }

    public String getMessage() { return message; }
    public void setMessage(String value) { this.message = value; }

    public java.time.OffsetDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(java.time.OffsetDateTime value) { this.receivedAt = value; }

    public boolean isRead() { return read; }
    public void setRead(boolean value) { this.read = value; }

    @PrePersist
    void onCreate() {
        if (receivedAt == null) receivedAt = java.time.OffsetDateTime.now(java.time.ZoneOffset.UTC);
    }

}

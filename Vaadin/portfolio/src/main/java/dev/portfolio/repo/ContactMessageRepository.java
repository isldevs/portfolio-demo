package dev.portfolio.repo;

import dev.portfolio.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    java.util.List<ContactMessage> findAllByOrderByReceivedAtDesc();
    long countByReadFalse();
}

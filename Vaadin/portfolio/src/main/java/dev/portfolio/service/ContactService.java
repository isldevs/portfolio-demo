package dev.portfolio.service;

import dev.portfolio.model.ContactMessage;
import dev.portfolio.repo.ContactMessageRepository;
import org.springframework.stereotype.Service;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class ContactService {
    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private final ContactMessageRepository repository;
    public ContactService(ContactMessageRepository repository) { this.repository = repository; }

    @Transactional
    public void submit(String name, String email, String subject, String message) {
        required(name, 120, "Name"); required(subject, 160, "Subject");
        required(message, 5000, "Message");
        required(email, 254, "Email");
        if (message.trim().length() < 15) throw new IllegalArgumentException("Message must have at least 15 characters");
        if (!EMAIL.matcher(email.strip()).matches()) throw new IllegalArgumentException("Enter a valid email address");
        ContactMessage m = new ContactMessage();
        m.setSenderName(name.strip()); m.setSenderEmail(email.strip());
        m.setSubject(subject.strip()); m.setMessage(message.strip());
        repository.save(m);
    }

    @Transactional(readOnly = true)
    @RolesAllowed("ADMIN")
    public List<ContactMessage> inbox() { return repository.findAllByOrderByReceivedAtDesc(); }
    @Transactional(readOnly = true)
    @RolesAllowed("ADMIN")
    public long count() { return repository.count(); }
    @Transactional(readOnly = true)
    @RolesAllowed("ADMIN")
    public long unread() { return repository.countByReadFalse(); }
    @Transactional
    @RolesAllowed("ADMIN")
    public void markRead(Long id) {
        ContactMessage m = repository.findById(id).orElseThrow();
        m.setRead(true); repository.save(m);
    }
    @Transactional
    @RolesAllowed("ADMIN")
    public void delete(Long id) { repository.deleteById(id); }

    private static void required(String s, int max, String label) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(label + " is required");
        if (s.length() > max) throw new IllegalArgumentException(label + " must be at most " + max + " characters");
    }
}

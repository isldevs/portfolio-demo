package dev.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "portfolio_profile")
public class Profile {
    @Id
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName = "Your Name";

    @Column(nullable = false, length = 220)
    private String headline = "Java & Full-Stack Developer";

    @Column(nullable = false, length = 120)
    private String location = "Phnom Penh, Cambodia";

    @Column(nullable = false, length = 254)
    private String email = "hello@example.com";

    @Column(nullable = false, length = 1400)
    private String introduction = "I build reliable software and practical digital experiences.";

    @Column(nullable = false, columnDefinition = "text")
    private String aboutText = "Replace this sample with your background, accomplishments and the kind of work you enjoy.";

    @Column(nullable = false, length = 500)
    private String githubUrl = "";

    @Column(nullable = false, length = 500)
    private String linkedinUrl = "";

    @Column(nullable = false, length = 500)
    private String cvUrl = "";

    @Column(nullable = false, length = 180)
    private String availability = "Open to interesting projects";

    public Profile() {}

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public String getFullName() { return fullName; }
    public void setFullName(String value) { this.fullName = value; }

    public String getHeadline() { return headline; }
    public void setHeadline(String value) { this.headline = value; }

    public String getLocation() { return location; }
    public void setLocation(String value) { this.location = value; }

    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }

    public String getIntroduction() { return introduction; }
    public void setIntroduction(String value) { this.introduction = value; }

    public String getAboutText() { return aboutText; }
    public void setAboutText(String value) { this.aboutText = value; }

    public String getGithubUrl() { return githubUrl; }
    public void setGithubUrl(String value) { this.githubUrl = value; }

    public String getLinkedinUrl() { return linkedinUrl; }
    public void setLinkedinUrl(String value) { this.linkedinUrl = value; }

    public String getCvUrl() { return cvUrl; }
    public void setCvUrl(String value) { this.cvUrl = value; }

    public String getAvailability() { return availability; }
    public void setAvailability(String value) { this.availability = value; }

}

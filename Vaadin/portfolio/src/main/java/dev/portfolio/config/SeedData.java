package dev.portfolio.config;

import dev.portfolio.model.*;
import dev.portfolio.repo.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SeedData {
    @Bean
    CommandLineRunner initializePortfolio(
            ProfileRepository profiles,
            ProjectRepository projects,
            SkillRepository skills,
            ExperienceRepository experiences,
            AdminUserRepository admins,
            PasswordEncoder encoder,
            @Value("${portfolio.admin.username:}") String username,
            @Value("${portfolio.admin.password:}") String password) {
        return args -> {
            if (username.isBlank() || password.length() < 16 || username.length() > 100) {
                throw new IllegalStateException("ADMIN_USERNAME and ADMIN_PASSWORD (at least 16 characters) are required");
            }
            // Only the configured admin may exist after startup, including username rotation.
            admins.findAll().stream()
                    .filter(existing -> !existing.getUsername().equals(username))
                    .forEach(admins::delete);
            // Synchronize the admin credential from the environment on restart.
            AdminUser admin = admins.findByUsername(username).orElseGet(AdminUser::new);
            admin.setUsername(username);
            if (!encoder.matches(password, admin.getPasswordHash() == null ? "" : admin.getPasswordHash())) {
                admin.setPasswordHash(encoder.encode(password));
            }
            admins.save(admin);
            if (profiles.count() == 0) {
                Profile p = new Profile(); p.setId(1L); profiles.save(p);
            }
            if (projects.count() == 0) {
                projects.save(project("banking-platform", "Digital Banking Platform",
                        "Reliable banking services built for scale.",
                        "A sample case study. Replace this text with your project goals, architecture, responsibilities, and outcomes.",
                        "Java, Spring Boot, PostgreSQL, Docker", true, 1));
                projects.save(project("analytics-dashboard", "Analytics & Reporting Dashboard",
                        "Turning operational data into useful decisions.",
                        "A sample project highlighting dashboards, reporting APIs and performance monitoring.",
                        "Java, SQL, Vaadin", true, 2));
                projects.save(project("cloud-automation", "Deployment Automation Toolkit",
                        "Repeatable delivery from development to production.",
                        "A sample DevOps case study. Describe your real automation, tests and deployment workflows.",
                        "Docker, CI/CD, Linux", true, 3));
            }
            if (skills.count() == 0) {
                addSkill(skills,"Java", "Backend", 93, 1);
                addSkill(skills,"Spring Boot", "Backend", 90, 2);
                addSkill(skills,"PostgreSQL", "Backend", 84, 3);
                addSkill(skills,"Vaadin Flow", "Frontend", 86, 1);
                addSkill(skills,"HTML / CSS", "Frontend", 80, 2);
                addSkill(skills,"Docker", "DevOps", 82, 1);
                addSkill(skills,"GitHub Actions", "DevOps", 78, 2);
            }
            if (experiences.count() == 0) {
                Experience e = new Experience();
                e.setRoleTitle("Your current role"); e.setOrganization("Your organization");
                e.setPeriod("2024 — Present"); e.setLocation("Cambodia");
                e.setDescription("Replace with a real summary of the systems you built and the impact of your work.");
                experiences.save(e);
            }
        };
    }

    private static Project project(String slug, String title, String summary, String details,
                                   String tech, boolean featured, int order) {
        Project p = new Project(); p.setSlug(slug); p.setTitle(title); p.setSummary(summary);
        p.setDetails(details); p.setTechnology(tech); p.setFeatured(featured); p.setSortOrder(order);
        return p;
    }
    private static void addSkill(SkillRepository repo, String name, String category, int score, int order) {
        Skill skill = new Skill(); skill.setName(name); skill.setCategory(category);
        skill.setProficiency(score); skill.setSortOrder(order); repo.save(skill);
    }
}

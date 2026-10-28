package dev.portfolio.service;

import dev.portfolio.model.*;
import dev.portfolio.repo.*;
import org.springframework.stereotype.Service;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {
    private final ProfileRepository profiles;
    private final ProjectRepository projects;
    private final SkillRepository skills;
    private final ExperienceRepository experiences;

    public PortfolioService(ProfileRepository profiles, ProjectRepository projects,
                            SkillRepository skills, ExperienceRepository experiences) {
        this.profiles = profiles; this.projects = projects;
        this.skills = skills; this.experiences = experiences;
    }

    @Transactional(readOnly = true)
    public Profile profile() { return profiles.findById(1L).orElseThrow(); }
    @Transactional(readOnly = true)
    public List<Project> projects() { return projects.findAllByOrderBySortOrderAscIdDesc(); }
    @Transactional(readOnly = true)
    public List<Project> featuredProjects() { return projects.findByFeaturedTrueOrderBySortOrderAscIdDesc(); }
    @Transactional(readOnly = true)
    public Optional<Project> project(String slug) { return projects.findBySlug(slug); }
    @Transactional(readOnly = true)
    public List<Skill> skills() { return skills.findAllByOrderByCategoryAscSortOrderAscNameAsc(); }
    @Transactional(readOnly = true)
    public List<Experience> experiences() { return experiences.findAllByOrderBySortOrderAscIdDesc(); }

    @Transactional
    @RolesAllowed("ADMIN")
    public void saveProfile(Profile p) {
        mustHave(p.getFullName(),120,"Name");
        mustHave(p.getHeadline(),220,"Headline");
        mustHave(p.getEmail(),254,"Email");
        checkLength(p.getLocation(),120,"Location");
        checkLength(p.getIntroduction(),1400,"Introduction");
        checkLength(p.getGithubUrl(),500,"GitHub URL");
        checkLength(p.getLinkedinUrl(),500,"LinkedIn URL");
        checkLength(p.getCvUrl(),500,"CV URL");
        checkLength(p.getAvailability(),180,"Availability");
        p.setId(1L);
        profiles.save(p);
    }
    @Transactional
    @RolesAllowed("ADMIN")
    public Project saveProject(Project p) {
        mustHave(p.getSlug(),100,"Slug");
        if (!p.getSlug().matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
            throw new IllegalArgumentException("Slug must use lowercase words separated by hyphens");
        }
        mustHave(p.getTitle(),150,"Title"); mustHave(p.getSummary(),500,"Summary");
        checkLength(p.getTechnology(),400,"Technologies");
        checkLength(p.getSourceUrl(),500,"Source URL"); checkLength(p.getDemoUrl(),500,"Demo URL");
        return projects.save(p);
    }
    @Transactional
    @RolesAllowed("ADMIN")
    public void deleteProject(Long id) { projects.deleteById(id); }
    @Transactional
    @RolesAllowed("ADMIN")
    public Skill saveSkill(Skill s) {
        mustHave(s.getName(),100,"Skill"); mustHave(s.getCategory(),100,"Category");
        if (s.getProficiency() < 0 || s.getProficiency() > 100) throw new IllegalArgumentException("Level must be between 0 and 100");
        return skills.save(s);
    }
    @Transactional
    @RolesAllowed("ADMIN")
    public void deleteSkill(Long id) { skills.deleteById(id); }
    @Transactional
    @RolesAllowed("ADMIN")
    public Experience saveExperience(Experience e) {
        mustHave(e.getRoleTitle(),160,"Role"); mustHave(e.getOrganization(),160,"Organization");
        mustHave(e.getPeriod(),120,"Period"); checkLength(e.getLocation(),160,"Location");
        return experiences.save(e);
    }
    @Transactional
    @RolesAllowed("ADMIN")
    public void deleteExperience(Long id) { experiences.deleteById(id); }

    private void mustHave(String s, int max, String label) {
        if (s == null || s.isBlank()) throw new IllegalArgumentException(label + " is required");
        checkLength(s,max,label);
    }
    private void checkLength(String s, int max, String label) {
        if (s != null && s.length() > max) throw new IllegalArgumentException(label + " is too long (max " + max + ")");
    }
}

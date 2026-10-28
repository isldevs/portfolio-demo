package dev.portfolio.service;

import dev.portfolio.model.Project;
import dev.portfolio.repo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PortfolioServiceTest {
    private ProjectRepository projects;
    private PortfolioService service;

    @BeforeEach
    void setup() {
        projects = mock(ProjectRepository.class);
        service = new PortfolioService(mock(ProfileRepository.class), projects,
                mock(SkillRepository.class), mock(ExperienceRepository.class));
    }

    @Test
    void disallowsInvalidSlug() {
        Project project = validProject(); project.setSlug("Bad URL / X");
        assertThrows(IllegalArgumentException.class, () -> service.saveProject(project));
        verifyNoInteractions(projects);
    }

    @Test
    void savesValidProject() {
        Project project = validProject();
        service.saveProject(project);
        verify(projects).save(project);
    }

    private Project validProject() {
        Project p = new Project();
        p.setSlug("my-project"); p.setTitle("My Project");
        p.setSummary("A useful portfolio project.");
        return p;
    }
}

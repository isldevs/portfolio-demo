package dev.portfolio.repo;

import dev.portfolio.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findBySlug(String slug);
    java.util.List<Project> findAllByOrderBySortOrderAscIdDesc();
    java.util.List<Project> findByFeaturedTrueOrderBySortOrderAscIdDesc();
}

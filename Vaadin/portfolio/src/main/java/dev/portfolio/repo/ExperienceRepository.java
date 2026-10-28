package dev.portfolio.repo;

import dev.portfolio.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    java.util.List<Experience> findAllByOrderBySortOrderAscIdDesc();
}

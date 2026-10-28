package dev.portfolio.repo;

import dev.portfolio.model.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    java.util.List<Skill> findAllByOrderByCategoryAscSortOrderAscNameAsc();
}

package dev.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "portfolio_skill")
public class Skill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name = "";

    @Column(nullable = false, length = 100)
    private String category = "";

    @Column(nullable = false)
    private int proficiency = 75;

    @Column(nullable = false)
    private int sortOrder = 0;

    public Skill() {}

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public String getName() { return name; }
    public void setName(String value) { this.name = value; }

    public String getCategory() { return category; }
    public void setCategory(String value) { this.category = value; }

    public int getProficiency() { return proficiency; }
    public void setProficiency(int value) { this.proficiency = value; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int value) { this.sortOrder = value; }

}

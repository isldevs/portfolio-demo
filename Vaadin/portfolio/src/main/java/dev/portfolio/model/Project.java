package dev.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "portfolio_project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug = "";

    @Column(nullable = false, length = 150)
    private String title = "";

    @Column(nullable = false, length = 500)
    private String summary = "";

    @Column(nullable = false, columnDefinition = "text")
    private String details = "";

    @Column(nullable = false, length = 400)
    private String technology = "";

    @Column(nullable = false, length = 500)
    private String sourceUrl = "";

    @Column(nullable = false, length = 500)
    private String demoUrl = "";

    @Column(nullable = false)
    private boolean featured = false;

    @Column(nullable = false)
    private int sortOrder = 0;

    public Project() {}

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public String getSlug() { return slug; }
    public void setSlug(String value) { this.slug = value; }

    public String getTitle() { return title; }
    public void setTitle(String value) { this.title = value; }

    public String getSummary() { return summary; }
    public void setSummary(String value) { this.summary = value; }

    public String getDetails() { return details; }
    public void setDetails(String value) { this.details = value; }

    public String getTechnology() { return technology; }
    public void setTechnology(String value) { this.technology = value; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String value) { this.sourceUrl = value; }

    public String getDemoUrl() { return demoUrl; }
    public void setDemoUrl(String value) { this.demoUrl = value; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean value) { this.featured = value; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int value) { this.sortOrder = value; }

}

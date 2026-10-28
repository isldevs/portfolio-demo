package dev.portfolio.model;

import jakarta.persistence.*;

@Entity
@Table(name = "portfolio_experience")
public class Experience {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String roleTitle = "";

    @Column(nullable = false, length = 160)
    private String organization = "";

    @Column(nullable = false, length = 120)
    private String period = "";

    @Column(nullable = false, length = 160)
    private String location = "";

    @Column(nullable = false, columnDefinition = "text")
    private String description = "";

    @Column(nullable = false)
    private int sortOrder = 0;

    public Experience() {}

    public Long getId() { return id; }
    public void setId(Long value) { this.id = value; }

    public String getRoleTitle() { return roleTitle; }
    public void setRoleTitle(String value) { this.roleTitle = value; }

    public String getOrganization() { return organization; }
    public void setOrganization(String value) { this.organization = value; }

    public String getPeriod() { return period; }
    public void setPeriod(String value) { this.period = value; }

    public String getLocation() { return location; }
    public void setLocation(String value) { this.location = value; }

    public String getDescription() { return description; }
    public void setDescription(String value) { this.description = value; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int value) { this.sortOrder = value; }

}

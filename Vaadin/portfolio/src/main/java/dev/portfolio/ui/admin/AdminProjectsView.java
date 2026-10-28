package dev.portfolio.ui.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.portfolio.model.Project;
import dev.portfolio.service.PortfolioService;
import dev.portfolio.ui.Ui;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/projects", layout = AdminLayout.class)
@PageTitle("Projects | Portfolio CMS")
@RolesAllowed("ADMIN")
public class AdminProjectsView extends Div {
    private final PortfolioService service;
    private final Grid<Project> grid = new Grid<>(Project.class, false);
    public AdminProjectsView(PortfolioService service) {
        this.service = service;
        grid.addColumn(Project::getTitle).setHeader("Project title").setAutoWidth(true);
        grid.addColumn(Project::getSlug).setHeader("Slug").setAutoWidth(true);
        grid.addColumn(Project::isFeatured).setHeader("Featured");
        grid.addColumn(Project::getSortOrder).setHeader("Order");
        grid.addComponentColumn(p -> AdminUi.primary("Edit", () -> editor(p))).setHeader("Edit");
        grid.addComponentColumn(p -> AdminUi.danger("Delete", () -> delete(p))).setHeader("Delete");
        grid.setWidthFull(); reload();
        add(AdminUi.page("Projects", "Manage your case studies, links and featured work.",
                AdminUi.primary("+ Add project", () -> editor(new Project())), grid));
    }
    private void reload() { grid.setItems(service.projects()); }
    private void delete(Project p) {
        AdminUi.confirm("Delete project ‘" + p.getTitle() + "’? This cannot be undone.", () -> {
            try { service.deleteProject(p.getId()); reload(); AdminUi.success("Project deleted"); }
            catch (Exception ex) { AdminUi.fail(ex); }
        });
    }
    private void editor(Project p) {
        TextField title = text("Title",p.getTitle(),150);
        TextField slug = text("Slug (lowercase-with-hyphens)",p.getSlug(),100);
        TextArea summary = area("Short summary",p.getSummary(),500);
        TextArea details = area("Full case study",p.getDetails(),10000);
        TextField technology = text("Technologies (comma separated)",p.getTechnology(),400);
        TextField source = text("GitHub / source URL",p.getSourceUrl(),500);
        TextField demo = text("Live demo URL",p.getDemoUrl(),500);
        IntegerField order = new IntegerField("Display order"); order.setValue(p.getSortOrder());
        Checkbox featured = new Checkbox("Show on home page"); featured.setValue(p.isFeatured());
        Dialog dialog = AdminUi.dialog(p.getId() == null ? "New project" : "Edit project",
                title, slug, summary, details, technology, source, demo, order, featured);
        dialog.getFooter().add(new Button("Cancel", e -> dialog.close()),
                AdminUi.primary("Save project", () -> {
                    try {
                        p.setTitle(title.getValue()); p.setSlug(slug.getValue());
                        p.setSummary(summary.getValue()); p.setDetails(details.getValue());
                        p.setTechnology(technology.getValue()); p.setSourceUrl(source.getValue());
                        p.setDemoUrl(demo.getValue()); p.setSortOrder(order.getValue() == null ? 0 : order.getValue());
                        p.setFeatured(featured.getValue()); service.saveProject(p);
                        dialog.close(); reload(); AdminUi.success("Project saved");
                    } catch (Exception ex) { AdminUi.fail(ex); }
                }));
        dialog.open();
    }
    static TextField text(String label, String value, int limit) {
        TextField field = new TextField(label); field.setWidthFull(); field.setMaxLength(limit);
        field.setValue(value == null ? "" : value); return field;
    }
    static TextArea area(String label, String value, int limit) {
        TextArea field = new TextArea(label); field.setWidthFull(); field.setMaxLength(limit);
        field.setMinHeight("95px"); field.setValue(value == null ? "" : value); return field;
    }
}

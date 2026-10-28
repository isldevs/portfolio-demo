package dev.portfolio.ui.admin;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import dev.portfolio.model.Skill;
import dev.portfolio.service.PortfolioService;
import jakarta.annotation.security.RolesAllowed;

@Route(value = "admin/skills", layout = AdminLayout.class)
@PageTitle("Skills | Portfolio CMS")
@RolesAllowed("ADMIN")
public class AdminSkillsView extends Div {
    private final PortfolioService service;
    private final Grid<Skill> grid = new Grid<>(Skill.class, false);
    public AdminSkillsView(PortfolioService service) {
        this.service = service;
        grid.addColumn(Skill::getName).setHeader("Skill");
        grid.addColumn(Skill::getCategory).setHeader("Category");
        grid.addColumn(Skill::getProficiency).setHeader("Level (%)");
        grid.addComponentColumn(s -> AdminUi.primary("Edit", () -> editor(s))).setHeader("Edit");
        grid.addComponentColumn(s -> AdminUi.danger("Delete", () -> delete(s))).setHeader("Delete");
        reload();
        add(AdminUi.page("Skills", "Showcase the languages, frameworks and tools you know.",
                AdminUi.primary("+ Add skill", () -> editor(new Skill())), grid));
    }
    private void reload() { grid.setItems(service.skills()); }
    private void delete(Skill s) {
        AdminUi.confirm("Delete skill ‘" + s.getName() + "’?", () -> {
            try { service.deleteSkill(s.getId()); reload(); AdminUi.success("Skill deleted"); }
            catch (Exception ex) { AdminUi.fail(ex); }
        });
    }
    private void editor(Skill s) {
        TextField name = AdminProjectsView.text("Skill name", s.getName(), 100);
        TextField category = AdminProjectsView.text("Category", s.getCategory(), 100);
        IntegerField level = new IntegerField("Proficiency 0–100"); level.setMin(0); level.setMax(100);
        level.setValue(s.getProficiency());
        IntegerField order = new IntegerField("Display order"); order.setValue(s.getSortOrder());
        Dialog dialog = AdminUi.dialog(s.getId() == null ? "Add skill" : "Edit skill", name, category, level, order);
        dialog.getFooter().add(new Button("Cancel", e -> dialog.close()),
                AdminUi.primary("Save skill", () -> {
                    try {
                        s.setName(name.getValue()); s.setCategory(category.getValue());
                        s.setProficiency(level.getValue() == null ? 0 : level.getValue());
                        s.setSortOrder(order.getValue() == null ? 0 : order.getValue());
                        service.saveSkill(s); dialog.close(); reload(); AdminUi.success("Skill saved");
                    } catch (Exception ex) { AdminUi.fail(ex); }
                }));
        dialog.open();
    }
}

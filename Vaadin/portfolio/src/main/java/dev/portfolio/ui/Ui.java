package dev.portfolio.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public final class Ui {
    private Ui() {}
    public static Div div(String className, Component... children) {
        Div d = new Div(children); d.addClassName(className); return d;
    }
    public static Span span(String className, String text) {
        Span s = new Span(text); s.addClassName(className); return s;
    }
    public static H2 heading(String text) {
        H2 h = new H2(text); h.addClassName("section-title"); return h;
    }
    public static Div container(Component... children) { return div("page-container", children); }
    public static Div section(Component... children) { return div("section", children); }
    public static Anchor link(String href, String label, String className) {
        Anchor a = new Anchor(href, label); a.addClassName(className); return a;
    }
    public static Anchor external(String url, String label, String className) {
        if (url == null || !(url.startsWith("https://") || url.startsWith("http://"))) {
            return link("#", label, className);
        }
        Anchor a = link(url, label, className);
        a.setTarget("_blank"); a.getElement().setAttribute("rel", "noopener noreferrer");
        return a;
    }
    public static Paragraph paragraph(String text) {
        Paragraph p = new Paragraph(text == null ? "" : text); p.addClassName("body-text"); return p;
    }
    public static Div footer() {
        Div left = div("footer-left", new Span("Built with Java, Vaadin & Spring Boot."));
        Div right = div("footer-right", new Span("© " + java.time.Year.now() + " · Portfolio"));
        return div("site-footer", left, right);
    }
    public static Div pageTitle(String eyebrow, String title, String subtitle) {
        H1 heading = new H1(title); heading.addClassName("page-title");
        return div("page-intro", span("eyebrow", eyebrow), heading, paragraph(subtitle));
    }
}

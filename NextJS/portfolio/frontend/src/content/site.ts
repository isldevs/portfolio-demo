/** EDIT THIS FILE to replace the deliberately fictional sample content. */
export const site = {
  name: "Your Name",
  initials: "YN",
  role: "Software Engineer & Product Builder",
  location: "Phnom Penh, Cambodia",
  availableForWork: true,
  email: "hello@example.com",
  github: "https://github.com/your-username",
  linkedin: "https://www.linkedin.com/in/your-profile/",
  tagline: "I engineer thoughtful digital experiences.",
  description:
    "From elegant interfaces to resilient backend systems, I love turning complex problems into products people actually enjoy using.",
  about:
    "I’m a software engineer who cares about both the system behind the screen and the experience in front of it. I work across Java, Spring Boot, modern frontend technologies, and thoughtful product design.",
  resumeUrl: "", // e.g. /resume.pdf (place the file in frontend/public)
  stats: [
    { value: "05+", label: "Years of learning" },
    { value: "12+", label: "Projects explored" },
    { value: "100%", label: "Curiosity driven" }
  ],
  skills: [
    { category: "Frontend", items: ["Next.js", "React", "TypeScript", "Tailwind CSS", "Accessible UI"] },
    { category: "Backend", items: ["Java", "Spring Boot", "REST APIs", "Spring Security", "JPA / Hibernate"] },
    { category: "Data & Cloud", items: ["PostgreSQL", "Docker", "GitHub Actions", "Linux", "CI/CD"] }
  ],
  experience: [
    { role: "Software Engineer", company: "Your Company", period: "2024 — Present", description: "Developing reliable services and user-friendly products, with an emphasis on maintainable code and practical delivery." },
    { role: "Full-stack Developer", company: "Previous Company", period: "2022 — 2024", description: "Worked across web interfaces, backend integrations, and database-backed applications." }
  ],
  projects: [
    {
      slug: "nexus-banking-platform", number: "01", title: "Nexus Banking Platform", label: "FINTECH · PLATFORM",
      year: "2026", summary: "A modern financial workspace for simplifying complex workflows and providing clearer operational insights.",
      challenge: "Complex financial tasks can become difficult to navigate when information is scattered across disconnected systems.",
      solution: "A modular, API-first application concept with focused workflows, fast search and clear status reporting.",
      outcome: "Demo concept illustrating an extensible financial platform; replace with your own validated project outcomes.",
      stack: ["Java", "Spring Boot", "PostgreSQL", "Next.js"],
      gradient: "blue", icon: "layers", featured: true, links: { demo: "", source: "" }
    },
    {
      slug: "atlas-analytics", number: "02", title: "Atlas Analytics", label: "DATA · DASHBOARD",
      year: "2025", summary: "An analytics dashboard concept that turns noisy information into accessible, actionable visual stories.",
      challenge: "Teams need an approachable way to interpret performance metrics without losing important context.",
      solution: "A responsive dashboard concept using clean information hierarchy, accessible visual components and filtering.",
      outcome: "Demo concept; add real screenshots, measurements and links when you customize the portfolio.",
      stack: ["React", "TypeScript", "REST API", "Charts"],
      gradient: "violet", icon: "chart", featured: true, links: { demo: "", source: "" }
    },
    {
      slug: "flow-task-manager", number: "03", title: "Flow Task Manager", label: "PRODUCTIVITY · SAAS",
      year: "2025", summary: "A streamlined task management experience designed to help small teams stay focused and in sync.",
      challenge: "Productivity tools often introduce unnecessary complexity into otherwise simple workflows.",
      solution: "An intentionally minimal task board concept with understandable priorities and predictable interactions.",
      outcome: "Demo concept; a starting point for showcasing a real SaaS project.",
      stack: ["Next.js", "Tailwind CSS", "Java", "Docker"],
      gradient: "orange", icon: "check", featured: true, links: { demo: "", source: "" }
    }
  ]
} as const;

export type PortfolioProject = (typeof site.projects)[number];

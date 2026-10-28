# Modern Portfolio — Vaadin Flow + Spring Boot + PostgreSQL

A **single-language, full-stack Java portfolio** with a responsive public website, protected content-management screens and a persistent contact inbox. The user interface is built in Java using **Vaadin Flow**. There is **no separate React / Next.js frontend** and no JavaScript application to maintain.

> **Build status:** Generated source code and deployment configuration. A complete Maven compile / Vaadin frontend build / Docker runtime test could not be executed in the generation environment because Maven and Docker were unavailable and Maven Central could not be reached. Run the CI and smoke tests below before advertising the service as production-ready.

## Versions

| Dependency | Version |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.1.2 |
| Vaadin Flow | 25.3.4 |
| PostgreSQL | 17 |
| Node.js (Vaadin production assets) | 24 |
| Maven | 3.9.11 in Docker; 3.8+ locally |
| Caddy | 2 |

Vaadin 25.3 requires Java 21+, Spring Boot 4.1+, Node 24+ for custom frontend bundling. The multi-stage Dockerfile provides these tools automatically.

## What you get

**Public web pages**
- `/` — responsive hero, highlighted projects, technology tags, collaboration CTA
- `/projects` — project portfolio
- `/projects/:slug` — detailed project view with demo/GitHub links
- `/about` — biography, work timeline, technical skills
- `/contact` — validated contact form, sent to private PostgreSQL inbox

**Private admin area**
- `/login` — protected Spring Security login
- `/admin` — portfolio statistics and unread message count
- `/admin/profile` — editable name, headline, introduction, bio, CV and links
- `/admin/projects` — add, edit, delete case studies
- `/admin/skills` — add, edit, delete technology skills
- `/admin/experience` — add, edit, delete experience
- `/admin/inbox` — read and delete contact messages

**Infrastructure**
- Flyway SQL schema migration; Hibernate schema verification (`ddl-auto=validate`)
- Passwords stored as BCrypt hashes; original secret provided by environment variables
- Non-root Java container, PostgreSQL persistent volume, private Docker network
- Caddy reverse proxy with automatic HTTPS for a public domain
- Local and production Docker Compose overlays
- Actuator health check, unit tests, GitHub Actions build
- Secure defaults: no hard-coded admin password, no public database in production, secure cookies with HTTPS, CSRF protection via Vaadin Security configuration

## 1. Local deployment — Docker (recommended)

Prerequisites: Docker Engine / Docker Desktop **with Compose V2**, and Python 3 (for generating secrets).

```bash
# Unzip the project and enter its folder
cd vaadin-portfolio

# Creates a PRIVATE .env with unique DB/admin passwords
python3 scripts/init_env.py

# Build Vaadin frontend, Java JAR and start PostgreSQL + app
# First build downloads Maven and npm dependencies and may take time.
docker compose -f compose.yml -f compose.local.yml up -d --build

# Observe app startup and Flyway migrations
docker compose -f compose.yml -f compose.local.yml logs -f app
```

When started, open:

- **Public website:** http://localhost:8080
- **Admin login:** http://localhost:8080/login
- **Health:** http://localhost:8080/actuator/health

Credentials are found in the **local, private** `.env` file (`ADMIN_USERNAME`, `ADMIN_PASSWORD`). Never commit `.env`, put it in a screenshot, or share it publicly.

Run a basic smoke test:

```bash
bash scripts/smoke.sh http://localhost:8080
```

Stop without deleting your data:

```bash
docker compose -f compose.yml -f compose.local.yml down
```

**WARNING:** `docker compose down -v` removes database volumes and all CMS/contact data.

## 2. Local development — Java IDE

Prerequisites: JDK 21+, Maven 3.8+, Node 24+ and Docker for PostgreSQL.

```bash
python3 scripts/init_env.py
# Start only PostgreSQL and expose it locally
docker compose -f compose.yml -f compose.local.yml up -d database

# Load values from .env into your shell (macOS/Linux only):
set -a
source .env
set +a

# Run in Vaadin development mode
mvn spring-boot:run
```

Development mode requires the optional `vaadin-dev` dependency already included in `pom.xml`.

Windows PowerShell: run the Docker approach, or set `DB_PASSWORD`, `ADMIN_USERNAME`, and `ADMIN_PASSWORD` in your IDE Run Configuration/environment. Docker Compose handles `.env` automatically; a standalone Maven process does not.

> If PostgreSQL is already running on host port `5432`, change the host port mapping in `compose.local.yml` and specify `DB_PORT` when running outside Docker.

## 3. Production deployment — Ubuntu VPS + HTTPS

Prerequisites:

1. Ubuntu VPS with Docker Engine and Compose V2 installed, with appropriate memory for Java, PostgreSQL and the Vaadin build; 2+ GB RAM recommended for runtime and substantially more/temporary swap for a local image build.
2. Public DNS **A/AAAA** record pointing to the VPS, e.g. `portfolio.example.com`.
3. Inbound TCP ports **80** and **443** allowed through the VPS firewall; UDP 443 optional for HTTP/3.
4. A system backup strategy for PostgreSQL and the `.env` configuration.

```bash
cd vaadin-portfolio
python3 scripts/init_env.py

# Update DOMAIN to your actual hostname in .env
nano .env

# Build and launch service + PostgreSQL + Caddy
# Do NOT use compose.local.yml for production.
docker compose -f compose.yml -f compose.prod.yml up -d --build

docker compose -f compose.yml -f compose.prod.yml ps
docker compose -f compose.yml -f compose.prod.yml logs -f app proxy
```

Website: `https://your-public-domain.example`.

Caddy requests and renews certificates automatically when DNS is correct and ports 80/443 are reachable. Caddy handles WebSocket/HTTP upgrade for Vaadin automatically through `reverse_proxy`.

**Production smoke test:**

```bash
bash scripts/smoke.sh https://portfolio.example.com
```

**Important:** Before connecting to the public Internet, replace the generated placeholder biography, profile email, project links, skills, experience, and sample descriptions using `/admin`. Review the contact form and privacy policy requirements for your jurisdiction.

### Changing credentials

Edit `ADMIN_PASSWORD` in `.env` to a new strong random value (16+ characters), then re-create the service:

```bash
docker compose -f compose.yml -f compose.prod.yml up -d --force-recreate app
```

On startup, the application synchronizes the current single admin login from environment variables, safely hashing the password again if it changed. Changing `ADMIN_USERNAME` also deletes old administrative accounts.

For the database, **do not simply replace `DB_PASSWORD` in `.env` on an existing PostgreSQL volume**. PostgreSQL initializes its password only on the first run. Rotate the actual role password inside PostgreSQL first (then update `.env` and recreate the app/container); use a proper maintenance procedure.

### Backup and restore

Take a consistent logical backup; an example from the running compose stack:

```bash
docker compose -f compose.yml -f compose.prod.yml exec -T database \
  sh -c 'pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Fc' > portfolio.backup
```

Keep backups **outside the VPS** in encrypted storage. Practice a restore on a separate staging instance before making critical changes. Do not commit `.backup` files or share contact messages.

## 4. Production build without Docker

```bash
mvn -Pproduction clean package
java -jar target/vaadin-portfolio-1.0.0.jar --spring.profiles.active=prod
```

Provide all required environment variables in the Java process (`DB_HOST`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `ADMIN_USERNAME`, `ADMIN_PASSWORD`). A PostgreSQL instance must already exist.

## 5. Source code layout

```text
vaadin-portfolio/
├── pom.xml
├── Dockerfile
├── compose.yml                  # app and PostgreSQL
├── compose.local.yml            # localhost port mappings
├── compose.prod.yml             # HTTPS Caddy reverse proxy
├── Caddyfile
├── .env.example
├── scripts/
│   ├── init_env.py              # generate random secrets
│   └── smoke.sh                 # basic HTTP smoke tests
├── .github/workflows/ci.yml
└── src/
    ├── main/java/dev/portfolio/
    │   ├── PortfolioApplication.java
    │   ├── config/               # Spring Security, sample data, admin bootstrap
    │   ├── model/                # JPA entities
    │   ├── repo/                 # Spring Data repositories
    │   ├── service/              # business logic and validation
    │   └── ui/
    │       ├── HomeView.java, ProjectsView.java, AboutView.java,
    │       │   ProjectDetailView.java, ContactView.java, LoginView.java
    │       └── admin/            # protected CMS views
    ├── main/resources/
    │   ├── application.yml
    │   ├── db/migration/V1__init.sql
    │   └── META-INF/resources/styles.css
    └── test/java/dev/portfolio/service/  # JUnit + Mockito
```

## 6. Tests and quality gates

Run unit tests:

```bash
mvn test
```

Build optimized production JAR and run unit tests:

```bash
mvn -Pproduction clean verify
```

CI runs the production build on every push/PR and fails if tests fail. Run `scripts/smoke.sh` after deployment. Manual functional QA should still verify: public routes, admin authorization (anonymous users cannot access admin pages), login error display, CRUD operations, persisted data after restart, contact validation/rate-limit, project links, and phone/tablet responsiveness.

## 7. Security / limitations to know

- Contact messages are **saved in PostgreSQL**, not sent to your email. Admin inbox is the notification destination. Add SMTP/email integration separately if required.
- The contact form has **field validation and a session-based 60-second cooldown**, not enterprise-grade bot protection or distributed rate limiting. Add Turnstile/CAPTCHA and proxy-level limits for a high-traffic public deployment.
- This is a **single instance** deployment. Vaadin Flow maintains UI state in the Java server; horizontal scaling requires sticky sessions and capacity planning.
- The public pages are browser-rendered Vaadin views. If extensive SEO/social preview/fully static rendering is a primary objective, Astro/Next.js may be a better frontend.
- Example portfolio entries are fictional demo content and must not be presented as personal accomplishments.
- A complete production handoff requires successful building/running the images in your environment, database migration verification, full browser testing, a domain, a privacy notice for contact submissions, and operational monitoring/backup procedures.
- For production operations, pin images by digest, set up automated image/security scans, configure application logs/alerts, and apply security updates regularly.

## Useful docs

- https://vaadin.com/docs/latest/compatibility
- https://vaadin.com/docs/latest/building-apps/security/add-login
- https://vaadin.com/docs/latest/building-apps/production/build
- https://vaadin.com/docs/latest/styling/stylesheets
- https://docs.spring.io/spring-boot/reference/

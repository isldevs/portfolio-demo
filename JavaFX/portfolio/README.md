# Personal Portfolio — JavaFX + Spring Boot

A complete source-code starter for a **responsive public portfolio website** and a **native JavaFX desktop client**, both using the same Java Spring Boot REST API. The public website can be deployed in Docker on a Linux VPS; the JavaFX desktop application connects to the deployed backend.

> **Architecture note:** JavaFX is a desktop UI toolkit, not a browser frontend framework. To make your portfolio publicly visitable as a website, this project includes responsive HTML/CSS/JavaScript delivered by Spring Boot, plus a separate **real, native JavaFX frontend**. They share the same backend and content.

## Included features

- Responsive portfolio website: About, selected projects with category filters, skills, experience, contact form, dark/light mode, mobile navigation, SEO metadata.
- Native JavaFX desktop: overview, project cards, skills, experience timeline, contact form, live API connectivity status.
- Java 21 / Spring Boot 4.0.9 backend with REST API, bean validation, JPA persistence, Flyway migrations and actuator health checks.
- PostgreSQL contact-message storage; honeypot anti-spam field and basic single-instance in-memory per-IP rate limiting (5 attempts/hour).
- Multi-stage Docker image, non-root app process, Docker Compose, Caddy reverse proxy example (automatic HTTPS after DNS/domain setup), and GitHub Actions CI.
- Placeholder portfolio data in **one file**: `backend/src/main/resources/portfolio.json`.

## Project layout

```text
portfolio-stack/
├── backend/
│   ├── pom.xml                       # Spring Boot 4.0.9 (Java 21)
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/portfolio/
│       │   ├── PortfolioApplication.java
│       │   ├── api/                  # Portfolio endpoint and error handling
│       │   ├── core/                 # Portfolio data loader
│       │   └── contact/              # Validation, persistence, rate limiting
│       ├── main/resources/
│       │   ├── application.yml
│       │   ├── portfolio.json        # CUSTOMIZE THIS
│       │   ├── db/migration/         # PostgreSQL/Flyway
│       │   └── static/               # Public website
│       └── test/                     # Unit tests
├── desktop/
│   ├── pom.xml                       # JavaFX 21 + Java 21
│   └── src/main/                    # Native JavaFX desktop UI
├── deploy/Caddyfile                 # Optional real domain + HTTPS
├── compose.yml                      # Backend + PostgreSQL
├── .env.example
└── .github/workflows/ci.yml
```

## 1. Customize before publishing

Edit `backend/src/main/resources/portfolio.json`:

1. `profile.name`, `initials`, `role`, `headline`, `summary`, `email`, `location`, and `availability`.
2. `profile.social[].url` for your actual GitHub and LinkedIn.
3. `skills`, `projects`, `experience` to reflect your real experience. The example organizations and project statistics are **sample data**, not real claims about you.
4. `projects[].demoUrl` and `sourceUrl` when you have live links; leave empty to hide the links.
5. In `backend/src/main/resources/static/index.html`, update the `<title>`, description, and OpenGraph metadata for search/social previews.
6. Optionally replace the SVG favicon and change colors in `styles.css`.

After editing your profile data, rebuild the backend: `docker compose up -d --build`.

## 2. Deployment with Docker (recommended)

### Prerequisites

- Linux server/VPS with Docker Engine and Compose v2, or Docker Desktop.
- At least 1–2 GB RAM available for the app, database and image build (more is helpful during Maven build).
- A domain and DNS record **only if you want public HTTPS**.

From the repository root:

```bash
cp .env.example .env
# EDIT .env and replace POSTGRES_PASSWORD with a secure random value
# e.g. openssl rand -base64 36

docker compose up -d --build
docker compose ps
curl http://127.0.0.1:8080/actuator/health
```

Open **http://localhost:8080** on the machine running Docker. The published port is intentionally bound to **127.0.0.1** so the API/database are not exposed directly to the internet. Set `PORT` in `.env` to change the local port; PostgreSQL has no published port.

Inspect logs:

```bash
docker compose logs -f app
```

### Production domain + automatic HTTPS (Caddy)

1. Set a DNS `A` record for your domain pointing to the VPS public IP. Open TCP ports **80 and 443** in your firewall.
2. Install Caddy on the host (see https://caddyserver.com/docs/install).
3. Change `your-portfolio.example.com` in `deploy/Caddyfile` to the real hostname.
4. Copy the Caddyfile into your Caddy configuration and reload Caddy:

```bash
sudo cp deploy/Caddyfile /etc/caddy/Caddyfile
sudo systemctl reload caddy
```

Caddy handles HTTPS and proxies to the Docker app at `127.0.0.1:8080`. If you changed `PORT`, update the reverse_proxy port as well. Do not expose the DB to the internet. **TLS cannot become active until you provide a real domain, configure DNS, and deploy to an internet-reachable server.**

### Updating an existing deployment

```bash
git pull
docker compose up -d --build
```

Data persists in the named Docker volume `postgres_data`. **Do not run** `docker compose down -v` unless you intend to delete stored messages.

### Database backups / reading contact requests

```bash
# Back up contact messages and schema (execute from the repository root)
docker compose exec -T db pg_dump -U portfolio -d portfolio > portfolio-backup.sql

# View saved messages in terminal (restrict access to authorized administrators)
docker compose exec db psql -U portfolio -d portfolio \
  -c 'SELECT created_at, name, email, subject, message FROM contact_messages ORDER BY created_at DESC LIMIT 20;'
```

No public inbox endpoint is provided to avoid accidentally exposing submitted contact information. Messages are **saved in PostgreSQL**, not emailed automatically. To receive email alerts, add an SMTP or transactional email integration before launch.

## 3. Run Spring Boot locally (without Docker for app)

Requirements: JDK **21** and Apache Maven (3.9+), with a local PostgreSQL database named `portfolio` and a `portfolio` user.

Linux/macOS:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/portfolio
export DB_USERNAME=portfolio
export DB_PASSWORD='your-password'
cd backend
mvn spring-boot:run
```

Windows PowerShell:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/portfolio"
$env:DB_USERNAME="portfolio"
$env:DB_PASSWORD="your-password"
cd backend
mvn spring-boot:run
```

REST endpoints:

| Method | URL | Purpose |
| --- | --- | --- |
| `GET` | `/` | Public responsive portfolio site |
| `GET` | `/api/v1/portfolio` | Public portfolio JSON |
| `POST` | `/api/v1/contact` | Validated contact submission |
| `GET` | `/actuator/health` | Service/DB health |

Example contact request:

```bash
curl -i http://localhost:8080/api/v1/contact \
  -H 'Content-Type: application/json' \
  -d '{"name":"Jane","email":"jane@example.com","subject":"Hello","message":"I would like to talk about a new project.","website":""}'
```

Expected successful response: **201 Created**. Invalid input returns 400; rate-limited requests return 429.

## 4. Run the native JavaFX frontend

JavaFX is installed as Maven dependencies; no manual JavaFX SDK installation is required. Use **JDK 21**, Maven 3.9+, and ensure the backend is already reachable.

```bash
cd desktop
mvn clean javafx:run
```

The app connects to `http://localhost:8080` by default. To connect to the publicly deployed portfolio:

Linux/macOS:

```bash
PORTFOLIO_API_URL=https://your-portfolio.example.com mvn javafx:run
```

Windows PowerShell:

```powershell
$env:PORTFOLIO_API_URL="https://your-portfolio.example.com"
mvn javafx:run
```

**Important:** JavaFX needs a graphical desktop session. It is not meant to run inside the Docker web container. This repository includes buildable desktop source and a Maven launcher; an OS-specific native installer is not pre-generated. Native installers need to be built on their respective target OS using `jpackage`/JavaFX runtime packaging.

## 5. Test and build

```bash
cd backend
mvn clean test
mvn clean package

cd ../desktop
mvn clean package
```

CI runs backend unit tests and compiles the JavaFX client. A real PostgreSQL integration test suite (e.g., Testcontainers) should be added before scaling beyond a personal portfolio.

## Security and production notes

- Store passwords in `.env` and keep `.env` out of Git. Rotate any password accidentally committed.
- Site/API are same-origin; no permissive CORS configuration is included.
- Contact requests are validated server-side and saved via JPA (no dynamic SQL).
- The honeypot and IP limiter are **basic** spam protection. The server uses Tomcat's native forwarded-header processing, intended for the provided Caddy + loopback proxy topology; verify that the trusted-proxy behavior is correct for your hosting setup. For significant traffic, use CAPTCHA/Turnstile and a shared rate limiter (Redis or edge WAF). Do not blindly trust arbitrary `X-Forwarded-For` headers.
- The provided code is a **deployable starter**; review your legal/privacy requirements when collecting contact messages, add privacy/retention information as appropriate, and audit dependencies before live use.
- Use HTTPS in production and manage server firewall rules, OS updates, monitoring, and backups.
- There is no public admin/login dashboard; contact data should be viewed by trusted operators via secured DB access.

## Versions

- Java **21** (LTS)
- Spring Boot **4.0.9**
- JavaFX **21.0.11** (works with JDK 21; JavaFX 27 needs JDK 25+)
- PostgreSQL **17**

## Troubleshooting

| Problem | Action |
| --- | --- |
| JavaFX says API offline | Start backend; verify `http://localhost:8080/actuator/health`, or set `PORTFOLIO_API_URL` |
| `JAVA_HOME` invalid | Point `JAVA_HOME` to a JDK 21 installation, not a JRE |
| Docker DB won't start | Check `POSTGRES_PASSWORD` and `docker compose logs db` |
| Website has placeholder content | Edit `portfolio.json`, rebuild the app image |
| Form says too many requests | Basic limiter permits five contact attempts per resolved IP per hour; check proxy header configuration if clients appear to share one IP |
| HTTPS not working | Check real DNS, 80/443 inbound firewall rules, and Caddy logs |

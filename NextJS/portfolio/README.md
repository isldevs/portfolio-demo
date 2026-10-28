# Modern Personal Portfolio — Next.js 16 + Spring Boot 4 + PostgreSQL

A customizable personal developer portfolio with a modern responsive website, working database-backed contact form, REST API, Docker Compose, HTTPS-ready Caddy reverse proxy, tests, and CI.

**This is a complete source-code/deployment configuration, NOT an already-hosted live site.** You must provide a VPS, domain name, and valid DNS to publish it. All sample identity and achievements are fictional; customize them before going live.

## Technology stack

| Component | Stack |
|---|---|
| Public website | Next.js 16.4.3 App Router, React 19.3.1, TypeScript, Tailwind CSS v4 |
| Icons | Lucide React |
| Backend | Java 21, Spring Boot 4.0.9, Jakarta Validation, JPA |
| Database | PostgreSQL 17 + Flyway SQL migrations |
| Deployment | Docker Compose, Caddy with automatic HTTPS |
| Continuous integration | GitHub Actions (frontend build/lint/typecheck, backend Maven tests) |

## What works

- Responsive homepage, navigation, project cards, individual case study routes.
- Professionally styled hero, engineering skill tags, experience timeline, contact form.
- Real POST `/api/contact` Next.js handler -> internal Spring Boot API -> PostgreSQL.
- Input validation, same-origin guard, honeypot spam field, hourly per-client rate limit, secret API key between web app and API.
- Docker healthchecks, migrations on startup, non-root runtime containers, private Docker network for Java/DB.
- Search indexing metadata, `robots.txt`, `sitemap.xml`, SVG favicon and OG placeholder.
- Automated Maven unit tests, smoke script, GitHub Actions workflow.

**Not provided:** domain/VPS purchase, SMTP email delivery, authenticated admin UI, external analytics, real project photographs, or a user account system. Contact messages are stored in PostgreSQL, **not emailed**. A portfolio does not require an admin account; portfolio contents are edited in one TypeScript data file.

## Project layout

```text
modern-portfolio/
├─ frontend/
│  ├─ src/app/                 # App Router pages, SEO, internal contact API
│  ├─ src/components/          # Header, footer, visuals, contact form
│  ├─ src/content/site.ts      # YOUR profile, projects, skills, links
│  ├─ public/                  # Favicon and OG image
│  └─ Dockerfile               # Next.js standalone build
├─ backend/
│  ├─ src/main/java/com/portfolio/
│  │  ├─ api/                  # API key filter, validation error handler
│  │  └─ contact/              # Controller, service, JPA model, repository
│  ├─ src/main/resources/db/migration/
│  ├─ src/test/                # ContactService unit tests
│  └─ Dockerfile
├─ deploy/Caddyfile            # HTTPS reverse proxy
├─ scripts/init_env.py        # Generates secure .env values
├─ scripts/smoke_test.py       # Exercises public and API routes
├─ compose.yml                # Private services
├─ compose.local.yml          # Localhost development ports
├─ compose.prod.yml           # HTTPS with Caddy
└─ .github/workflows/ci.yml
```

## Quick start — Docker on Windows, macOS, Linux

**Prerequisites:** Docker Desktop / Docker Engine with Compose V2, Python 3.9+ to generate keys, and sufficient Docker build resources. You do NOT need to install Java, Maven, Node.js or PostgreSQL when using Docker.

```bash
# Inside this extracted project root:
python scripts/init_env.py
# Create local images and start DB, backend, website:
docker compose -f compose.yml -f compose.local.yml up -d --build
# Inspect health:
docker compose -f compose.yml -f compose.local.yml ps
# Optional smoke checks:
python scripts/smoke_test.py http://localhost:3000
```

**Local website:** http://localhost:3000

**Backend health:** http://localhost:8080/actuator/health (local only)

**PostgreSQL:** `localhost:5433` (local only; user `portfolio`; password in `.env`; database `portfolio`).

Initial build will download dependency packages and Docker images; this requires internet access. If `.env` already exists, `init_env.py` deliberately refuses to overwrite secrets.

### Windows PowerShell

```powershell
py scripts/init_env.py
# If `py` is unavailable, try `python`.
docker compose -f compose.yml -f compose.local.yml up -d --build
py scripts/smoke_test.py http://localhost:3000
```

### Troubleshooting

```bash
docker compose -f compose.yml -f compose.local.yml logs -f backend
docker compose -f compose.yml -f compose.local.yml logs -f frontend
# Stop services, KEEP contact submissions
docker compose -f compose.yml -f compose.local.yml down
# Do NOT add -v unless you want to DELETE all PostgreSQL data.
```

If ports 3000, 8080 or 5433 are taken, change the host-side port mapping in `compose.local.yml`.

## Replace the example portfolio content

Edit **`frontend/src/content/site.ts`**:

- `name` and `initials` (both used throughout the website)
- `role`, `description`, `about`, `location`, `email`
- `github`, `linkedin`, `resumeUrl` (add `frontend/public/resume.pdf` when ready)
- `stats`, `skills`, `experience`
- `projects`: `slug`, `title`, `summary`, `challenge`, `solution`, `outcome`, `stack`, `links`

The starter data is **illustrative only**: claims like years of experience and the sample project case studies are fictional. Replace them with your actual accomplishments and links before launch.

To change colors, styles and spacing, edit `frontend/src/app/globals.css`; the UI lives in `frontend/src/app/page.tsx` and `frontend/src/components/`. The visual cards are self-contained illustrations, so no external image server is needed. Replace `frontend/public/og-image.svg` with your personal social preview; update metadata in `frontend/src/app/layout.tsx` if needed.

After any changes to frontend files: `docker compose -f compose.yml -f compose.local.yml up -d --build frontend`.

## Publish to production: VPS + domain + HTTPS

### 1. Provision a server

Use a small Ubuntu VPS with at least **2 GB RAM** (4 GB recommended to build images comfortably), Docker Engine, Compose V2, and sufficient disk. DNS must already point your domain to the server's public IP with an A (and optionally AAAA) record. Allow inbound TCP ports **80 and 443**, and UDP **443**; do not expose database port 5432 or backend port 8080.

### 2. Configure `.env`

If this is a fresh server, run:

```bash
python3 scripts/init_env.py
```

Edit `.env`:

```env
SITE_URL=https://portfolio.yourdomain.com
DOMAIN=portfolio.yourdomain.com
```

Keep the generated POSTGRES_PASSWORD, INTERNAL_API_KEY and IP_HASH_SALT secret; if you copy `.env` from local, preserve your existing database password when moving its volume. Do not commit `.env` to Git or share it in screenshots. Prefer an unprivileged deployment account.

### 3. Start production

```bash
docker compose -f compose.yml -f compose.prod.yml up -d --build
docker compose -f compose.yml -f compose.prod.yml ps
curl -I https://portfolio.yourdomain.com
python3 scripts/smoke_test.py https://portfolio.yourdomain.com
```

Caddy obtains and renews TLS certificates automatically when DNS and ports are configured correctly. It forwards incoming traffic ONLY to Next.js. The backend and DB remain on a private Docker network. Caddy stores cert data in persistent named volumes. `SITE_URL` is embedded at frontend build time, so **rebuild frontend whenever it changes**.

### 4. Updates

```bash
# Backup first; then pull your code changes.
docker compose -f compose.yml -f compose.prod.yml up -d --build
# Prune unused old images occasionally after checking the site.
```

Keep Node.js, Next.js, Spring Boot, PostgreSQL and Caddy updated for security. Avoid blind major upgrades without testing first.

## How the contact API works

```text
Visitor's browser
  POST /api/contact (same public domain)
           │
           ▼
Next.js API route (validates request envelope, checks same-origin)
  X-Internal-Api-Key (server-only environment secret)
           │ Docker private network
           ▼
Spring Boot POST /api/v1/contact
  Jakarta Bean Validation + honeypot + 5 requests per hour per hashed client IP
           │
           ▼
PostgreSQL contact_messages (Flyway migration)
```

The Next.js server gets the client's network address from a trusted ingress proxy forwarded header. If deploying behind a different proxy provider, configure its trusted-forwarded-header policy to prevent spoofing. For substantial public traffic, use a proper edge/WAF rate limiter; the built-in 5/hour database check is intentionally lightweight and does not provide concurrency-safe distributed throttling.

### Read incoming messages (locally)

```bash
docker compose -f compose.yml -f compose.local.yml exec db \
  psql -U portfolio -d portfolio -c \
  'SELECT sender_name, sender_email, subject, message, created_at FROM contact_messages ORDER BY created_at DESC LIMIT 50;'
```

On production, replace the local compose command with `-f compose.yml -f compose.prod.yml` and run from a trusted admin shell. The example query should not be exposed as a public API.

### Retention and privacy

Contact forms collect personal data. Add a privacy notice suitable for your jurisdiction, restrict access to database backups, and decide on data retention. Example cleanup after **180 days**:

```sql
DELETE FROM contact_messages WHERE created_at < now() - interval '180 days';
```

**Email notifications:** Not enabled in this starter. Messages are safely persisted in the database and acknowledged on-screen; you can later integrate an email provider or an authenticated admin inbox.

## Database backup and restore

Backup (from the server, in the root directory):

```bash
# Keep backups somewhere secure OFF the server, with encrypted storage.
docker compose -f compose.yml -f compose.prod.yml exec -T db \
  pg_dump -U portfolio -d portfolio -Fc > portfolio-backup.dump
```

For restore, provision a clean database first, and verify which data will be replaced. Example:

```bash
cat portfolio-backup.dump | docker compose -f compose.yml -f compose.prod.yml exec -T db \
  pg_restore -U portfolio -d portfolio --clean --if-exists --no-owner
```

A restore may conflict with live application connections; use a maintenance window and rehearse recovery on a separate environment.

## Developer mode (without running full Docker app)

Use **Node.js 22+, Java 21, Maven 3.9+**, and a local PostgreSQL instance (or run only the `db` service with `compose.local.yml`). Use the generated `.env` keys as environment variables in both processes. For example:

```bash
# Database only
docker compose -f compose.yml -f compose.local.yml up -d db
# Start backend (separate terminal); ensure env vars are exported
cd backend
DATABASE_URL=jdbc:postgresql://localhost:5433/portfolio mvn spring-boot:run
# Frontend (separate terminal); BACKEND_URL and INTERNAL_API_KEY must be set
cd frontend
npm install
npm run dev
```

For Windows PowerShell use `$env:VARIABLE = 'value'` instead of Bash inline environment variables. Never put internal API secrets into `NEXT_PUBLIC_*` variables. Frontend standalone build requires a running Node server because the `/api/contact` route uses server-side code; **do not use `output: "export"`**.

## Verification

```bash
cd frontend && npm install && npm run typecheck && npm run lint && npm run build
cd ../backend && mvn verify
python scripts/smoke_test.py http://localhost:3000
```

Tests and build commands are included; their successful execution depends on Maven/npm/Docker availability on your machine or CI. The provided archive was source-reviewed and syntax/structure checked, but it has not been deployed to a real server on your behalf.

## Security notes

- Caddy is the sole public entry point in production, with automatic TLS and HSTS.
- Backend and PostgreSQL have NO published ports in production.
- Internal API key must be at least 32 characters; IP hash salt must be at least 32 characters.
- Java server never trusts public submissions without the internal key; Java validation and rate limiting backstop client checks.
- The contact API is intentionally not accessible cross-origin; do not add permissive `Access-Control-Allow-Origin: *`.
- Change any placeholder social links and email before publishing.
- Enable backups, server monitoring, vulnerability updates, and appropriate privacy notices.

## License

MIT. You may modify this source code and publish your own portfolio. Replace fictional sample text and mockup results with truthful, personal information.

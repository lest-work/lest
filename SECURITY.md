# Security Policy

## Supported Versions

| Version | Supported          |
| ------- | ------------------ |
| v0.1.x  | :white_check_mark: |
| v0.2.x  | :white_check_mark: |
| < v0.1  | :x:                |

---

## Reporting a Vulnerability

We take security vulnerabilities seriously. If you discover a security issue, **please do NOT open a public GitHub issue**.

### How to Report

1. **Email**: Send details to the project maintainer directly (or use GitHub's private vulnerability reporting)
2. **GitHub Private Vulnerability Reporting**: Go to the repository's **Security** tab → **Advisories** → **Report a vulnerability**

### What to Include

- Type of vulnerability (e.g., SQL injection, XSS, authentication bypass)
- Full paths of the source file(s) related to the vulnerability
- Location of the affected source code (tag/branch/commit)
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact assessment — how the vulnerability affects the system

### Response Timeline

| Timeline | Action |
|----------|--------|
| < 24 hours | Acknowledge receipt of the report |
| < 7 days | Initial assessment and triage |
| < 30 days | Fix development and verification |
| Coordinated disclosure | Public announcement with fix |

---

## Security Features

### Authentication

- JWT-based stateless authentication
- Token refresh mechanism with Redis session management
- Captcha-based login to prevent brute force attacks
- Configurable token expiration

### Authorization

- Role-based access control (RBAC) with data scope filtering
- Menu-level and button-level permission control
- Service-to-service authentication via Feign interceptors

### Transport Security

- All production endpoints should be served over HTTPS
- CORS whitelist restricts cross-origin requests to trusted domains
- Sensitive endpoints protected by Spring Security filters

### Data Protection

- Passwords hashed with BCrypt (Spring Security default)
- Sensitive data fields annotated with `@Sensitive` for automatic desensitization
- SQL injection prevention via MyBatis parameter binding (no string concatenation)
- File upload size limits enforced at Gateway level

---

## Known Security Considerations

- **Nacos Console** — Should be protected by VPN or IP whitelist in production; do not expose publicly without additional authentication
- **MinIO** — Access keys should be rotated periodically; use bucket policies to limit public access
- **Default Credentials** — Change all default passwords (admin/admin123) before deploying to production
- **API Gateway** — The whitelist mechanism should be reviewed regularly to remove unnecessary entries
- **WebSocket** — Notification WebSocket endpoints use token-based authentication; do not bypass auth in production

---

## Dependency Security

We use multiple tools to monitor dependency vulnerabilities:

- **GitHub Dependency Review** — Runs on every pull request; blocks high-severity vulnerabilities
- **Trivy** — Scans Docker images and filesystem for known CVEs
- **CodeQL** — GitHub-native static analysis for security queries
- **Renovate** — Automated dependency updates to keep libraries current

---

## Security Updates

Security patches are released following our [versioning policy](./CHANGELOG.md). Critical vulnerabilities may receive out-of-band patches. Subscribe to GitHub notifications to stay informed.

---

*Last updated: 2026-05-31*

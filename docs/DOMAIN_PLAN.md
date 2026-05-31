# Domain Name Plan

> **Last Updated**: 2026-05-31

---

## Overview

This document describes the domain name architecture for the LEST Platform. Two top-level domains are registered for this project:

| Domain | Purpose | Status |
|--------|---------|--------|
| **lest.work** | Product platform (app, API, docs) | Active |
| **lest.top** | Official marketing website | Reserved |

---

## Domain Allocation

### lest.work — Product Platform

`lest.work` hosts all production-facing services for the LEST Platform.

| Subdomain | Service | Description | Port |
|-----------|---------|-------------|------|
| `app.lest.work` | Frontend Web App | Vue 3 admin dashboard | 443 (HTTPS) |
| `api.lest.work` | API Gateway | Spring Cloud Gateway | 443 (HTTPS) |
| `doc.lest.work` | API Documentation | Swagger UI / Knife4j | 443 (HTTPS) |
| `nacos.lest.work` | Nacos Console | Service registry & config center | 8848 |
| `minio.lest.work` | MinIO Console | Object storage dashboard | 9001 |
| `sentinel.lest.work` | Sentinel Dashboard | Flow control & circuit breaker | 8858 |

### lest.top — Official Website

`lest.top` is reserved for the product's official marketing website.

| Subdomain | Service | Description | Status |
|-----------|---------|-------------|--------|
| `lest.top` | Marketing Site | Product introduction, features, pricing | Reserved (not yet developed) |
| `docs.lest.top` | Documentation Hub | User guides, API reference | Reserved |
| `blog.lest.top` | Blog | Engineering blog, changelog | Reserved |

---

## Production Architecture

```
                         Internet
                              │
                              ▼
                    ┌─────────────────┐
                    │   lest.top      │  (Official Website - Reserved)
                    │  lest.work      │  (Product Platform)
                    │   Nginx / CDN    │
                    └────────┬────────┘
                             │
            ┌────────────────┼────────────────┐
            │                │                │
            ▼                ▼                ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │ app.lest.work│  │ api.lest.work│  │ doc.lest.work│
    │  Vue 3 SPA   │  │  API Gateway │  │  Swagger UI  │
    │   (HTTPS)    │  │   (HTTPS)    │  │   (HTTPS)    │
    └──────┬───────┘  └──────┬───────┘  └──────────────┘
           │                  │
           │  HTTPS /api/*   │
           └──────────────────┘
                              │
                    ┌─────────▼─────────┐
                    │   lest.work        │
                    │  Nginx Reverse     │
                    │     Proxy          │
                    └─────────┬─────────┘
                              │
            ┌─────────────────┼─────────────────┐
            │                 │                 │
            ▼                 ▼                 ▼
    ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
    │ lest-gateway │  │   Nacos      │  │   MinIO      │
    │   :8080      │  │   :8848     │  │   :9000      │
    └──────┬───────┘  └──────────────┘  └──────────────┘
           │
    ┌──────┴──────┬──────┬──────┬──────┬──────┐
    ▼              ▼      ▼      ▼      ▼      ▼
 lest-auth   lest-system lest-project lest-task lest-release lest-job
   :8096         :8081      :8082      :8083     :8087       :9203
```

---

## Local Development URLs

When developing locally, use the following endpoints:

| Service | URL |
|---------|-----|
| Frontend Dev Server | http://localhost:5173 |
| API Gateway | http://localhost:8080 |
| API Docs | http://localhost:8080/doc.html |
| Nacos Console | http://localhost:8848/nacos |
| MinIO Console | http://localhost:9001 |

### How Local Development Works (No Hosts File Needed)

The key insight is that **frontend and backend are served on different ports locally**, and Vite's built-in proxy handles the cross-origin API requests automatically:

```
Browser (http://localhost:5173)
    │
    │  GET /api/auth/login
    │  (Vite proxy intercepts this)
    ▼
Vite Dev Server (localhost:5173)
    │  forwards /api/* → http://localhost:8080/*
    ▼
Spring Cloud Gateway (localhost:8080)
```

This means:
- **No hosts file editing required** — `app.lest.work` only needs to be configured for production
- **Same API path in all environments** — both local and production use `/api/*`
- **Frontend `.env.development`** sets `VITE_API_URL=/api`, so all API calls go through the Vite proxy
- **Frontend `.env.staging` and `.env.production`** set `VITE_API_URL=https://api.lest.work` for production

The production build (built with `npm run build`) has the domain hardcoded, so it connects directly to `https://api.lest.work` without any proxy.

---

## Environment Configuration

### Frontend (`frontend-pc/`)

```bash
# .env.development (local dev)
VITE_API_URL=/api

# .env.staging (pre-production)
VITE_API_URL=https://api.lest.work

# .env.production (production)
VITE_API_URL=https://api.lest.work
```

### Backend Gateway CORS

The API Gateway allows requests from the following origins:

```yaml
allowedOriginPatterns:
  - http://localhost:*
  - https://app.lest.work
  - https://lest.top
  - https://*.lest.work
```

---

## DNS Configuration Guide

### lest.work (Production)

Configure the following DNS A records:

| Host | Type | Value | TTL |
|------|------|-------|-----|
| app | A | `<server-ip>` | 300 |
| api | A | `<server-ip>` | 300 |
| doc | A | `<server-ip>` | 300 |
| nacos | A | `<server-ip>` | 300 |
| minio | A | `<server-ip>` | 300 |
| sentinel | A | `<server-ip>` | 300 |

### lest.top (Marketing Site)

Reserve for future development. Suggested DNS:

| Host | Type | Value | TTL |
|------|------|-------|-----|
| @ | A | `<server-ip>` | 300 |
| www | CNAME | lest.top | 300 |
| docs | CNAME | app.lest.work | 300 |

---

## TLS / SSL

All production services should use HTTPS. Recommended approach:

- **Let's Encrypt** (free, automated) for TLS certificates
- Wildcard certificate for `*.lest.work` is preferred
- HSTS should be enabled on all HTTPS endpoints

### Nginx Configuration Example

```nginx
server {
    listen 443 ssl;
    server_name app.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.pem;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    location / {
        proxy_pass http://localhost:5173;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

server {
    listen 443 ssl;
    server_name api.lest.work;

    ssl_certificate /etc/nginx/ssl/lest.work.pem;
    ssl_certificate_key /etc/nginx/ssl/lest.work.key;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## Backend Service Ports

| Service | Internal Port | Description |
|---------|--------------|-------------|
| Gateway | 8080 | API Gateway |
| Auth | 8096 | Authentication Service |
| System | 8081 | System Management |
| Project | 8082 | Project Management |
| Task | 8083 | Task Management |
| Release | 8087 | Release Management |
| Job | 9203 | Job Scheduler |
| File | 8091 | File Service |
| Monitor | 9100 | Monitor Service |

---

## Security Considerations

1. **CORS**: Only `app.lest.work` and `lest.top` are allowed as frontend origins
2. **Nacos**: Should be protected behind VPN or IP whitelist in production
3. **MinIO**: Access keys should be rotated periodically
4. **Sentinel**: Dashboard should not be publicly exposed without authentication

---

## Future Subdomains

Reserved for future use on `lest.work`:

| Subdomain | Planned Use |
|-----------|-------------|
| `m.lest.work` | Mobile H5 site |
| `admin.lest.work` | Super admin panel |
| `status.lest.work` | Status page / uptime monitor |
| `cdn.lest.work` | CDN origin for static assets |

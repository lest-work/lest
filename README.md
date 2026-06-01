<p align="center">
  <img src="docs/assets/logo.png" alt="LEST Platform" width="120" />
</p>

<h1 align="center">LEST Platform</h1>

<p align="center">
  An open-source, cloud-native project management platform built for modern engineering teams.
</p>

<p align="center">
<!-- Badges row 1 -->
<a href="https://github.com/lest-work/lest/tags"><img src="https://img.shields.io/github/v/tag/lest-work/lest?sort=semver&style=flat-square" alt="Latest tag"></a>
<a href="https://github.com/lest-work/lest/releases"><img src="https://img.shields.io/github/v/release/lest-work/lest?sort=semver&style=flat-square&label=release" alt="Latest release"></a>
<a href="https://github.com/lest-work/lest/releases"><img src="https://img.shields.io/github/release-date/lest-work/lest?style=flat-square&label=released" alt="Release date"></a>
<a href="https://github.com/lest-work/lest/blob/main/LICENSE"><img src="https://img.shields.io/github/license/lest-work/lest?style=flat-square" alt="License"></a>
<a href="https://github.com/lest-work/lest/actions/workflows/ci.yml"><img src="https://img.shields.io/github/actions/workflow/status/lest-work/lest/ci.yml?branch=main&style=flat-square" alt="CI"></a>
<a href="https://github.com/lest-work/lest/issues"><img src="https://img.shields.io/github/issues/lest-work/lest?style=flat-square" alt="Issues"></a>
<a href="https://github.com/lest-work/lest/stargazers"><img src="https://img.shields.io/github/stars/lest-work/lest?style=flat-square" alt="Stars"></a>
<a href="https://github.com/lest-work/lest/fork"><img src="https://img.shields.io/github/forks/lest-work/lest?style=flat-square" alt="Forks"></a>
<br/>
<!-- Badges row 2 -->
<a href="https://github.com/lest-work/lest/pkgs/container/lest-platform"><img src="https://img.shields.io/badge/GHCR-ghcr.io/lest--work/lest--platform-2496ED?style=flat-square&logo=github" alt="GHCR"></a>
<a href="https://hub.docker.com/u/yshan2028"><img src="https://img.shields.io/badge/Docker%20Hub-yshan2028-2496ED?style=flat-square&logo=docker" alt="Docker Hub"></a>
<a href="https://www.oracle.com/java/technologies/downloads/#java21"><img src="https://img.shields.io/badge/JDK-21+-blue?style=flat-square&logo=openjdk" alt="JDK"></a>
<a href="https://spring.io/projects/spring-boot"><img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-green?style=flat-square&logo=spring" alt="Spring Boot"></a>
<a href="https://v3.vuejs.org"><img src="https://img.shields.io/badge/Vue-3.x-42b883?style=flat-square&logo=vuedotjs" alt="Vue"></a>
<a href="https://nestjs.com"><img src="https://img.shields.io/badge/TypeScript-5.x-3178C6?style=flat-square&logo=typescript" alt="TypeScript"></a>
<a href="https://docker.com"><img src="https://img.shields.io/badge/Docker-24.x-2496ED?style=flat-square&logo=docker" alt="Docker"></a>
<a href="https://github.com/lest-work/lest/discussions"><img src="https://img.shields.io/badge/Discussions-Welcome-blueviolet?style=flat-square" alt="Discussions"></a>
</p>

<p align="center">
  <a href="README.zh-CN.md"><img src="https://img.shields.io/badge/文档-中文版-red?style=flat-square" alt="中文文档"></a>
  <a href="https://github.com/lest-work/lest/releases"><img src="https://img.shields.io/badge/Releases-Changelog-brightgreen?style=flat-square" alt="Releases"></a>
  <a href="https://github.com/lest-work/lest/discussions"><img src="https://img.shields.io/badge/Community-Discussions-blueviolet?style=flat-square" alt="Discussions"></a>
  <a href="https://github.com/lest-work/lest/issues/new/choose"><img src="https://img.shields.io/badge/❮%20Issues-Report%20Bug-orange?style=flat-square" alt="Report Bug"></a>
</p>

---

## 📖 Overview

**LEST Platform** is a full-featured, cloud-native project & task management platform targeting software engineering teams. It provides an integrated environment for project lifecycle management, sprint planning, task tracking, work-log recording, release management, and team collaboration.

The backend is inspired by and architecturally aligned with the battle-tested [RuoYi-Cloud](https://ruoyi.vip) microservice framework. The frontend admin UI is built on top of [EleAdmin Pro](https://eleadmin.com), a premium Vue 3 component library.

> **Note on Licensing** — The core frameworks we depend on offer official commercial licenses. If you use RuoYi in a commercial product, consider supporting the authors at [ruoyi.vip](https://ruoyi.vip). If you adopt EleAdmin Pro in production, purchasing an official license at [eleadmin.com](https://eleadmin.com) is the right thing to do and directly funds ongoing development.

---

## ✨ Features

| Module | Highlights |
|--------|-----------|
| **Auth Service** | Captcha login, JWT token issuance & refresh, Redis session management |
| **System Management** | User / Role / Menu / Dept / Post / Dictionary / Config / Notice |
| **Audit Logs** | Operation logs, login logs, online user management, force logout |
| **Scheduler** | Quartz-based job scheduler with execution history |
| **Project Management** | Project CRUD, archive, member management, template support (Agile / Kanban / Waterfall) |
| **Sprint / Iteration** | Sprint planning, status lifecycle, milestone timeline |
| **Task Management** | Task CRUD, priority & type labeling, sub-tasks, assignment, due dates |
| **Kanban Board** | Three-column board (Todo / In Progress / Done), filter by project & iteration |
| **Work Log** | Per-task time logging with estimated vs actual hours tracking |
| **File Service** | File upload/download backed by MinIO object storage |
| **Dashboard** | Real-time activity feed, member online status, project progress cards |

---

## 🏗️ Architecture

```
                ┌──────────────────────────────────┐
                │          Nginx / Frontend          │
                │   Vue 3 + TypeScript + EleAdmin    │
                └────────────────┬─────────────────┘
                                 │ HTTP /api/*
                ┌────────────────▼─────────────────┐
                │          API Gateway [8080]        │
                │   Spring Cloud Gateway + JWT Auth  │
                └──┬────┬────┬────┬────┬────┬──────┘
                   │    │    │    │    │    │
        ┌──────────▼─┐ ┌▼──┐ ┌▼──┐ ┌▼──┐ ┌▼──┐  ┌──────┐
        │ lest-auth  │ │sys│ │prj│ │tsk│ │job│  │ ...  │
        │  [8096]    │ │81 │ │82 │ │83 │ │93 │  │      │
        └────────────┘ └───┘ └───┘ └───┘ └───┘  └──────┘
                   │    │    │    │    │
        ┌──────────▼────▼────▼────▼────▼──────────────┐
        │          Nacos (Service Registry + Config)    │
        │          MySQL 8  ·  Redis 7  ·  Kafka        │
        │          MinIO  ·  Sentinel                   │
        └───────────────────────────────────────────────┘
```

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| **Backend Framework** | Spring Boot `4.0.3` + Spring Cloud `2025.1.0` + Spring Cloud Alibaba `2025.1.0.0` |
| **Database ORM** | Native MyBatis + PageHelper (no Lombok, explicit accessors) |
| **Security** | Spring Security + JWT (`jjwt 0.12.7`) |
| **Service Registry** | Nacos v2.x |
| **Cache** | Redis 7 + Spring Cache |
| **Message Queue** | Apache Kafka |
| **Object Storage** | MinIO |
| **Scheduler** | Quartz |
| **Frontend** | Vue 3 + TypeScript + Element Plus + Vite |
| **UI Library** | [EleAdmin Pro](https://eleadmin.com) |
| **Build** | Maven 3.9+ (multi-module flat layout) |
| **Container** | Docker + Docker Compose |

---

## 📦 Module Structure

```
lest-platform/
├── backend/                    # Java backend (Maven multi-module)
│   ├── lest-common/            # Shared libraries (core/security/log/redis/...)
│   ├── lest-auth/              # Authentication service            [8096]
│   ├── lest-gateway/           # API Gateway                       [8080]
│   ├── lest-api/               # Feign client interfaces
│   └── lest-modules/
│       ├── lest-system/        # System management                 [8081]
│       ├── lest-project/       # Project management                [8082]
│       ├── lest-task/          # Task management                   [8083]
│       ├── lest-release/       # Release management                [8087]
│       ├── lest-job/           # Job scheduler                     [8093]
│       └── lest-file/          # File service                      [8091]
├── frontend-pc/                # Admin web application (Vue 3 + TS)
├── frontend-h5/                # Mobile H5 frontend
├── frontend-app/               # Native mobile app
└── docs/                       # Architecture, API, PRD, task docs
```

---

## 🚀 Quick Start

### Prerequisites

| Dependency | Minimum Version |
|-----------|----------------|
| JDK | 21 |
| Maven | 3.9 |
| Docker | 24.x |
| Docker Compose | 2.x |
| Node.js | 18+ |

### Option 1 — Pull Official Images (Recommended for Deployment)

```bash
# Pull all 16 service images from GitHub Container Registry (no login required for public repos)
for svc in gateway auth modules-system modules-project modules-task modules-release modules-job modules-file modules-meeting modules-notification modules-ai modules-open modules-performance modules-plugin modules-wakapi visual-monitor; do
  docker pull ghcr.io/lest-work/lest-platform/${svc}:latest
done

# Then start infrastructure services
docker compose -f backend/docker/docker-compose.local.yaml up -d

# See docs/guide/DEPLOYMENT.md for full deployment guide with image-based docker-compose.yml
```

### Option 2 — Build from Source (Recommended for Development)

```bash
# Clone the repository
git clone https://github.com/lest-work/lest.git
cd lest-platform

# Start infrastructure services (Nacos, MinIO)
docker compose -f backend/docker/docker-compose.local.yaml up -d

# Start backend services (each in its own terminal or via scripts in backend/docker/bin/)
# See backend/docker/README.md for per-service startup scripts

# The frontend dev server (hot-reload)
cd frontend-pc
npm install
npm run dev
```

---

## 🌐 Service Endpoints

### Local Development

When developing locally, the Vite dev server proxies `/api/*` requests to `localhost:8080` — **no domain or hosts file configuration needed**.

| Service | Local URL | Credentials |
|---------|-----------|-------------|
| **Frontend** | http://localhost:5173 | admin / admin123 |
| **API Gateway** | http://localhost:8080 | — |
| **API Docs** | http://localhost:8080/doc.html | — |
| **Nacos Console** | http://localhost:8848/nacos | nacos / nacos |
| **MinIO Console** | http://localhost:9001 | minioadmin / minioadmin |

### Production Environment

> The following endpoints are hosted on `lest.work`. See [docs/guide/DOMAIN_PLAN.md](./docs/guide/DOMAIN_PLAN.md) for full domain architecture.

| Service | Production URL | Credentials |
|---------|---------------|-------------|
| **Web App** | https://app.lest.work | admin / admin123 |
| **API Gateway** | https://api.lest.work | — |
| **API Docs** | https://doc.lest.work | — |
| **Nacos Console** | https://nacos.lest.work | nacos / nacos |
| **MinIO Console** | https://minio.lest.work | minioadmin / minioadmin |

> **Official Website** — Visit [https://lest.top](https://lest.top) for product introduction.

---

## 📋 Roadmap

> Track progress at a glance. See full milestone details at [docs/MILESTONES.md](./docs/MILESTONES.md).

```
v0.1.0          v0.2.0          v0.3.0          v0.4.0          v0.5.0          v1.0.0
  |               |               |               |               |               |
  ●───────────────●───────────────○               ○               ○               ○  Foundation
  ●───────────────●───────────────○               ○               ○               ○  Auth
  ●───────────────●───────────────○               ○               ○               ○  System Mgmt
  ●───────────────●───────────────○               ○               ○               ○  Gateway
  ●───────────────●───────────────○               ○               ○               ○  Project BE
  ●───────────────●───────────────○               ○               ○               ○  Task BE
  ○───────────────●───────────────○               ○               ○               ○  Project UI
  ○───────────────●───────────────○               ○               ○               ○  Task UI
  ○───────────────●───────────────○               ○               ○               ○  Release BE
  ○───────────────○───────────────●               ○               ○               ○  Burndown Chart
  ○───────────────○───────────────●               ○               ○               ○  Kanban DnD
  ○───────────────○───────────────●               ○               ○               ○  Worklog/Comment
  ○───────────────○───────────────○               ●               ○               ○  Notification
  ○───────────────○───────────────○               ●               ○               ○  Meeting
  ○───────────────○───────────────○               ●               ○               ○  Release UI
  ○───────────────○───────────────○               ○               ●               ○  CI Integration
  ○───────────────○───────────────○               ○               ●               ○  WakaTime
  ○───────────────○───────────────○               ○               ●               ○  Performance
  ○───────────────○───────────────○               ○               ○               ●  AI Assistant
  ○───────────────○───────────────○               ○               ○               ●  Mobile App
  ○───────────────○───────────────○               ○               ○               ●  Plugin System

  ✅ Released      ✅ Released      🔵 Planned      🔵 Planned      🔵 Planned      🔵 Planned
```

| Version | Theme | Target Date | Status |
|---------|-------|------------|--------|
| **v0.1.0** | Foundation — Auth, System, Gateway, Dashboard | 2026-05-29 | ✅ Released |
| **v0.2.0** | Project & Task Frontend Pages, DDL, API completion | 2026-05-30 | ✅ Released |
| **v0.3.0** | Burndown chart, Kanban DnD, Task worklog/comment panel | 2026-06-05 | 🔵 Planned |
| **v0.4.0** | Notification (WebSocket), Meeting, Release UI | 2026-06-12 | 🔵 Planned |
| **v0.5.0** | CI Integration, WakaTime, Performance dashboard | 2026-06-19 | 🔵 Planned |
| **v0.6.0–v0.9.0** | Advanced modules — AI, Plugin, Open Platform | 2026-07 | 🔵 Planned |
| **v1.0.0** | Stable release — full feature set, mobile app, docs | 2026-08-07 | 🔵 Planned |

See the full changelog at [CHANGELOG.md](./CHANGELOG.md) and milestone roadmap at [docs/MILESTONES.md](./docs/MILESTONES.md).

---

## 🤝 Contributing

We welcome contributions! Please read our guidelines before submitting any work.

| Resource | Description |
|----------|-------------|
| [CONTRIBUTING.md](./CONTRIBUTING.md) | Contribution guidelines, code style, commit conventions |
| [docs/guide/DEVELOPMENT.md](./docs/guide/DEVELOPMENT.md) | Development guide (environment, standards, code style) |
| [docs/reference/ARCHITECTURE.md](./docs/reference/ARCHITECTURE.md) | System architecture (microservices, deployment, service communication) |
| [docs/reference/api/API.en.md](./docs/reference/api/API.en.md) | API reference (English) |
| [docs/guide/BRANCHING.md](./docs/guide/BRANCHING.md) | Branch naming, Git Flow workflow, commit message format |
| [CHANGELOG.md](./CHANGELOG.md) | Release history and version conventions |
| [docs/MILESTONES.md](./docs/MILESTONES.md) | Feature roadmap and milestone planning |

### Quick Start

```bash
# 1. Fork the repository and clone your fork
git clone https://github.com/YOUR_USERNAME/lest.git
cd lest-platform

# 2. Add the upstream remote
git remote add upstream https://github.com/lest-work/lest.git

# 3. Create a feature branch from develop
git checkout -b feature/your-feature-name

# 4. Make your changes and commit (follow Conventional Commits)
git commit -m "feat(project): add burndown chart component"

# 5. Keep your fork in sync with upstream
git fetch upstream
git rebase upstream/develop

# 6. Push and open a Pull Request
git push origin feature/your-feature-name
```

> See [CONTRIBUTING.md](./CONTRIBUTING.md) for the full guide including coding standards, testing requirements, and PR checklist.

---

## 🔒 Security

If you discover a security vulnerability, please follow our [SECURITY.md](./SECURITY.md) for responsible disclosure. Do **not** open a public issue for security problems.

---

## 🙏 Acknowledgements

This project stands on the shoulders of giants. We sincerely thank:

- **[RuoYi-Cloud](https://ruoyi.vip)** — The backend microservice architecture, security framework, permission model, and code generation patterns of LEST Platform are deeply inspired by RuoYi-Cloud. It is one of the most comprehensive open-source Java microservice scaffolds in the Chinese developer community. **If you use RuoYi in a commercial product, please consider purchasing an official license or sponsoring the team at [ruoyi.vip](https://ruoyi.vip).**

- **[EleAdmin Pro](https://eleadmin.com)** — The frontend admin UI of LEST Platform is built upon EleAdmin Pro, a beautifully designed Vue 3 + Element Plus component library. It delivers an exceptional developer experience with rich pre-built components. **If you adopt EleAdmin Pro for your own projects, we strongly encourage you to purchase an official commercial license at [eleadmin.com](https://eleadmin.com) to support continued development.**

- [Spring Boot](https://spring.io/projects/spring-boot) / [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba) / [Vue 3](https://vuejs.org) / [Element Plus](https://element-plus.org) — and all other open-source dependencies that make this project possible.

---

## 📄 License

This project is licensed under the **Apache License 2.0** — see the [LICENSE](./LICENSE) file for details.

> The Apache License 2.0 applies to the LEST Platform source code itself. Please respect the individual licenses of third-party dependencies, particularly the commercial components listed in the Acknowledgements section above.


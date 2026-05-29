<p align="center">
  <img src="docs/assets/logo.png" alt="LEST Platform" width="120" />
</p>

<h1 align="center">LEST Platform</h1>

<p align="center">
  An open-source, cloud-native project management platform built for modern engineering teams.
</p>

<p align="center">
  <a href="https://github.com/yshan2028/Lest/releases"><img src="https://img.shields.io/badge/version-v0.2.0-brightgreen.svg" alt="version"></a>
  <img src="https://img.shields.io/badge/license-MIT-blue.svg" alt="license">
  <img src="https://img.shields.io/badge/JDK-21+-blue.svg" alt="jdk">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.3-green.svg" alt="spring boot">
  <img src="https://img.shields.io/badge/Vue-3.x-42b883.svg" alt="vue">
  <a href="README.zh-CN.md"><img src="https://img.shields.io/badge/文档-中文版-red.svg" alt="中文文档"></a>
</p>

<p align="center">
  <a href="README.zh-CN.md">中文文档</a> ·
  <a href="https://github.com/yshan2028/Lest/issues">Report Bug</a> ·
  <a href="https://github.com/yshan2028/Lest/issues">Request Feature</a>
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

### Option 1 — Docker Compose (Recommended)

```bash
# Clone the repository
git clone https://github.com/yshan2028/Lest.git
cd lest-platform

# Start all infrastructure + services
docker compose -f docker-compose.dev.yml up -d

# The frontend dev server (hot-reload)
cd frontend-pc
npm install
npm run dev
```

### Option 2 — Local Development

```bash
# 1. Start infrastructure (MySQL, Redis, Nacos)
docker compose -f docker-compose.dev.yml up mysql redis nacos -d

# 2. Build backend
cd backend
mvn clean install -DskipTests

# 3. Start services individually (in separate terminals)
./bin/run-auth.sh
./bin/run-gateway.sh
./bin/run-system.sh

# 4. Start frontend
cd ../frontend-pc
npm run dev
```

---

## 🌐 Service Endpoints

| Service | URL | Credentials |
|---------|-----|-------------|
| **Frontend** | http://localhost:5173 | admin / admin123 |
| **API Gateway** | http://localhost:8080 | — |
| **Swagger UI** | http://localhost:8080/doc.html | — |
| **Nacos Console** | http://localhost:8848/nacos | nacos / nacos |
| **MinIO Console** | http://localhost:9001 | minioadmin / minioadmin |

---

## 📋 Roadmap

| Version | Theme | Status |
|---------|-------|--------|
| **v0.1.0** | Foundation — Auth, System, Gateway, Dashboard | ✅ Released |
| **v0.2.0** | Project & Task Frontend Pages, DDL, API completion | ✅ Released |
| **v0.3.0** | Burndown charts, Task worklog/comment panel, Kanban DnD | 🔵 Planned |
| **v0.4.0** | Release management UI, Webhook integration | 🔵 Planned |
| **v1.0.0** | Stable release, mobile app, full documentation | 🔵 Planned |

See the full changelog at [CHANGELOG.md](./CHANGELOG.md) and milestone roadmap at [docs/MILESTONES.md](./docs/MILESTONES.md).

---

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

1. Fork the repository
2. Create your branch: `git checkout -b feat/your-feature`
3. Commit your changes: `git commit -m 'feat: add some feature'`
4. Push to the branch: `git push origin feat/your-feature`
5. Open a Pull Request

Please read [CONTRIBUTING.md](./CONTRIBUTING.md) for details on our code of conduct and submission process.

---

## 🙏 Acknowledgements

This project stands on the shoulders of giants. We sincerely thank:

- **[RuoYi-Cloud](https://ruoyi.vip)** — The backend microservice architecture, security framework, permission model, and code generation patterns of LEST Platform are deeply inspired by RuoYi-Cloud. It is one of the most comprehensive open-source Java microservice scaffolds in the Chinese developer community. **If you use RuoYi in a commercial product, please consider purchasing an official license or sponsoring the team at [ruoyi.vip](https://ruoyi.vip).**

- **[EleAdmin Pro](https://eleadmin.com)** — The frontend admin UI of LEST Platform is built upon EleAdmin Pro, a beautifully designed Vue 3 + Element Plus component library. It delivers an exceptional developer experience with rich pre-built components. **If you adopt EleAdmin Pro for your own projects, we strongly encourage you to purchase an official commercial license at [eleadmin.com](https://eleadmin.com) to support continued development.**

- [Spring Boot](https://spring.io/projects/spring-boot) / [Spring Cloud Alibaba](https://github.com/alibaba/spring-cloud-alibaba) / [Vue 3](https://vuejs.org) / [Element Plus](https://element-plus.org) — and all other open-source dependencies that make this project possible.

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](./LICENSE) file for details.

> The MIT License applies to the LEST Platform source code itself. Please respect the individual licenses of third-party dependencies, particularly the commercial components listed in the Acknowledgements section above.


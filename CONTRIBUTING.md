# Contributing to LEST Platform

Thank you for your interest in contributing to LEST Platform! This document provides guidelines and instructions for contributing.

---

## Ways to Contribute

- **Report bugs** — Open an issue with detailed reproduction steps
- **Suggest features** — Start a discussion in GitHub Discussions
- **Improve documentation** — Fix typos, improve clarity, translate
- **Submit code** — Fix bugs, implement features, refactor

---

## Development Setup

### Prerequisites

| Tool | Version |
|------|---------|
| JDK | 21+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| Docker | 24.x |
| Docker Compose | 2.x |

### Local Environment

```bash
# 1. Clone and start infrastructure
git clone https://github.com/lest-work/lest.git
cd lest-platform
docker compose -f backend/docker/docker-compose.local.yaml up -d

# 2. Build backend
cd backend
mvn clean install -DskipTests

# 3. Start backend services
# (see backend/docker/README.md for individual service startup)

# 4. Start frontend
cd ../frontend-pc
npm install
npm run dev
```

---

## Branching Strategy

We follow a simplified Git Flow. See [docs/BRANCHING.md](./docs/BRANCHING.md) for the full guide.

```
main           ← Production-ready code
  └─ develop   ← Integration branch (all features merge here)
       ├─ feature/*   ← New features
       ├─ fix/*       ← Bug fixes
       └─ chore/*     ← Dependencies, configs
```

**Key rules:**
- All development happens on `feature/*`, `fix/*`, or `chore/*` branches
- All branches are cut from and merged back into `develop`
- `main` only receives merges from `develop` or `hotfix/*`
- Never commit directly to `main` or `develop`

---

## Commit Message Format

We use [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

[optional body]

[optional footer]
```

### Types

| Type | Description | Example |
|------|-------------|---------|
| `feat` | New feature | `feat(task): add worklog panel` |
| `fix` | Bug fix | `fix(login): captcha not refreshing` |
| `docs` | Documentation | `docs: update API documentation` |
| `refactor` | Code refactoring | `refactor(project): simplify mapper` |
| `perf` | Performance improvement | `perf(task): optimize list query` |
| `test` | Adding or updating tests | `test(project): add integration tests` |
| `chore` | Build, CI, dependencies | `chore: upgrade spring boot to 4.0.4` |
| `style` | Code formatting | `style: format java files` |
| `build` | Build system changes | `build: update maven wrapper` |
| `ci` | CI/CD pipeline changes | `ci: add sonarqube scan` |

### Scopes

`auth`, `gateway`, `system`, `project`, `task`, `release`, `job`, `file`, `meeting`, `notification`, `ai`, `frontend`, `sql`, `docker`, `docs`

### Examples

```bash
git commit -m "feat(task): add kanban drag-and-drop support"
git commit -m "fix(project): correct iteration date validation"
git commit -m "docs: add Docker deployment troubleshooting guide"
git commit -m "chore: upgrade Vue 3 to 3.5.2"
git commit -m "perf(gateway): reduce token validation latency"
```

---

## Pull Request Process

### Before Submitting

1. **Sync with upstream** — rebase onto the latest `develop`
2. **Run locally** — ensure all builds and tests pass
3. **Self-review** — check for debug code, commented-out logic, missing Javadocs
4. **Scope** — keep PRs small and focused (one feature or one fix per PR)

### PR Checklist

- [ ] Code follows the project's style guidelines
- [ ] Commit messages follow Conventional Commits
- [ ] Changes are limited to one logical purpose
- [ ] Tests added/updated for new functionality
- [ ] Documentation updated if needed
- [ ] PR description clearly describes the *why*, not just the *what*

### PR Description Template

When opening a PR, use this structure:

```markdown
## Summary
<!-- What does this PR do? Why is it needed? -->

## Changes
<!-- Bullet list of specific changes -->

## Testing
<!-- How was this tested? What edge cases were covered? -->

## Screenshots (if UI changes)
<!-- Add screenshots for any visual changes -->
```

### Review Process

1. CI must pass (build, test, lint, security scan)
2. At least one maintainer review is required
3. Address review comments and re-request review
4. Squash and merge once approved

---

## Code Style

### Java (Backend)

- **No Lombok** — Write explicit getters/setters/constructors
- **4-space indentation** (no tabs)
- **Max line length** — 120 characters
- **Spring Boot conventions** — constructor injection preferred over field injection
- **MyBatis** — Use XML mappers for complex queries, annotations for simple CRUD
- **Naming**:
  - Classes: `PascalCase` (e.g., `ProjectService`)
  - Methods: `camelCase` (e.g., `selectById`)
  - Constants: `UPPER_SNAKE_CASE` (e.g., `DEFAULT_PAGE_SIZE`)
  - Database columns: `snake_case` (e.g., `create_time`)

### TypeScript / Vue (Frontend)

- Follow the project's ESLint and Prettier configuration
- Use **Composition API** (`<script setup>`) for new components
- Prefer `defineProps` / `defineEmits` with TypeScript types
- Store state in Pinia; avoid Vuex
- Naming:
  - Components: `PascalCase` (e.g., `ProjectList.vue`)
  - Composables: `use` prefix (e.g., `useProject.ts`)
  - Store: `use` prefix (e.g., `useProjectStore.ts`)

---

## Testing

### Backend

Run unit tests:

```bash
cd backend
mvn test
```

### Frontend

Run lint and type check:

```bash
cd frontend-pc
npm run lint:eslint
npx vue-tsc --noEmit
```

---

## Project Structure

```
lest-platform/
├── backend/                    # Spring Boot microservices
│   ├── lest-common/            # Shared libraries
│   ├── lest-auth/             # Authentication [8096]
│   ├── lest-gateway/          # API Gateway [8080]
│   ├── lest-modules/          # Business modules
│   └── lest-visual/           # Monitoring
├── frontend-pc/               # Vue 3 admin web app
├── frontend-h5/               # Mobile H5 app
├── frontend-app/              # React Native mobile app
└── docs/                      # Architecture, PRD, tasks
```

---

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

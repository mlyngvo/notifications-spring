# Development Guidelines & Engineering Conventions

Welcome to the `notifications-spring` engineering guidelines. This directory documents our development standards, project workflows, naming conventions, and quality gates to maintain consistency across contributors and autonomous agents.

---

## Index of Guidelines

1. **[Ticket & Issue Tracking Conventions](ticket-conventions.md)**
   * Code formatting for Epics (`EP-*`), Standalone Tickets (`T-*`), and Sub-Tickets (`ST-*-*`).
   * Title formatting rules (no square brackets, strict single colon).
   * Label taxonomy and the Phase Label Deletion Lifecycle.

2. **[Git & Conventional Commits](git-commit-conventions.md)**
   * **Strictly forbidden to work live on `main`:** Every change must use a branch formatted strictly as `<type>/<ticketCode (if exist)>-<description>` and merge via Pull Request.
   * Strict adherence to [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) with strictly no scopes.
   * Include ticket code if the change relates to a ticket: `<type>: <ticketCode> - <description>`.
   * Standard format for non-ticket changes: `<type>: <description>`.
   * **Strictly forbidden footers:** Zero `Co-authored-by:`, `Generated-by:`, or AI model attributions. Only ticket links (`Resolves: ST-1-X`) and `BREAKING CHANGE:` are permitted.

3. **[Code Quality, Conventions & Linting](code-quality-and-conventions.md)**
   * Kotlin toolchain: `ktlint` + `detekt` with `.editorconfig` and `detekt.yml`.
   * Automated Git hooks and CLI verification commands.
   * Architectural rules: Spring Boot auto-configuration boundaries, email/notification delivery hygiene, typed error modeling, and immutability by default.

4. **[Automated Testing Strategy & Quality Gates](automated-testing-strategy.md)**
   * Testing pyramid: hermetic unit tests and Spring Boot / SMTP integration tests.
   * Toolchain: JUnit 5, Spring Boot Test (`ApplicationContextRunner`, `@SpringBootTest`), GreenMail SMTP, and Awaitility.
   * CI quality gates and local execution commands (`./gradlew check`).

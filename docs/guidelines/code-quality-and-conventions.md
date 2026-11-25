# Code Quality, Conventions & Linting Standards

This document establishes the code quality, static analysis toolchain, Git hook automation, and architectural coding standards for the `mlyngvo/notifications-spring` repository.

---

## 1. Toolchain & Static Analysis Matrix

Our repository enforces formatting consistency, clean Kotlin idioms, and static analysis checks:

| Language | Primary Formatter | Static Analysis / Linter | Configuration File | Key Enforcements |
| :--- | :--- | :--- | :--- | :--- |
| **Kotlin (`src/main`, `src/test`, `*.kts`)** | **`ktlint`** | **`detekt`** | `.editorconfig`<br>`detekt.yml` | 120-char line limit, wildcards forbidden in imports, cyclomatic complexity limits, no empty catch blocks, explicit API clarity. |

---

## 2. Local Git Hooks & Automation Workflow

Code quality is enforced locally prior to committing, preventing broken or unformatted code from reaching the remote repository.

### Pre-Commit Hook Setup
When Git hooks are configured in the repository (`./gradlew installGitHooks`), commits should automatically verify:
1. **Staged Kotlin Files:** Runs `ktlintFormat` / `ktlintCheck` on staged `.kt` and `.kts` files.
2. **Commit Message Rules:** Validates that commit headers follow Conventional Commits without scopes and without forbidden trailers (e.g., no `Co-authored-by:` or AI signatures).

### CLI Convenience Commands
```bash
./gradlew ktlintCheck       # Verify Kotlin code formatting
./gradlew ktlintFormat      # Auto-format Kotlin source files
./gradlew detekt            # Run full Detekt static analysis
./gradlew check             # Run all linters and tests
```

### CI Pull Request Gates
Every pull request triggers a GitHub Actions workflow that executes:
* `./gradlew build` (and `./gradlew ktlintCheck detekt test` once linters are enabled)

Pull requests with formatting or static analysis failures cannot be merged.

---

## 3. Core Architectural Conventions

### 3.1 Spring Boot Library & Auto-Configuration Boundaries
Because `notifications-spring` is a reusable Spring Boot starter/library consumed by host applications:
* **Override-Friendly Beans:** Auto-configured beans should use appropriate `@ConditionalOnMissingBean` / `@ConditionalOnProperty` annotations so host applications can replace or customize components without fighting the starter.
* **Minimal Public Surface:** Keep internal helpers `internal` or `private`. Only expose intentional, well-documented public contracts (properties, service interfaces, attachment DTOs).
* **No Hidden Global State:** Avoid static mutable singletons; inject dependencies via constructor injection.
* **Clean Dependency Hygiene:** Only declare dependencies as `api` when their types appear in the library's public binary API; keep internal dependencies `implementation`.

### 3.2 Notification & SMTP Delivery Hygiene
* **No Secret Leakage:** Never log SMTP passwords, API tokens, or sensitive message payloads at any log level.
* **Safe Recipient Guardrails:** Always respect `notifications.email.fixed-recipients` when configured so non-production environments cannot accidentally email real end users.
* **Proper Encoding & Resource Handling:** Always encode MIME headers and attachments in UTF-8 and close `InputStream` / external connections cleanly after use.

### 3.3 Typed Error Modeling & Exception Handling
* **Explicit Failure Modes:** Model expected validation, template rendering, or delivery failures clearly rather than throwing generic `RuntimeException`.
* **Zero Silent Catch:** Catching generic `Throwable` or `Exception` and swallowing it without logging or mapping to an explicit failure is strictly forbidden.

### 3.4 Immutability & Data Modeling
* **Read-Only Defaults:** Default to `val`. Use `var` only when local mutation is strictly necessary and contained within a small function scope.
* **Immutable Collections:** Public interfaces must expose read-only collections (`List<T>`, `Set<T>`, `Map<K, V>`). Never expose `MutableList` or `ArrayList` across public boundaries.
* **Immutable Configuration & DTOs:** Prefer immutable data classes and constructor-bound `@ConfigurationProperties`.

---

## 4. Code Formatting Quick Reference

### EditorConfig (`.editorconfig`)
The repository uses an `.editorconfig` file enforcing:
* Charset: `utf-8`
* End of line: `lf`
* Indent style: `space`
* Indent size: `4` (Kotlin, Java), `2` (YAML, JSON, Markdown)
* Trim trailing whitespace: `true`
* Insert final newline: `true`
* Max line length: `120`

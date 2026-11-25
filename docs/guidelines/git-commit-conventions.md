# Conventional Commits & Git Standards

This document establishes the Git commit standards for the `mlyngvo/notifications-spring` repository, strictly adhering to the [Conventional Commits v1.0.0](https://www.conventionalcommits.org/en/v1.0.0/) specification.

---

## 1. Commit Message Structure

Commit messages follow standard Conventional Commits with strictly no scopes. If a change directly relates to an issue or ticket (such as an epic sub-ticket `ST-*-*` or standalone ticket `T-*`), include the ticket code in the header. If the change is not tied to a ticket (e.g. general housekeeping, minor documentation fix, dependency update), omit the ticket code.

### Ticket-Related Commits
```
<type>: <ticketCode> - <description>

[optional body]

[optional footer(s)]
```

### General / Non-Ticket Commits
```
<type>: <description>

[optional body]

[optional footer(s)]
```

### Examples

**Ticket-Related:**
```
feat: ST-1-2 - support custom reply-to headers in EmailService

Allow callers to pass an optional reply-to address that defaults to the
configured default-from property when omitted.

Resolves: ST-1-2
```

**Non-Ticket:**
```
chore: update gradle dependencies and plugin versions
```

---

## 2. Commit Types

| Type | SemVer Correlation | Description | Example |
| :--- | :---: | :--- | :--- |
| `feat` | **MINOR** | A new feature or capability | `feat: ST-1-3 - add multi-brand template resolution` |
| `fix` | **PATCH** | A bug fix or defect resolution | `fix: ST-1-4 - close attachment input streams after send` |
| `docs` | None | Documentation only changes | `docs: clarify smtp configuration properties in readme` |
| `style` | None | Code style, formatting, whitespace (no functional change) | `style: apply ktlint formatting across main sources` |
| `refactor` | None | Code change that neither fixes a bug nor adds a feature | `refactor: ST-1-5 - extract mime message builder utility` |
| `perf` | **PATCH** | Code change that improves execution performance | `perf: ST-1-6 - cache merged branding properties` |
| `test` | None | Adding missing tests or correcting existing tests | `test: ST-1-2 - add fixed-recipients override test` |
| `build` | None | Changes to build configuration, dependencies, or Gradle | `build: ST-1-1 - configure ktlint and detekt plugins` |
| `ci` | None | Changes to CI configuration files and workflows | `ci: enforce ktlintCheck and detekt in PR workflow` |
| `chore` | None | Maintenance tasks, repository setup, or housekeeping | `chore: update gitignore rules` |

---

## 3. No Scopes Policy

Scopes are **strictly omitted** in this repository.
- Use `feat:` instead of `feat(email):`
- Use `build:` instead of `build(gradle):`
- Use `fix:` instead of `fix(template):`

Domain classification is handled via GitHub labels (`domain:*`, `phase:*`, `type:*`) and the ticket description itself, keeping git commit subjects concise, clean, and consistent.

---

## 4. Subject Line Rules

1. **Ticket Code (When Applicable):** Include the ticket code followed by ` - ` if the commit relates to a ticket:
   `<type>: <ticketCode> - <description>`
   If the commit does not relate to a specific ticket, omit the ticket code:
   `<type>: <description>`
2. **No Scopes:** Never wrap components in parentheses after the type (`feat(scope):` is strictly prohibited).
3. **Imperative Mood:** Use the imperative, present tense ("add" not "added", "fix" not "fixes").
4. **Lowercase:** Start the description with a lowercase letter (after `<ticketCode> - ` if present, or immediately after `<type>: `).
5. **No Trailing Period:** Do not end the subject line with a period (`.`).
6. **Length:** Keep the subject line under 72 characters when possible.

❌ *Incorrect:* `feat(email): ST-1-2 - add reply-to support` (no scope allowed)
❌ *Incorrect:* `feat: Added reply-to support.` (missing ticket code when ticket exists, capitalized past tense, trailing period)
❌ *Incorrect:* `feat: [ST-1-2] add reply-to support` (no square brackets allowed)
✅ *Correct (Ticket-related):* `feat: ST-1-2 - support custom reply-to headers in EmailService`
✅ *Correct (Ticket-related):* `build: ST-1-1 - configure ktlint and detekt plugins`
✅ *Correct (Non-ticket):* `chore: update dependencies and gradle plugins`
✅ *Correct (Non-ticket):* `docs: clarify configuration properties in readme`

---

## 5. Linking Tickets in Footers

Whenever a commit addresses, advances, or completes a sub-ticket or standalone ticket, reference the ticket ID in the footer:

* **In-progress work:** `Refs: ST-1-1`
* **Closing a ticket:** `Resolves: ST-1-1` or `Closes: ST-1-1`

```
build: ST-1-1 - configure ktlint and detekt plugins

Add ktlint and detekt to the Gradle build and wire both into the check task.

Resolves: ST-1-1
```

---

## 6. Breaking Changes

Breaking changes must be indicated by:
1. An exclamation mark (`!`) before the colon in the header:
   ```
   feat!: T-1 - upgrade to spring boot 4.0
   ```
2. A `BREAKING CHANGE:` footer explaining what changed and the migration path:
   ```
   BREAKING CHANGE: requires Java 21+ and Spring Boot 4.x.
   ```

---

## 7. Strictly Forbidden Footers (Zero AI / Bot Attributions)

To maintain a clean, professional, and audit-ready repository history, the following footer trailers are **strictly forbidden**:

* ❌ **NO Co-Author Trailers:** Never include `Co-authored-by:` lines.
* ❌ **NO Generator Trailers:** Never include `Generated-by:`, `Created-by:`, or tool signature footers.
* ❌ **NO AI / Model Attributions:** Never include any AI model references (e.g., `Generated with Claude`, `Generated with Gemini`, `AI pair programmer`).

### Permitted Footers Only:
The **only** permitted footers are:
1. Issue tracking references (`Resolves: <ticketCode>`, `Closes: <ticketCode>`, `Refs: <ticketCode>`)
2. `BREAKING CHANGE: <description>`

Any commit containing forbidden trailers will be rejected or rebased.

---

## 8. Branching & Pull Request Workflow

### Strictly No Direct Commits on `main`
Direct development or committing directly to `main` is **strictly forbidden**. The `main` branch represents tested, verified, and shippable code.

### Standard Workflow for All Changes:
1. **Branch Off `main`:**
   - Every change, whether feature, bugfix, refactor, or documentation, must be developed on a dedicated branch branched from the latest `main`.
   - **Branch Naming Convention:**
     Branches must **strictly** follow the pattern:
     ```
     <type>/<ticketCode (if exist)>-<description>
     ```
     - **When a ticket exists:** `<type>/<ticketCode>-<description>`
       *(e.g., `feat/T-1-upgrade-spring-boot-4`, `fix/ST-1-4-close-attachment-streams`, `refactor/ST-1-5-mime-builder`)*
     - **When no ticket exists:** `<type>/<description>`
       *(e.g., `chore/bump-dependencies`, `docs/add-engineering-guidelines`)*
     - Only valid commit `<type>` values are allowed (`feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`).

   ```bash
   git checkout main
   git pull origin main
   git checkout -b feat/T-1-upgrade-spring-boot-4
   ```

2. **Commit Changes on Branch:**
   - Make atomic, tested commits adhering to Conventional Commits and formatting rules.

3. **Push Branch & Open Pull Request:**
   - Push your branch to GitHub:
     ```bash
     git push -u origin feat/T-1-upgrade-spring-boot-4
     ```
   - Open a PR targeting `main`:
     ```bash
     gh pr create --title "feat!: T-1 - upgrade to spring boot 4.0" \
                  --body "Resolves: T-1"
     ```

4. **CI Verification & Merge:**
   - CI quality gates (`ktlintCheck`, `detekt`, `test` when configured) must pass 100%.
   - Code review approval required.
   - Merged into `main` via PR.

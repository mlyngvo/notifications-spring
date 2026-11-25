# Ticket & Issue Tracking Conventions

This guide establishes the mandatory naming, hierarchy, and labeling standards for GitHub Issues in the `mlyngvo/notifications-spring` repository.

---

## 1. Ticket Code Hierarchy & Prefix Rules

All issues created in the repository must use one of three standardized prefixes:

### A. Epics (`EP-<number>`)
Used for master tracking initiatives and major multi-step milestones that encompass multiple sub-tickets.
* **Format:** `EP-<number>: <Descriptive Title>`
* **Example:** `EP-1: notifications-spring 2.0 Rework and Spring Boot Upgrade`

### B. Sub-Tickets (`ST-<epicNumber>-<number>`)
Used for specific, actionable implementation tasks that belong to an existing Epic.
* **Format:** `ST-<epicNumber>-<subTicketNumber>: <Descriptive Task Title>`
* **Example:** `ST-1-1: Configure ktlint, detekt, and CI quality gates`
* **Example:** `ST-1-2: Add ApplicationContextRunner auto-configuration tests`

### C. Standalone Tickets (`T-<number>`)
Used for ad-hoc, isolated tasks, bug fixes, or minor maintenance not tied to an active Epic.
* **Format:** `T-<number>: <Descriptive Title>`
* **Example:** `T-1: Upgrade notifications-spring to Spring Boot 4.0`

---

## 2. Title Formatting Rules

To keep notifications, CLI outputs, and GitHub lists readable and clean:

1. **No Square Brackets:**
   Never use `[` or `]` anywhere in the issue title.
   ❌ *Incorrect:* `[Phase 0] ST-1-1: [build] Configure ktlint`
   ✅ *Correct:* `ST-1-1: Configure ktlint, detekt, and CI quality gates`

2. **Single Colon Rule:**
   The colon (`:`) must be used **strictly once** in the entire title, placed immediately after the ticket code.
   ❌ *Incorrect:* `ST-1-1: Phase 0: Configure ktlint`
   ✅ *Correct:* `ST-1-1: Configure ktlint, detekt, and CI quality gates`

---

## 3. Label Taxonomy

Labels are categorized using the `<category>: <value>` format:

### A. Phase Labels (`phase:*`)
*Used to track active and upcoming milestone progression (created per active epic/roadmap).*
* Format: `phase: <number>-<name>` (e.g., `phase: 0-foundation`, `phase: 1-core-api`, `phase: 2-validation`)

### B. Domain Labels (`domain:*`)
* `domain: email` — SMTP delivery, MIME composition, attachments, and recipient handling.
* `domain: templating` — Thymeleaf template rendering, i18n message sources, and branding styles/assets.
* `domain: auto-configuration` — Spring Boot auto-configuration, properties binding, and bean overrides.
* `domain: build-ci` — Gradle build configuration, publishing, linting, and GitHub Actions workflows.

### C. Work Type Labels (`type:*`)
* `type: epic` — High-level milestone tracking issues.
* `type: feature` — New user-facing or library capabilities.
* `type: foundation` — Architecture, build configuration, and infrastructure.
* `type: test` — Automated test suites and test utilities.
* `type: bug` — Defects, regressions, or unexpected runtime behaviors.

---

## 4. Phase Label Deletion Lifecycle

Phase labels are designed to track **active and upcoming** milestone work only.

> **Mandatory Rule:**
> When a phase is 100% completed, signed off, and verified, the corresponding `phase: <name>` label must be **deleted entirely from the GitHub repository** via:
> ```bash
> gh label delete "phase: <name>" --yes
> ```

This prevents label clutter and ensures the active label list on GitHub only displays milestones currently in flight.

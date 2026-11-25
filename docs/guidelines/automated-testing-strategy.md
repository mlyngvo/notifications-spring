# Automated Testing Strategy & Quality Gates

This document establishes the testing architecture, framework toolchain, and CI quality gates for the `mlyngvo/notifications-spring` repository.

---

## 1. Testing Philosophy & Pyramid

Our testing strategy follows a pragmatic pyramid tailored to a Spring Boot notification library: **fast, hermetic unit tests** for template rendering, branding properties, attachment construction, and subject/recipient parsing, paired with **focused Spring Boot integration tests** for auto-configuration and end-to-end SMTP delivery against an embedded mock SMTP server.

```
                  ┌──────────────────────────────────────────┐
                  │       Spring Boot Integration Tests      │   (ApplicationContextRunner,
                  │     (Auto-Config, GreenMail SMTP)        │    @SpringBootTest + Awaitility)
                  ├──────────────────────────────────────────┴──────────────────┐
                  │                        Unit Tests                           │   (EmailService,
                  │            (Fast, Deterministic, 100% Hermetic)             │    Template & Attachment Services)
                  └─────────────────────────────────────────────────────────────┘
```

### Coverage & Scope Matrix

| Layer | Unit Tests | Integration Tests | Test Frameworks |
| :--- | :--- | :--- | :--- |
| **Email Delivery (`EmailService`)** | **High (>90%)**: Subject prefix/postfix & truncation, empty recipients, `fixed-recipients` override, CC/BCC, inline vs file attachments. | End-to-end MIME delivery verified against an embedded GreenMail SMTP server. | JUnit 5<br>GreenMail<br>Awaitility |
| **Templating & Branding (`EmailTemplateService`)** | Template rendering with locales & variables, default + brand style/image property merging, missing resource fallback. | Thymeleaf `SpringTemplateEngine` + Spring `MessageSource` i18n resolution. | JUnit 5<br>Spring Boot Test |
| **Attachments (`EmailAttachmentService`)** | Creating file and inline (`cid:`) attachments from `ByteArray`, `InputStream`, and `URL`. | Multipart `related`/`alternative` MIME structure verification. | JUnit 5 |
| **Auto-Configuration (`NotificationsAutoConfiguration`, `EmailAutoConfiguration`, `EmailProperties`)** | Property defaults and binding validation. | Conditional bean registration (`notifications.email.enabled`), SMTP property wiring, user bean overrides. | `ApplicationContextRunner`<br>`@SpringBootTest` |

---

## 2. Test Toolchain

* **JUnit 5 (`useJUnitPlatform()`):** Standard test runner for unit and integration suites.
* **Spring Boot `ApplicationContextRunner`:** Fast, isolated verification of auto-configuration conditions, property binding, and user-provided bean overrides without booting a full application.
* **GreenMail (`greenmail-spring`):** Embedded mock SMTP server for verifying actual MIME messages, headers, recipients, and attachments without external network dependencies.
* **Awaitility:** Deterministic polling assertions for asynchronous delivery verification — never use raw `Thread.sleep`.

---

## 3. Test Execution Commands

### Local CLI Execution

```bash
# Run all unit and integration tests
./gradlew test

# Run full verification suite (linters + static analysis + tests)
./gradlew check
```

---

## 4. CI Pull Request Gates

All GitHub Actions workflows enforce the following mandatory quality gates before any PR can merge:

1. **100% Test Pass Rate:** Every unit and integration test in `./gradlew test` must pass.
2. **Zero Delivery Regressions:** Template rendering, MIME composition, recipient routing, and SMTP delivery tests must maintain 100% pass rate.
3. **Zero Linting / Static Analysis Violations:** `ktlintCheck` and `detekt` (once configured) must pass cleanly.
4. **No Flaky Tests:** Any test exhibiting non-deterministic failure in CI (e.g., port collisions or timing races) must be diagnosed and resolved immediately.

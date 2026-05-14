# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Context

**toplms** is a Learning Management System (LMS) being built as a **multi-tenant, multi-database SaaS** product. The owner is new to Spring and is using this project as a learning vehicle, so prefer explanations alongside changes — surface the *why* (Spring lifecycle, JPA semantics, transactional boundaries, tenant isolation trade-offs) rather than just producing code. When introducing a new Spring concept (e.g. `@Transactional`, `AbstractRoutingDataSource`, `@ConfigurationProperties`), briefly explain it the first time it appears.

## Tech Stack

- **Java 17** (toolchain pinned in `build.gradle`)
- **Spring Boot 4.0.6** with Spring Web MVC and Spring Data JPA
- **Spring AI 2.0.0-M5** with the Anthropic Claude starter (`spring-ai-starter-model-anthropic`)
- **PostgreSQL** as the runtime database driver
- **Gradle** (Groovy DSL) — wrapper checked in
- **JUnit 5** via `useJUnitPlatform()`

## Common commands

Use the Gradle wrapper (`./gradlew`) — never a system-installed Gradle.

| Task | Command |
| --- | --- |
| Run the app | `./gradlew bootRun` |
| Build (compile + test + jar) | `./gradlew build` |
| Compile only | `./gradlew classes` |
| Run all tests | `./gradlew test` |
| Run a single test class | `./gradlew test --tests com.toplms.ToplmsApplicationTests` |
| Run a single test method | `./gradlew test --tests 'com.toplms.ToplmsApplicationTests.contextLoads'` |
| Continuous test re-run | `./gradlew test --continuous` |
| Clean build artifacts | `./gradlew clean` |
| Build OCI image | `./gradlew bootBuildImage` |
| Show dependency tree | `./gradlew dependencies` |

Test reports land in `build/reports/tests/test/index.html`.

## Architecture notes

The codebase is currently a Spring Boot skeleton (`ToplmsApplication` + `application.yaml` + a context-loads smoke test). The architectural decisions below are *target state* for the SaaS LMS — code added to this repo should move toward them, not away.

### Multi-tenancy model

Two dimensions to keep distinct, because Spring/Hibernate handle them differently:

1. **Tenant resolution** — how an inbound HTTP request is mapped to a tenant. Typical sources: subdomain, JWT claim, `X-Tenant-ID` header. Implement once as a `CurrentTenantIdentifierResolver` (Hibernate) plus a servlet `Filter` / `HandlerInterceptor` that stashes the tenant in a `ThreadLocal` (or `RequestContextHolder`) for the duration of the request. Clear the `ThreadLocal` in a `finally` block — leaks across thread-pool reuse are the classic multi-tenant bug.
2. **Tenant isolation strategy** — the user has stated **multi-database** (database-per-tenant). In Hibernate terms this is `MultiTenancyStrategy.DATABASE`, implemented via a `MultiTenantConnectionProvider` that returns a `Connection` from the right tenant `DataSource`. Spring's `AbstractRoutingDataSource` is a simpler alternative when you control routing yourself, but it does not coordinate with Hibernate's L2 cache or schema-per-tenant migrations.

A `tenants` registry (often a small "master" / control-plane database) stores per-tenant `DataSource` config (JDBC URL, credentials, plan, status). The connection provider reads from this registry, lazily building and caching `HikariDataSource` instances per tenant.

Per-tenant **Flyway/Liquibase** migrations should run on tenant provisioning and on app startup for every active tenant — *not* via Spring Boot's auto-migration, which only sees the primary `DataSource`.

### Layering convention to adopt

As features land, keep the standard Spring layering so the tenant context flows cleanly:

```
controller (web)  →  service (@Transactional)  →  repository (Spring Data JPA)  →  entity
```

`@Transactional` boundaries belong on the service layer. The tenant `ThreadLocal` must be set *before* the transaction begins, otherwise Hibernate opens a connection on the wrong (or no) `DataSource`.

### Spring AI

`spring-ai-starter-model-anthropic` is on the classpath, so AI features (e.g. tutoring assistant, question generation, content summarisation) are expected. Configure via `spring.ai.anthropic.api-key` — keep the key in env vars or a secrets manager, never `application.yaml`. AI calls should be tenant-scoped for billing/quota reasons; consider wrapping the `ChatClient` so every call records `tenantId`.

## Configuration

`src/main/resources/application.yaml` only sets the application name today. As config grows, prefer:

- `application.yaml` for shared defaults
- `application-{profile}.yaml` (e.g. `application-dev.yaml`, `application-prod.yaml`) for env-specific overrides — activate with `SPRING_PROFILES_ACTIVE=dev`
- `@ConfigurationProperties` classes (with `@Validated`) over scattered `@Value` injections

The PostgreSQL driver is `runtimeOnly`, so the app will fail to start without `spring.datasource.*` (or per-tenant equivalents) configured.

## Build artifacts

Do not commit `build/` or `.gradle/`. The `.gitignore` already covers them.

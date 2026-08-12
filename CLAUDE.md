# CLAUDE.md — CeyGreen Project Instructions (Student 5 / Forum Service)

This file is read automatically by Claude Code CLI at the start of every session in this repo.
It defines how to work in this codebase — git workflow, safety rules, and build guidance for the
Community Forum microservice.

---

## 0. Scope of this file

These rules apply whenever Claude Code is working inside the `CeyGreen` repository, and especially
when working on `forum-service/` (Student 5's microservice). Full feature spec for the Forum service
is in `forum-service-api-plan.md` — read that before implementing anything in `forum-service/`.

---

## 1. Git Workflow Rules — READ CAREFULLY

### 1.1 Never push directly to `main`
`main` is protected. Every change, no matter how small, goes through a feature branch and a Pull
Request. Never run `git push origin main` or any command that pushes directly to `main`.

### 1.2 Always work on a feature branch
Before making any code change, confirm the current branch is not `main`. If it is, create a new
branch first:
```
git checkout main
git pull origin main
git checkout -b feature/forum-service
```
**Branch naming convention (confirmed from this repo's existing branches):** `feature/<kebab-case-description>`,
matching the pattern used by other services — e.g. `feature/iot-service`, `feature/treatment-service`,
`feature/user-diagnosis-service`, `feature/ecommerce-marketplace`. No student-number prefix is used.
Bug fixes use `fix/<kebab-case-description>` (e.g. `fix/kafka-listener-binding`).

For this service, use `feature/forum-service` as the working branch. If a task is large enough to
warrant splitting further, use a more specific name in the same style, e.g. `feature/forum-ai-fallback`.

### 1.3 Commit often, in small logical units
Do not batch a whole feature into one giant commit. Make a separate commit for each logical step —
e.g. one commit for the model/schema, one for an endpoint, one for security config, one for tests.
This produces a clean, reviewable history and is required for this project (frequent, meaningful
commits are expected, not optional).

Use Conventional Commits format, matching the existing repo pattern, e.g.:
```
feat(forum): add Post and Reply MongoDB models
feat(forum): implement GET /forum/posts with tag/crop filters
fix(forum): prevent duplicate upvotes on same reply
docs(forum): add README for forum-service
```

### 1.4 Commit and push autonomously — no need to stop for approval each time
The user does not have time to manually review and approve every single commit. You may commit and
push to the **feature branch** (never `main`) without pausing for confirmation, as long as:
- each commit is a small, logical unit (see 1.3) with a clear Conventional Commits message, and
- you never push to `main` directly (rule 1.1 still applies, no exceptions), and
- after finishing a chunk of work, you give a brief summary of what was committed/pushed so the
  user has a record, without requiring them to approve each individual commit.

The goal is a clean history of many small, well-labeled commits pushed to the feature branch as you
go — not one giant commit at the end, and not a bottleneck waiting on manual sign-off for each step.

### 1.5 Every branch ends in a Pull Request
Once the feature branch has enough committed/pushed work to be reviewable (or the feature is
complete):
1. Confirm the branch is pushed: `git push origin <branch-name>`
2. Open a PR against `main` (via `gh pr create` if the GitHub CLI is available, or state the compare
   URL for the user to open manually).
3. Write a clear PR description: what changed, why, and how it was tested (e.g. "tested standalone
   with `docker compose up forum-service mongodb kafka` — confirmed it runs with no dependency on
   other services").
4. Do not merge the PR. Merging happens only after human review is approved AND CI/CD checks pass.
   If CI/CD fails, fix the issue on the same branch and push again — do not merge around a failing
   pipeline.

---

## 2. Cost / Token Efficiency

- Prefer small, focused tasks over "build the whole service" requests. Work through
  `forum-service-api-plan.md`'s Build Order section one step at a time.
- Don't re-read large files (e.g. full `docker-compose.yml`, full README) more than necessary once
  their relevant contents are already known in-session.
- Model selection (e.g. switching to a cheaper model for simple/repetitive tasks) is controlled by
  the user via the `/model` command or `.claude/settings.json` — this file cannot set that
  automatically, so the user will manage it manually per task.

---

## 3. Service Independence Rule (applies to all backend work in this repo)

- No REST call from one internal CeyGreen microservice to another, ever.
- Identity (`authorId`, `authorName`, `role`, etc.) comes only from the OAuth 2.0 JWT claims
  forwarded by the API Gateway — never from calling User Management.
- The only external network call the Forum service makes is to the Google Gemini API (third-party
  LLM, not an internal microservice) for the AI fallback answer feature.
- Kafka: this service is a **producer only** on `forum-events` — it never consumes any topic.
- The service must run and be testable standalone:
  `docker compose up forum-service mongodb kafka` — with no other CeyGreen service present.

If any implementation approach would require calling another internal service, stop and flag it
instead of implementing it — it violates the project's core architecture rule.

---

## 4. Where to find the full spec

- `forum-service-api-plan.md` — complete data model, 5 REST endpoints, security rules, AI fallback
  design, Kafka event schema, project structure, and suggested build order.
- Follow that document's "Build Order" section sequentially unless told otherwise.

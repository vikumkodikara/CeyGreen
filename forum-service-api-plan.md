# CeyGreen — Community Forum Service (Student 5)
## API & Build Plan for Claude Code CLI

---

## 1. Overview

**Service name:** `forum-service`
**Port:** `8085`
**Database:** MongoDB 7.0
**Owner:** Student 5
**Role:** Farmer discussion posts and replies, with tags, upvotes, resolved-answer marking, and an AI-assisted fallback answer when a question goes unanswered.

**Hard constraint — Service Independence:**
This service must be fully self-contained.
- No REST call is ever made from this service to another internal CeyGreen microservice (IoT, Disease Detection, Treatment, E-Commerce, Sales Analytics/Notification, User Management).
- Author identity (`authorId`, `authorName`) comes only from the OAuth 2.0 JWT claims forwarded by the API Gateway on each request — never fetched via a call to User Management.
- The only external network call this service makes is to the Google Gemini API (a third-party LLM API, not an internal microservice), used solely for the AI-assisted fallback answer feature.
- The service must be independently buildable, runnable, and testable with `docker compose up forum-service mongodb kafka` — no other CeyGreen service needs to be running.

---

## 2. Tech Stack

- **Language/Framework:** Java 17+, Spring Boot 3.x
- **Database:** MongoDB 7.0 (Spring Data MongoDB)
- **Messaging:** Apache Kafka (KRaft mode) — producer only
- **Security:** Spring Security, JWT validation (RS256, same public key as Gateway), API Key check
- **External API:** Google Gemini API (`gemini-1.5-flash` or later) for AI fallback answers
- **Build tool:** Maven
- **Containerization:** Docker, added to root `docker-compose.yml`

---

## 3. Data Model (MongoDB)

### Collection: `posts`

```json
{
  "_id": "ObjectId",
  "authorId": "string",
  "authorName": "string",
  "title": "string",
  "body": "string",
  "tags": ["string"],
  "cropType": "string",
  "resolved": "boolean (default false)",
  "acceptedReplyId": "ObjectId | null",
  "flagged": "boolean (default false)",
  "flagCount": "number (default 0)",
  "aiAnswerAttempted": "boolean (default false)",
  "replies": [
    {
      "_id": "ObjectId",
      "authorId": "string",
      "authorName": "string",
      "body": "string",
      "createdAt": "ISODate",
      "isAiGenerated": "boolean (default false)",
      "upvotes": "number (default 0)",
      "upvotedBy": ["string (userId)"],
      "flagged": "boolean (default false)",
      "flagCount": "number (default 0)"
    }
  ],
  "createdAt": "ISODate",
  "updatedAt": "ISODate"
}
```

### Indexes

```javascript
db.posts.createIndex({ tags: 1 })
db.posts.createIndex({ cropType: 1 })
db.posts.createIndex({ authorId: 1 })
db.posts.createIndex({ createdAt: -1 })
db.posts.createIndex({ resolved: 1, createdAt: -1 })
```

### Design notes

- Replies are embedded in the post document (not a separate collection) so a full thread loads in a single read — no join needed.
- `authorName` is denormalized (copied from the JWT claim at write time) so this service never needs to call User Management to resolve a name. Trade-off: if a user later changes their display name, old posts keep the old name. This is accepted deliberately to preserve independence.
- `upvotedBy` prevents duplicate votes per reply per user.

---

## 4. REST Endpoints (5 total — stays within the project's 3–5 endpoint rule)

All endpoints are behind the API Gateway at `/api/forum/**`. All require a valid OAuth 2.0 bearer token (validated by Gateway, claims forwarded) plus a service-level API Key header.

### 4.1 `GET /forum/posts`
List posts, paginated, filterable.

**Query params:**
| Param | Type | Description |
|---|---|---|
| `page` | int | default 0 |
| `size` | int | default 20 |
| `tags` | string (comma-separated) | filter by tag, e.g. `tags=tomato,blight` |
| `cropType` | string | filter by crop |
| `resolved` | boolean | filter resolved/unresolved |
| `sort` | string | `newest` (default) or `mostUpvoted` |

**Response 200:** paginated list of posts (replies array can be omitted/truncated in the list view — return only `replyCount`, not full reply bodies, to keep payload small).

---

### 4.2 `POST /forum/posts`
Create a new discussion post.

**Auth:** any authenticated farmer/buyer.

**Request body:**
```json
{
  "title": "string (required)",
  "body": "string (required)",
  "tags": ["string"],
  "cropType": "string (optional)"
}
```

`authorId` and `authorName` are NOT taken from the request body — extract them from the validated JWT claims (`sub`/`farmerId`, `name`) forwarded by the Gateway.

**Response 201:** created post object.

**Side effect:** schedule an AI-fallback check (see section 6) for this post if it remains unanswered after the configured delay.

---

### 4.3 `GET /forum/posts/{id}`
Get a single post with its full reply thread.

**Response 200:** full post document including all replies.
**Response 404:** post not found.

---

### 4.4 `POST /forum/posts/{id}/replies`
Add a reply to an existing post. This single endpoint also handles reply-level actions via an `action` field, to avoid adding new endpoints:

**Request body (normal reply):**
```json
{
  "body": "string (required)"
}
```

**Request body (upvote action):**
```json
{
  "action": "upvote",
  "replyId": "string (required)"
}
```

**Request body (accept-answer action — original post author only):**
```json
{
  "action": "acceptAnswer",
  "replyId": "string (required)"
}
```

**Request body (flag action):**
```json
{
  "action": "flag",
  "replyId": "string (optional — omit to flag the post itself)"
}
```

**Behavior:**
- Default (no `action` field): create a new reply, `isAiGenerated: false`, `authorId`/`authorName` from JWT. Publish `forum-events` (type: `NEW_REPLY`) to Kafka.
- `upvote`: push `userId` into `upvotedBy` if not already present, increment `upvotes`. Idempotent — reject/no-op if already upvoted by this user.
- `acceptAnswer`: only allowed if the requester's JWT `authorId` matches the post's `authorId`. Sets `post.resolved = true`, `post.acceptedReplyId = replyId`.
- `flag`: increment `flagCount` on the reply or post; if `flagCount` crosses a threshold (e.g. 3), set `flagged = true` for moderation review.

**Response 200/201** depending on action; **403** if `acceptAnswer` attempted by non-author.

---

### 4.5 `DELETE /forum/posts/{id}`
Remove a post. Author or admin role (from JWT) only.

**Response 204:** deleted.
**Response 403:** not the author and not admin.
**Response 404:** not found.

---

## 5. Security

- **JWT validation:** verify RS256 signature using the Gateway's shared public key (or JWKS endpoint) — this service validates the token itself; it does not call another service to check it.
- **API Key:** required on every request via header, e.g. `X-API-KEY`, matching the pattern used by other CeyGreen services.
- **Authorization rules:**
  - Create post / reply: any authenticated user.
  - Delete post: post author OR `role: admin` claim.
  - Accept answer: post author only.
  - Flag: any authenticated user (rate-limit or dedupe by userId to prevent abuse — stretch goal).

---

## 6. AI-Assisted Fallback Answer

**Purpose:** if a question goes unanswered for a configurable period, generate a clearly-labeled AI answer so the thread isn't dead, rather than requiring a human to always respond first.

**Trigger:** a scheduled job (Spring `@Scheduled`, e.g. runs every 30 minutes) queries for posts where:
- `replies` is empty (or has zero non-AI replies)
- `aiAnswerAttempted == false`
- `createdAt` is older than a configured threshold (e.g. 24 hours)

**Step 1 — cheap check first (no LLM call):**
Before calling Gemini, search existing resolved posts for similarity (by shared `tags` + `cropType`, or basic text search on `title`/`body` using a MongoDB text index). If a strong match exists, post the AI reply as: *"This looks similar to an earlier resolved question — [link/reference to that post]."* No LLM cost incurred.

```javascript
db.posts.createIndex({ title: "text", body: "text" })
```

**Step 2 — LLM fallback (only if no similar resolved post found):**
Call Gemini API with the post's `title`, `body`, `cropType`, and `tags` as context. Prompt should instruct the model to give a short, practical, greenhouse-farming-specific answer and to be explicit about uncertainty.

Append the generated text as a new reply:
```json
{
  "authorId": "SYSTEM_AI",
  "authorName": "CeyGreen AI Assistant",
  "body": "<generated text>\n\n⚠️ This is an AI-generated answer and has not been verified by another farmer.",
  "isAiGenerated": true
}
```

Set `post.aiAnswerAttempted = true` regardless of outcome (so it's only attempted once automatically).

**Step 3:** publish the same `forum-events` (type: `NEW_REPLY`) Kafka event as a normal reply, so Student 6's notification service treats it identically — no special-casing needed downstream.

**Config (application.yml / env vars):**
```yaml
forum:
  ai-fallback:
    enabled: true
    unanswered-threshold-hours: 24
    check-interval-minutes: 30
gemini:
  api-key: ${GEMINI_API_KEY}
  model: gemini-1.5-flash
```

---

## 7. Kafka Integration

**Topic:** `forum-events`
**Role:** producer only (this service never consumes any topic).

**Event payload (published on every new reply, human or AI-generated):**
```json
{
  "eventType": "NEW_REPLY",
  "postId": "string",
  "postAuthorId": "string",
  "replyId": "string",
  "replyAuthorId": "string",
  "isAiGenerated": "boolean",
  "timestamp": "ISO8601"
}
```

Consumed by Student 6's Sales Analytics & Notification Service — this service does not need to know or care whether anything is listening (fire-and-forget, matches the project's Kafka independence rule).

---

## 8. Project Structure (suggested)

```
forum-service/
├── src/main/java/com/ceygreen/forum/
│   ├── ForumServiceApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java         # JWT + API key filters
│   │   ├── KafkaProducerConfig.java
│   │   └── SchedulingConfig.java       # enables @Scheduled
│   ├── controller/
│   │   └── ForumController.java        # the 5 endpoints
│   ├── service/
│   │   ├── ForumService.java           # core CRUD + action logic
│   │   ├── AiFallbackService.java       # scheduled job + Gemini call
│   │   └── SimilarityService.java       # text-search-based similarity check
│   ├── repository/
│   │   └── PostRepository.java         # Spring Data MongoDB
│   ├── model/
│   │   ├── Post.java
│   │   └── Reply.java
│   ├── dto/
│   │   ├── CreatePostRequest.java
│   │   ├── ReplyActionRequest.java
│   │   └── PostResponse.java
│   └── kafka/
│       └── ForumEventProducer.java
├── src/main/resources/
│   └── application.yml
├── Dockerfile
└── pom.xml
```

---

## 9. Environment Variables

| Variable | Purpose |
|---|---|
| `MONGO_URI` | MongoDB connection string |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker address |
| `JWT_PUBLIC_KEY` or `JWKS_URI` | for validating Gateway-issued tokens |
| `FORUM_API_KEY` | this service's API key secret |
| `GEMINI_API_KEY` | Google Gemini API key |
| `SERVER_PORT` | default 8085 |

---

## 10. Build Order (suggested, for Claude Code CLI session)

1. Scaffold Spring Boot project (Maven, dependencies: Web, Spring Data MongoDB, Spring Security, Spring Kafka, Validation).
2. Define `Post` and `Reply` models + `PostRepository`.
3. Implement JWT + API key security filters.
4. Implement `GET /forum/posts` (list + filters + pagination).
5. Implement `POST /forum/posts` (create).
6. Implement `GET /forum/posts/{id}`.
7. Implement `POST /forum/posts/{id}/replies` — start with plain reply creation, then add `upvote`, `acceptAnswer`, `flag` action branches.
8. Implement `DELETE /forum/posts/{id}`.
9. Add Kafka producer + publish on every reply.
10. Add MongoDB text index + `SimilarityService`.
11. Add `AiFallbackService` with Gemini API integration + `@Scheduled` job.
12. Write Dockerfile, add to root `docker-compose.yml`, wire Gateway route `/api/forum/**` → `forum-service:8085`.
13. Test standalone: `docker compose up forum-service mongodb kafka` only — confirm it runs without any other CeyGreen service present.

# Community Forum Service — Student 5

**Port:** `8085`  
**Database:** MongoDB  
**Owner:** Student 5

## Overview

Gives CeyGreen a social layer: farmers post updates, ask questions, and reply to each other's threads. This is the most naturally document-shaped data of the six services — a post with a variable number of nested replies fits a document database far more comfortably than a rigid relational join.

Like the E-Commerce service, it is a Kafka producer: every new reply publishes an event so the original poster can be notified.

## Endpoints

| Method | Path | CRUD | Description |
|---|---|---|---|
| GET | `/forum/posts` | Read | List posts, paginated and filterable by tag or crop |
| POST | `/forum/posts` | Create | Create a new discussion post |
| GET | `/forum/posts/{id}` | Read | Get a single post with its full reply thread |
| POST | `/forum/posts/{id}/replies` | Create | Add a reply; publishes a `forum-events` message |
| DELETE | `/forum/posts/{id}` | Delete | Remove a post (author or admin only) |

## Database Schema (MongoDB)

| Collection | Key Fields | Purpose |
|---|---|---|
| `posts` | `_id, authorId, title, body, tags[], createdAt, replies[]` | Each post embeds its replies, so a full thread loads in a single read |

## Kafka Integration

| Topic | Role | Published When |
|---|---|---|
| `forum-events` | **Producer** | A new reply is posted to a thread |

## Security

- API Key verification on all endpoints
- Delete/moderation restricted to the post's original author or an admin role

## Ideas Worth Adding

- Tag-based browsing (`#tomato`, `#pestcontrol`) so discussions are discoverable
- An upvote count on replies so the most useful answer rises to the top

## Getting Started

```bash
# Place your Spring Boot project here
# Port: 8085
# Tech: Spring Boot 3.5.x, Java 17, MongoDB
```

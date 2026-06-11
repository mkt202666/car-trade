---
name: gap-analysis
description: "Compare functional requirements (docs) against DB schema and entity classes to find missing fields, tables, or features."
---

# Functional Gap Analysis (功能差距分析)

Compare the design docs against the actual codebase to find what's missing.

## Procedure

### 1. Extract requirements from docs

Read `docs/01-功能模块.md` to extract:
- Each functional module (F-codes) and its described features
- Fields implied by each feature (e.g., "图片/视频上传" implies `video_url`)

### 2. Inventory DB schema

Read `car-trade-backend/src/main/resources/init.sql`:
- List all tables and their columns
- Note: init.sql is the source of truth (32 tables), NOT `docs/04-数据库设计.md` (stale, 28 tables)

### 3. Inventory entities

List all Java entity classes:
```bash
find car-trade-backend/src -name "*Entity.java" -o -name "*.java" | xargs grep "@TableName"
```
Compare entity fields against DB columns.

### 4. Compare and list gaps

For each functional requirement:
- Does a DB table exist? If no → is it a UI/config feature (no DB needed)?
- Does the table have the required columns?
- Does the entity class have matching fields?
- Do VOs/DTOs expose the fields?

### 5. Output format

Present gaps as a table:

| # | Table | Missing Field | Related Feature | Action |
|---|-------|--------------|-----------------|--------|

Then proceed with field补全 (see `field-schema-sync` skill) for each gap.

## Key files

| File | Purpose |
|------|---------|
| `docs/01-功能模块.md` | Functional requirements (authoritative) |
| `docs/04-数据库设计.md` | **STALE** — do not trust, use init.sql instead |
| `car-trade-backend/src/main/resources/init.sql` | Actual DB schema (source of truth) |
| `car-trade-backend/src/main/java/.../entity/*.java` | Entity field inventory |

## Known stale areas

- `docs/04-数据库设计.md` defines 28 tables; init.sql has 32. Many fields added via ALTER TABLE are missing from the doc.
- Design doc should NOT be used as a reference for current schema — always check init.sql directly.

---
name: field-schema-sync
description: "Add a new DB column across all layers: init.sql → schema.sql → Entity → VOs → DTOs → Converters, then verify with mvn compile."
---

# Field Schema Sync (字段补全)

When adding a new database column, update all 6 layers in order. Missing any layer causes runtime failures.

## Procedure

### 1. DDL — `car-trade-backend/src/main/resources/init.sql`

Add the column to the CREATE TABLE block with `IF NOT EXISTS` semantics:
```sql
column_name  TYPE  [constraints],  -- between logical neighbors
```
Add a COMMENT:
```sql
COMMENT ON COLUMN table_name.column_name IS '描述';
```

### 2. Migration — `car-trade-backend/src/main/resources/schema.sql`

Add an idempotent ALTER for existing databases:
```sql
ALTER TABLE table_name ADD COLUMN IF NOT EXISTS column_name TYPE;
```

### 3. Entity — `car-trade-backend/src/main/java/.../entity/*.java`

Add `private Type fieldName;` with Javadoc. Do NOT add `@TableField(exist = false)` — the field must persist.

### 4. VOs — `car-trade-backend/src/main/java/.../vo/*.java`

Add the field to every VO that returns this entity's data. Common VO files per entity:
- `*VO.java` — general response
- `*DetailVO.java` — detail page response
- `UserPublicVO.java` — public/seller-facing response (if User entity)

### 5. DTOs — `car-trade-backend/src/main/java/.../dto/*.java`

Add the field to `*CreateDTO.java` and `*UpdateDTO.java` (if applicable). Skip if field is computed, not user-input.

### 6. Converters — `car-trade-backend/src/main/java/.../converter/*.java`

Add mapping in every `toVO()` / `toDetailVO()` / `toPublicVO()` method:
```java
vo.setFieldName(source.getFieldName());
```

### 7. Verify

```bash
cd car-trade-backend && mvn compile -q 2>&1 | tail -5
```

If compile fails, the error message usually names the missing setter/getter — fix and re-verify.

## Stopping condition

`mvn compile` succeeds with no errors.

## Key files reference

| Layer | Path pattern |
|-------|-------------|
| DDL | `car-trade-backend/src/main/resources/init.sql` |
| Migration | `car-trade-backend/src/main/resources/schema.sql` |
| Entity | `car-trade-backend/src/main/java/.../entity/*.java` |
| VO | `car-trade-backend/src/main/java/.../vo/*.java` |
| DTO | `car-trade-backend/src/main/java/.../dto/*.java` |
| Converter | `car-trade-backend/src/main/java/.../converter/*.java` |

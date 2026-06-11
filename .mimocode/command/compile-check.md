---
description: "Run mvn compile and show the last N lines of output to verify backend builds cleanly."
---

# Compile Check

Run Maven compile on the backend and display results:

```bash
cd /d/ai_project/new-car-trade/car-trade-backend && mvn compile -q 2>&1 | tail -$1
```

Default: `5` lines of output. Pass a larger number (e.g., `20` or `30`) for more context when debugging failures.

## Usage

- After editing Java files: `compile-check` or `compile-check 5`
- After debugging compile errors: `compile-check 30`

# Comprehensive Git Branching Strategies & Conflict Resolution Guide

## 1. Industry Branching Models

### A. GitFlow
- **Structure**:
  - `main` / `master`: Production-ready code only. Tagged with semantic releases (`v1.0.0`).
  - `develop`: Integration branch where nightly builds occur.
  - `feature/*`: Branched off `develop`, merged back to `develop` via pull requests.
  - `release/*`: Branched off `develop` for final staging and QA; merged into both `main` and `develop`.
  - `hotfix/*`: Branched directly off `main` to address critical production incidents.
- **Best For**: Scheduled software releases, enterprise monoliths, and regulatory sign-offs.

### B. Trunk-Based Development
- **Structure**: All engineers commit to a single shared branch (`main` or "trunk") frequently (multiple times a day).
- **Techniques**:
  - Very short-lived branches (< 24 hours).
  - Feature Flags (Toggles) hide incomplete functionality in production.
  - Pair programming and comprehensive CI pipelines.
- **Best For**: High-velocity DevOps teams, microservices, and continuous deployment environments.

### C. GitHub Flow
- **Structure**:
  - Anything in `main` is deployable.
  - Create descriptive branches off `main` (`feature/order-summary`).
  - Open a Pull Request for discussion and review.
  - Merge to `main` upon approval and deploy immediately.
- **Best For**: Web applications and continuous integration workflows.

---

## 2. Merge vs. Rebase: Deep-Dive Analysis

```
Feature Branching Topology:

      C --- D (feature)
     /
A --- B --- E (main)

1. git merge feature (Creates Merge Commit M preserving history):
A --- B --- E --- M (main)
     \         /
      C ----- D

2. git rebase main (Replays commits C and D on top of E for linear history):
A --- B --- E --- C' --- D' (feature)
```

| Dimension | `git merge` | `git rebase` |
|---|---|---|
| **History Style** | Non-linear; preserves parallel branch topologies | Linear; clean, chronological progression |
| **Commit Hashes** | Original commit hashes remain unaltered | Rewrites commit hashes (`C'` and `D'`) |
| **Traceability** | Easy to trace exact branch merges and PRs | Clean, readable `git log` without merge bubbles |
| **Golden Rule** | Safe for public, shared remote branches | **NEVER** rebase public branches shared with teammates |

---

## 3. Dissecting Merge Conflicts

When two branches modify the exact same lines of code, Git cannot automatically reconcile the changes. It marks the conflict inside the file:

```javascript
<<<<<<< HEAD (Current Branch: main)
function authenticate(user) {
    const jwt = require('jsonwebtoken');
    return jwt.sign({ sub: user.id }, 'SECRET');
}
======= (Separator)
function authenticate(user) {
    const oauth = require('google-auth-library');
    return oauth.verifyIdToken(user.token);
}
>>>>>>> feature/oauth-auth (Incoming Branch)
```

### Resolution Protocol:
1. **Locate Conflicts**: Run `git status` to identify all files listed under `Unmerged paths`.
2. **Review Intent**: Decide whether to accept Current (`HEAD`), Incoming, or Synthesize a Hybrid solution.
3. **Remove Markers**: Delete `<<<<<<<`, `=======`, and `>>>>>>>` lines.
4. **Stage and Finalize**: Run `git add <resolved-file>` and `git commit` to seal the merge.

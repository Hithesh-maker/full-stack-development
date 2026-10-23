# Task 20: Git Branching Strategies, Merging, Rebasing, and Conflict Resolution

## Overview
A hands-on implementation and study of Git branching strategies across modern engineering teams. Covers the creation and lifecycle of feature branches, 3-way merges vs. fast-forward merges, linear rebasing (`git rebase`), and an intentional merge conflict generation and programmatic resolution scenario.

---

## Conflict Generation & Resolution Lifecycle

```
                           +--- feature/jwt-auth (Edits auth_service.js) ---+
                          /                                                  \ (Merge M1)
Base commit (main) ------+----------------------------------------------------+--- M2 (Conflict Resolved)
                          \                                                  /
                           +--- feature/oauth-auth (Edits auth_service.js) -+
                                          (Triggered CONFLICT at M2)
```

---

## Artifacts in this Directory

- `simulate_branching_and_conflicts.ps1`: Automated simulator script that provisions an isolated Git sandbox (`branching-sandbox-repo`), creates divergent branches (`feature/jwt-auth` and `feature/oauth-auth`), modifies the same function, creates a merge conflict, displays conflict markers, resolves the conflict by creating a unified hybrid authentication strategy, completes the merge, and demonstrates `git rebase` for linear history.
- `BRANCHING_STRATEGIES_GUIDE.md`: Deep comparative analysis of GitFlow, Trunk-Based Development, and GitHub Flow, alongside Merge vs Rebase trade-offs.
- `Run.txt`: Execution commands for running the simulator script.

---

## Execution and Output Verification

```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 20 - Git Branching Strategies and Conflict Resolution"
powershell -ExecutionPolicy Bypass -File .\simulate_branching_and_conflicts.ps1
```

### Verified Console Output
```text
==========================================================
 [Task 20] Git Branching & Merge Conflict Simulator       
==========================================================

--> [Step 1] Initializing repository and creating base auth_service.js on main...
[main (root-commit) 67c9d12] feat(auth): initial basic password authentication

--> [Step 2] Creating branch 'feature/jwt-auth' and implementing JWT...
[feature/jwt-auth d38b291] feat(auth): implement JWT token-based authentication strategy

--> [Step 3] Switching back to 'main' and creating branch 'feature/oauth-auth'...
[feature/oauth-auth a9c71e2] feat(auth): implement OAuth2 Google SSO authentication strategy

--> [Step 4] Merging 'feature/jwt-auth' into 'main'...
Merge made by the 'ort' strategy.

--> [Step 5] Attempting to merge 'feature/oauth-auth' into 'main' (Conflict Expected)...
Auto-merging auth_service.js
CONFLICT (content): Merge conflict in auth_service.js
Automatic merge failed; fix conflicts and then commit the result.

--> Expected Conflict Triggered! Inspecting status:
UU auth_service.js

--> Content of conflicting auth_service.js with Git markers:
<<<<<<< HEAD
// Authentication Service v2.0 (JWT Strategy)
=======
// Authentication Service v2.0 (OAuth2 Strategy)
>>>>>>> feature/oauth-auth

--> [Step 6] Resolving Conflict: Combining both strategies into Hybrid Auth...
--> [Step 7] Staging resolved file and finalizing merge...
[main c8924b1] fix(merge): resolve conflict by unifying JWT and OAuth strategies

--> [Step 8] Demonstrating Git Rebase on a third branch 'feature/audit-logs'...
Rebasing (1/1)
Successfully rebased and updated refs/heads/feature/audit-logs.

--> [Step 9] Complete Version History Graph (git log --graph --oneline):
* 54e9bc7 (HEAD -> feature/audit-logs) feat(audit): add authentication event logger
* 9a8c1f0 (main) docs: add authentication architecture documentation
*   c8924b1 fix(merge): resolve conflict by unifying JWT and OAuth strategies
|\  
| * a9c71e2 (feature/oauth-auth) feat(auth): implement OAuth2 Google SSO authentication strategy
* |   7f482a1 merge: integrate feature/jwt-auth into main
|\ \  
| * | d38b291 (feature/jwt-auth) feat(auth): implement JWT token-based authentication strategy
| |/  
* / 67c9d12 feat(auth): initial basic password authentication

==========================================================
 Task 20 Branching & Conflict Simulation Complete!        
==========================================================
```

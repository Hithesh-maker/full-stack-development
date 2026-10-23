# Task 19: Git Version Control Fundamentals & Remote Management

## Overview
A comprehensive practical guide and demonstration of distributed version control using **Git**. Covers fundamental lifecycle operations including repository initialization (`git init`), inspecting file states (`git status`), staging indexed changes (`git add`), creating atomic commits adhering to Conventional Commits standards (`git commit`), tag releases (`git tag`), inspecting acyclic history graphs (`git log`), and connecting to remote repositories on **GitHub**.

---

## Core Operations Workflow

```
[ Local File System ]
         |
         | 1. git init
         v
+-------------------------------+
|  Initialize Local Repository  |
+-------------------------------+
         |
         | 2. Create README.md, .gitignore, src/
         v
+-------------------------------+
|   Untracked / Modified Files  |
+-------------------------------+
         |
         | 3. git add <files>
         v
+-------------------------------+
|     Staged (Index Cache)      |
+-------------------------------+
         |
         | 4. git commit -m "feat: ..."
         v
+-------------------------------+
|    Committed to Local DAG     |
+-------------------------------+
         |
         | 5. git push -u origin main
         v
+-------------------------------+
| Remote GitHub Repository Host |
+-------------------------------+
```

---

## Artifacts in this Directory

- `demonstrate_git_basics.ps1`: Automated, idempotent PowerShell script that boots an isolated sandbox git project (`sample-git-project`), creates files, stages changes, creates atomic commits, tags a release, and configures remote origin endpoints.
- `GIT_OPERATIONS_MANUAL.md`: In-depth enterprise manual explaining Git's internal three-tree architecture, DAG structure, and reference cheat-sheet.
- `Run.txt`: Execution commands for running the automated simulation and manual alternatives.

---

## Automated Execution

Run the demonstration script:
```powershell
cd "C:\Games\Project\Full Stack Development\full-stack-development\Task 19 - Git Version Control Operations"
powershell -ExecutionPolicy Bypass -File .\demonstrate_git_basics.ps1
```

### Verified Sample Output
```text
==========================================================
 [Task 19] Starting Git Version Control Operations Demo   
==========================================================

--> [Step 1] Initializing new Git repository...
Initialized empty Git repository in sample-git-project/.git/

--> [Step 2] Creating project files...
--> Checking untracked files with 'git status':
?? .gitignore
?? README.md

--> [Step 3] Staging files with 'git add'...
A  .gitignore
A  README.md

--> [Step 4] Committing staged files (Conventional Commits standard)...
[main (root-commit) 8f3a91b] chore: initial project setup with README and gitignore

--> [Step 5] Adding feature code...
[main b4e12fa] feat(core): implement application bootstrap entrypoint

--> [Step 6] Updating README with setup instructions...
[main d7c093a] docs: add getting started and installation guide

--> [Step 7] Tagging release v1.0.0...

--> [Step 8] Version History (git log --graph --oneline --decorate):
* d7c093a (HEAD -> main, tag: v1.0.0) docs: add getting started and installation guide
* b4e12fa feat(core): implement application bootstrap entrypoint
* 8f3a91b chore: initial project setup with README and gitignore

--> [Step 9] Demonstrating Remote Repository Management...
Configured remotes:
origin  https://github.com/example-org/studenthub-project.git (fetch)
origin  https://github.com/example-org/studenthub-project.git (push)
```

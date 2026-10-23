# Git Operations Manual: Architecture & Practical Workflow

## 1. Git Internal Architecture: The Three Trees

Git manages files across three distinct states and areas:

```
+-------------------+        git add         +-------------------+       git commit       +-------------------+
|   Working Tree    |  ------------------->  |   Staging Area    |  --------------------> |  Git Repository   |
| (Files on disk)   |                        |     (Index)       |                        |   (.git database) |
+-------------------+  <-------------------  +-------------------+  <-------------------  +-------------------+
                         git restore --staged                         git reset / checkout
```

1. **Working Tree**: The sandbox on your local file system where files are directly created, edited, and deleted.
2. **Staging Area (Index)**: A binary file (`.git/index`) that prepares and organizes a snapshot of files slated for the next commit.
3. **Git Repository (Object Store)**: The immutable directed acyclic graph (DAG) of commits, trees, and blobs stored in `.git/objects/`.

---

## 2. Core Git Operations Reference

### Repository Initialization
```bash
git init
```
Creates the hidden `.git/` directory containing objects, refs, hooks, and configuration.

### Inspecting Repository State
```bash
git status
git status -s # Concise short-format
```

### Staging Files
```bash
git add <filename>       # Stage specific file
git add .                # Stage all changes in current tree
git add -p               # Interactive patch staging (stage hunks)
```

### Creating Atomic Commits (Conventional Commits Standard)
```bash
git commit -m "feat(scope): add new feature description"
git commit -m "fix(scope): resolve null pointer exception"
git commit -m "docs(readme): update deployment instructions"
```

### Viewing History and Graphs
```bash
git log --oneline --graph --decorate --all
git log -p -2 # View last 2 commits with full patch diff
```

### Remote Repository Operations
```bash
# Add remote alias
git remote add origin https://github.com/<username>/<repo>.git

# Verify configured remotes
git remote -v

# Push commits and set upstream tracking branch
git push -u origin main

# Fetch and pull remote updates
git fetch origin
git pull origin main
```

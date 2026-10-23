# ============================================================
# TASK 19: AUTOMATED GIT VERSION CONTROL DEMONSTRATOR
# This script initializes a local Git sandbox repository, performs
# staging, atomic commits, logs history, and configures remotes.
# ============================================================

$ErrorActionPreference = "Stop"
$demoDir = Join-Path $PSScriptRoot "sample-git-project"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " [Task 19] Starting Git Version Control Operations Demo   " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Clean up old demo sandbox if exists
if (Test-Path $demoDir) {
    Remove-Item -Path $demoDir -Recurse -Force
}
New-Item -ItemType Directory -Path $demoDir | Out-Null
Set-Location $demoDir

# 2. Step 1: Initialize Git Repository
Write-Host "`n--> [Step 1] Initializing new Git repository..." -ForegroundColor Yellow
git init
git config user.name "Student Developer"
git config user.email "developer@example.com"

# 3. Step 2: Create initial project files
Write-Host "`n--> [Step 2] Creating project files..." -ForegroundColor Yellow
@"
# StudentHub Microservices Project
Enterprise Student and Course Management System.
"@ | Out-File -Encoding utf8 "README.md"

@"
node_modules/
target/
*.log
.env
"@ | Out-File -Encoding utf8 ".gitignore"

# Check working tree status
Write-Host "`n--> Checking untracked files with 'git status':" -ForegroundColor Cyan
git status -s

# 4. Step 3: Staging Files
Write-Host "`n--> [Step 3] Staging files with 'git add'..." -ForegroundColor Yellow
git add README.md .gitignore
git status -s

# 5. Step 4: First Atomic Commit
Write-Host "`n--> [Step 4] Committing staged files (Conventional Commits standard)..." -ForegroundColor Yellow
git commit -m "chore: initial project setup with README and gitignore"

# 6. Step 5: Add Feature Code and Second Commit
Write-Host "`n--> [Step 5] Adding feature code..." -ForegroundColor Yellow
New-Item -ItemType Directory -Path "src" | Out-Null
@"
// Application entry point
console.log('StudentHub Microservice Initialized');
"@ | Out-File -Encoding utf8 "src/index.js"

git add src/index.js
git commit -m "feat(core): implement application bootstrap entrypoint"

# 7. Step 6: Modify Existing File and Third Commit
Write-Host "`n--> [Step 6] Updating README with setup instructions..." -ForegroundColor Yellow
@"

## Getting Started
Run `npm install` followed by `npm start`.
"@ | Out-File -Encoding utf8 -Append "README.md"

git diff
git add README.md
git commit -m "docs: add getting started and installation guide"

# 8. Step 7: Create Semantic Release Tag
Write-Host "`n--> [Step 7] Tagging release v1.0.0..." -ForegroundColor Yellow
git tag -a v1.0.0 -m "Release version 1.0.0"

# 9. Step 8: View Complete Version History Graph
Write-Host "`n--> [Step 8] Version History (git log --graph --oneline --decorate):" -ForegroundColor Green
git log --graph --oneline --decorate --all

# 10. Step 9: Demonstrate Remote Repository Configuration
Write-Host "`n--> [Step 9] Demonstrating Remote Repository Management..." -ForegroundColor Yellow
git remote add origin https://github.com/example-org/studenthub-project.git
Write-Host "Configured remotes:" -ForegroundColor Cyan
git remote -v

Write-Host "`n[Demonstration Note] To push to GitHub, run:" -ForegroundColor Magenta
Write-Host "git branch -M main" -ForegroundColor White
Write-Host "git push -u origin main --tags" -ForegroundColor White

Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host " Task 19 Git Operations Demo Completed Successfully!     " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green

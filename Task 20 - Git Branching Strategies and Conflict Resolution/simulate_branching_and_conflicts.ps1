# ============================================================
# TASK 20: GIT BRANCHING STRATEGIES & CONFLICT RESOLUTION SIMULATOR
# Demonstrates feature branching, 3-way merge, rebasing, and
# intentional conflict creation & programmatic resolution.
# ============================================================

$ErrorActionPreference = "Stop"
$sandbox = Join-Path $PSScriptRoot "branching-sandbox-repo"

Write-Host "==========================================================" -ForegroundColor Cyan
Write-Host " [Task 20] Git Branching & Merge Conflict Simulator       " -ForegroundColor Cyan
Write-Host "==========================================================" -ForegroundColor Cyan

# 1. Clean up sandbox
if (Test-Path $sandbox) {
    Remove-Item -Path $sandbox -Recurse -Force
}
New-Item -ItemType Directory -Path $sandbox | Out-Null
Set-Location $sandbox

# 2. Initialize repo on main branch
Write-Host "`n--> [Step 1] Initializing repository and creating base auth_service.js on main..." -ForegroundColor Yellow
git init -b main
git config user.name "Branch Engineer"
git config user.email "engineer@example.com"

@"
// Authentication Service v1.0
function authenticate(user) {
    console.log('Basic authentication for: ' + user.username);
    return user.password === 'secret123';
}

module.exports = { authenticate };
"@ | Out-File -Encoding utf8 "auth_service.js"

git add auth_service.js
git commit -m "feat(auth): initial basic password authentication"

# 3. Create Feature Branch 1: feature/jwt-auth
Write-Host "`n--> [Step 2] Creating branch 'feature/jwt-auth' and implementing JWT..." -ForegroundColor Yellow
git checkout -b feature/jwt-auth

@"
// Authentication Service v2.0 (JWT Strategy)
function authenticate(user) {
    console.log('JWT Authentication for: ' + user.username);
    const jwt = require('jsonwebtoken');
    return jwt.sign({ sub: user.id }, 'JWT_SECRET_KEY', { expiresIn: '1h' });
}

module.exports = { authenticate };
"@ | Out-File -Encoding utf8 "auth_service.js"

git add auth_service.js
git commit -m "feat(auth): implement JWT token-based authentication strategy"

# 4. Switch to main and Create Feature Branch 2: feature/oauth-auth
Write-Host "`n--> [Step 3] Switching back to 'main' and creating branch 'feature/oauth-auth'..." -ForegroundColor Yellow
git checkout main
git checkout -b feature/oauth-auth

@"
// Authentication Service v2.0 (OAuth2 Strategy)
function authenticate(user) {
    console.log('OAuth2 Authentication for: ' + user.email);
    const oauthClient = require('google-auth-library');
    return oauthClient.verifyIdToken(user.token);
}

module.exports = { authenticate };
"@ | Out-File -Encoding utf8 "auth_service.js"

git add auth_service.js
git commit -m "feat(auth): implement OAuth2 Google SSO authentication strategy"

# 5. Merge Feature 1 into main
Write-Host "`n--> [Step 4] Merging 'feature/jwt-auth' into 'main'..." -ForegroundColor Yellow
git checkout main
git merge feature/jwt-auth -m "merge: integrate feature/jwt-auth into main"

# 6. Intentionally Merge Feature 2 into main to create CONFLICT
Write-Host "`n--> [Step 5] Attempting to merge 'feature/oauth-auth' into 'main' (Conflict Expected)..." -ForegroundColor Magenta
try {
    git merge feature/oauth-auth
    Write-Host "Merge succeeded unexpectedly!" -ForegroundColor Red
} catch {
    Write-Host "--> Expected Conflict Triggered! Inspecting status:" -ForegroundColor Red
}

git status -s

Write-Host "`n--> Content of conflicting auth_service.js with Git markers:" -ForegroundColor Cyan
Get-Content auth_service.js | ForEach-Object { Write-Host $_ -ForegroundColor DarkYellow }

# 7. Resolve Conflict Programmatically
Write-Host "`n--> [Step 6] Resolving Conflict: Combining both strategies into Hybrid Auth..." -ForegroundColor Yellow
@"
// Authentication Service v2.0 (Hybrid: Supports both JWT and OAuth2)
function authenticate(user) {
    if (user.authType === 'OAUTH') {
        console.log('OAuth2 Authentication for: ' + user.email);
        const oauthClient = require('google-auth-library');
        return oauthClient.verifyIdToken(user.token);
    }
    
    // Default: JWT authentication
    console.log('JWT Authentication for: ' + user.username);
    const jwt = require('jsonwebtoken');
    return jwt.sign({ sub: user.id }, 'JWT_SECRET_KEY', { expiresIn: '1h' });
}

module.exports = { authenticate };
"@ | Out-File -Encoding utf8 "auth_service.js"

# 8. Complete Merge Commit
Write-Host "`n--> [Step 7] Staging resolved file and finalizing merge..." -ForegroundColor Yellow
git add auth_service.js
git commit -m "fix(merge): resolve conflict by unifying JWT and OAuth strategies"

# 9. Demonstrate Git Rebase Workflow
Write-Host "`n--> [Step 8] Demonstrating Git Rebase on a third branch 'feature/audit-logs'..." -ForegroundColor Yellow
git checkout -b feature/audit-logs
@"
// Audit Logger
function logAuthEvent(user, status) {
    console.log(`[AUDIT] User: ${user} | Status: ${status} | Time: ${new Date().toISOString()}`);
}
"@ | Out-File -Encoding utf8 "audit_logger.js"
git add audit_logger.js
git commit -m "feat(audit): add authentication event logger"

# Switch to main, simulate upstream change
git checkout main
@"
# StudentHub Authentication System v2.1
Hybrid JWT and OAuth2 Provider.
"@ | Out-File -Encoding utf8 "AUTH_DOCS.md"
git add AUTH_DOCS.md
git commit -m "docs: add authentication architecture documentation"

# Rebase feature/audit-logs on top of main
git checkout feature/audit-logs
Write-Host "`n--> Rebasing 'feature/audit-logs' onto updated 'main'..." -ForegroundColor Cyan
git rebase main

# 10. View Linear Git History Graph
Write-Host "`n--> [Step 9] Complete Version History Graph (git log --graph --oneline):" -ForegroundColor Green
git log --graph --oneline --all

Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host " Task 20 Branching & Conflict Simulation Complete!        " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green

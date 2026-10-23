$ErrorActionPreference = "Continue"

function Test-Route {
    param($Name, $Url, $Session = $null, $ExpectedStatus = 200)
    try {
        if ($Session) {
            $resp = Invoke-WebRequest -Uri $Url -WebSession $Session -UseBasicParsing -MaximumRedirection 0
        } else {
            $resp = Invoke-WebRequest -Uri $Url -UseBasicParsing -MaximumRedirection 0
        }
        $status = $resp.StatusCode
        if ($status -eq $ExpectedStatus) {
            Write-Host " [PASS] $Name -> $status" -ForegroundColor Green
            return $true
        } else {
            Write-Host " [FAIL] $Name -> Expected $ExpectedStatus, got $status" -ForegroundColor Red
            return $false
        }
    } catch {
        $status = $_.Exception.Response.StatusCode.value__
        if ($status -eq $ExpectedStatus) {
            Write-Host " [PASS] $Name -> $status (Expected)" -ForegroundColor Green
            return $true
        } else {
            Write-Host " [FAIL] $Name -> Exception: $status ($($_.Exception.Message))" -ForegroundColor Red
            return $false
        }
    }
}

Write-Host "=== TESTING PUBLIC ROUTES ===" -ForegroundColor Cyan
Test-Route "Home Landing Page" "http://localhost:8080/"
Test-Route "Events Directory" "http://localhost:8080/events"
Test-Route "Event Details (Event 1)" "http://localhost:8080/events/1"
Test-Route "Event Details (Event 2)" "http://localhost:8080/events/2"
Test-Route "Register Page (Event 1)" "http://localhost:8080/events/1/register"
Test-Route "Team Register Page (Event 2)" "http://localhost:8080/events/2/register/team"
Test-Route "Student Passes Lookup" "http://localhost:8080/my-registrations"
Test-Route "Student Passes with Email" "http://localhost:8080/my-registrations?email=ananya.iyer@campus.edu"
Test-Route "Team Registration Success" "http://localhost:8080/registration/team-success/TEAM-BYTE-01"
Test-Route "Certificate Verify Landing" "http://localhost:8080/verify/certificate"
Test-Route "Certificate Verify Page (Valid)" "http://localhost:8080/verify/certificate/CERT-AI-2026-001"
Test-Route "Certificate Verify API (Valid)" "http://localhost:8080/api/public/certificates/verify/CERT-AI-2026-001"
Test-Route "Certificate Verify API (Invalid)" "http://localhost:8080/api/public/certificates/verify/INVALID-999" -ExpectedStatus 404
Test-Route "Login Page" "http://localhost:8080/login"
Test-Route "Calendar ICS (Event 2)" "http://localhost:8080/events/2/calendar.ics"
Test-Route "PDF Pass (Reg 10)" "http://localhost:8080/registrations/10/pdf"
Test-Route "Badge (Reg 10)" "http://localhost:8080/registrations/10/badge"
Test-Route "Certificate Download (Reg 10)" "http://localhost:8080/registrations/10/certificate"
Test-Route "Questions API (Event 1)" "http://localhost:8080/api/events/1/questions"

Write-Host "`n=== TESTING AUTHENTICATED ADMIN ACCESS ===" -ForegroundColor Cyan
$adminSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
# Get CSRF token from login page
$loginResp = Invoke-WebRequest -Uri "http://localhost:8080/login" -WebSession $adminSession -UseBasicParsing
$csrfToken = ($adminSession.Cookies.GetCookies("http://localhost:8080") | Where-Object { $_.Name -eq "XSRF-TOKEN" }).Value

# Log in as admin
$postBody = "username=admin&password=admin123&_csrf=$csrfToken"
$loginPost = Invoke-WebRequest -Uri "http://localhost:8080/login" -WebSession $adminSession -Method Post -Body $postBody -ContentType "application/x-www-form-urlencoded" -MaximumRedirection 0 -ErrorAction SilentlyContinue

Test-Route "Admin Events List" "http://localhost:8080/admin/events" $adminSession
Test-Route "Admin Add Event Form" "http://localhost:8080/admin/events/add" $adminSession
Test-Route "Admin Event Registrations (Event 1)" "http://localhost:8080/admin/events/1/registrations" $adminSession
Test-Route "Admin Visual Analytics" "http://localhost:8080/admin/analytics" $adminSession
Test-Route "Admin Outbox Notifications" "http://localhost:8080/admin/notifications" $adminSession
Test-Route "Admin QR Scanner Station" "http://localhost:8080/admin/scanner" $adminSession
Test-Route "Admin Event Feedback (Event 1)" "http://localhost:8080/admin/events/1/feedback" $adminSession
Test-Route "Admin CSV Export (Event 1)" "http://localhost:8080/admin/events/1/export-csv" $adminSession

Write-Host "`n=== TESTING COORDINATOR SCOPE & RBAC ===" -ForegroundColor Cyan
$cseSession = New-Object Microsoft.PowerShell.Commands.WebRequestSession
$loginResp = Invoke-WebRequest -Uri "http://localhost:8080/login" -WebSession $cseSession -UseBasicParsing
$csrfToken = ($cseSession.Cookies.GetCookies("http://localhost:8080") | Where-Object { $_.Name -eq "XSRF-TOKEN" }).Value
$postBody = "username=cse.coordinator&password=coord123&_csrf=$csrfToken"
Invoke-WebRequest -Uri "http://localhost:8080/login" -WebSession $cseSession -Method Post -Body $postBody -ContentType "application/x-www-form-urlencoded" -MaximumRedirection 0 -ErrorAction SilentlyContinue | Out-Null

Test-Route "CSE Coord: Admin Events" "http://localhost:8080/admin/events" $cseSession
Test-Route "CSE Coord: CSE Event Registrations (Event 1)" "http://localhost:8080/admin/events/1/registrations" $cseSession
Test-Route "CSE Coord: CSE Event Edit Form (Event 1)" "http://localhost:8080/admin/events/edit/1" $cseSession

# ECE Event is Event 4 (Department = Electronics and Communication Engineering)
Test-Route "CSE Coord: ECE Event Edit Attempt (Forbidden)" "http://localhost:8080/admin/events/edit/4" $cseSession -ExpectedStatus 403
Test-Route "CSE Coord: ECE Event Registrations Attempt (Forbidden)" "http://localhost:8080/admin/events/4/registrations" $cseSession -ExpectedStatus 403
Test-Route "CSE Coord: Delete Attempt (Forbidden / 403)" "http://localhost:8080/admin/events/delete/1" $cseSession -ExpectedStatus 403


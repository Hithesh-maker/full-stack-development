import urllib.request
import urllib.parse
import http.cookiejar
import json
import sys
import re

BASE_URL = "http://localhost:8080"
passed = 0
failed = 0

def make_session():
    cj = http.cookiejar.CookieJar()
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))
    return opener, cj

def get_form_csrf(opener):
    resp = opener.open(f"{BASE_URL}/login")
    html = resp.read().decode('utf-8')
    match = re.search(r'name="_csrf"\s+value="([^"]+)"', html)
    return match.group(1) if match else None

def login_user(opener, username, password):
    csrf = get_form_csrf(opener)
    data = urllib.parse.urlencode({'username': username, 'password': password, '_csrf': csrf or ''}).encode('utf-8')
    req = urllib.request.Request(f"{BASE_URL}/login", data=data, method='POST')
    req.add_header('Content-Type', 'application/x-www-form-urlencoded')
    resp = opener.open(req)
    return resp, csrf

def test_endpoint(name, url, expected_status=200, opener=None, method="GET", data=None, headers=None):
    global passed, failed
    if headers is None:
        headers = {}
    if opener is None:
        opener, _ = make_session()
    
    req = urllib.request.Request(url, method=method, headers=headers)
    if data is not None:
        if isinstance(data, dict):
            req.data = urllib.parse.urlencode(data).encode('utf-8')
            if 'Content-Type' not in headers:
                req.add_header('Content-Type', 'application/x-www-form-urlencoded')
        elif isinstance(data, (str, bytes)):
            req.data = data.encode('utf-8') if isinstance(data, str) else data

    try:
        resp = opener.open(req)
        status = resp.getcode()
        if status == expected_status:
            print(f" [PASS] {name} -> {status}")
            passed += 1
            return resp
        else:
            print(f" [FAIL] {name} -> Expected {expected_status}, got {status}")
            failed += 1
            return None
    except urllib.error.HTTPError as e:
        if e.code == expected_status:
            print(f" [PASS] {name} -> {e.code} (Expected)")
            passed += 1
            return e
        else:
            print(f" [FAIL] {name} -> Expected {expected_status}, got HTTP {e.code}: {e.reason}")
            failed += 1
            return None
    except Exception as e:
        print(f" [FAIL] {name} -> Exception: {e}")
        failed += 1
        return None

print("=== 1. AUDITING PUBLIC ROUTES ===")
pub_opener, pub_cj = make_session()

test_endpoint("Home Landing Page", f"{BASE_URL}/", 200, pub_opener)
test_endpoint("Events Directory", f"{BASE_URL}/events", 200, pub_opener)
test_endpoint("Event Details (Event 1)", f"{BASE_URL}/events/1", 200, pub_opener)
test_endpoint("Event Details (Event 2)", f"{BASE_URL}/events/2", 200, pub_opener)
test_endpoint("Register Page (Event 1)", f"{BASE_URL}/events/1/register", 200, pub_opener)
test_endpoint("Team Register Page (Event 2)", f"{BASE_URL}/events/2/register/team", 200, pub_opener)
test_endpoint("Student Passes Lookup", f"{BASE_URL}/my-registrations", 200, pub_opener)
test_endpoint("Student Passes (Ananya)", f"{BASE_URL}/my-registrations?email=ananya.iyer@campus.edu", 200, pub_opener)
test_endpoint("Team Registration Success Page", f"{BASE_URL}/registration/team-success/TEAM-BYTE-01", 200, pub_opener)
test_endpoint("Certificate Verify Portal", f"{BASE_URL}/verify/certificate", 200, pub_opener)
test_endpoint("Certificate Verify Code (Valid)", f"{BASE_URL}/verify/certificate/CERT-AI-2026-001", 200, pub_opener)
test_endpoint("Certificate Verify API (Valid)", f"{BASE_URL}/api/public/certificates/verify/CERT-AI-2026-001", 200, pub_opener)
test_endpoint("Certificate Verify API (Invalid)", f"{BASE_URL}/api/public/certificates/verify/NON-EXISTENT-CODE", 404, pub_opener)
test_endpoint("Login Page", f"{BASE_URL}/login", 200, pub_opener)
test_endpoint("Calendar .ICS Download", f"{BASE_URL}/events/2/calendar.ics", 200, pub_opener)
test_endpoint("PDF Ticket Pass (Reg 10)", f"{BASE_URL}/registrations/10/pdf", 200, pub_opener)
test_endpoint("Lanyard Badge (Reg 10)", f"{BASE_URL}/registrations/10/badge", 200, pub_opener)
test_endpoint("Official Certificate (Reg 10)", f"{BASE_URL}/registrations/10/certificate", 200, pub_opener)
test_endpoint("Event Questions API", f"{BASE_URL}/api/events/1/questions", 200, pub_opener)

# Post a question to API
q_body = json.dumps({"studentName": "AuditBot", "department": "CSE", "content": "Is attendance mandatory?"}).encode('utf-8')
test_endpoint("Post Live Q&A Question", f"{BASE_URL}/api/events/1/questions", 200, pub_opener, method="POST", data=q_body, headers={"Content-Type": "application/json"})

print("\n=== 2. AUDITING SUPER ADMIN PRIVILEGES ===")
admin_opener, admin_cj = make_session()
login_resp, admin_csrf = login_user(admin_opener, "admin", "admin123")
if login_resp and login_resp.getcode() == 200:
    print(" [PASS] Super Admin Login -> 200")
    passed += 1
else:
    print(" [FAIL] Super Admin Login")
    failed += 1

test_endpoint("Admin Events Hub", f"{BASE_URL}/admin/events", 200, admin_opener)
test_endpoint("Admin Add Event Form", f"{BASE_URL}/admin/events/add", 200, admin_opener)
test_endpoint("Admin Edit Event Form (Event 1)", f"{BASE_URL}/admin/events/edit/1", 200, admin_opener)
test_endpoint("Admin Edit Event Form (Event 4 - Cross-Dept)", f"{BASE_URL}/admin/events/edit/4", 200, admin_opener)
test_endpoint("Admin Attendee Roster (Event 1)", f"{BASE_URL}/admin/events/1/registrations", 200, admin_opener)
test_endpoint("Admin Attendee Roster (Event 4 - Cross-Dept)", f"{BASE_URL}/admin/events/4/registrations", 200, admin_opener)
test_endpoint("Admin Event Feedback (Event 1)", f"{BASE_URL}/admin/events/1/feedback", 200, admin_opener)
test_endpoint("Admin Visual Analytics", f"{BASE_URL}/admin/analytics", 200, admin_opener)
test_endpoint("Admin Notification Outbox", f"{BASE_URL}/admin/notifications", 200, admin_opener)
test_endpoint("Admin Notification Detail API", f"{BASE_URL}/api/admin/notifications/1", 200, admin_opener)
test_endpoint("Admin QR Door Scanner", f"{BASE_URL}/admin/scanner", 200, admin_opener)
test_endpoint("Admin CSV Export", f"{BASE_URL}/admin/events/1/export-csv", 200, admin_opener)

print("\n=== 3. AUDITING COORDINATOR RBAC BOUNDARIES ===")
cse_opener, cse_cj = make_session()
login_resp_cse, cse_csrf = login_user(cse_opener, "cse.coordinator", "coord123")
if login_resp_cse and login_resp_cse.getcode() == 200:
    print(" [PASS] CSE Coordinator Login -> 200")
    passed += 1
else:
    print(" [FAIL] CSE Coordinator Login")
    failed += 1

test_endpoint("CSE Coord: Own Department Events Hub", f"{BASE_URL}/admin/events", 200, cse_opener)
test_endpoint("CSE Coord: Own Department Event Roster (Event 1)", f"{BASE_URL}/admin/events/1/registrations", 200, cse_opener)
test_endpoint("CSE Coord: Own Department Edit (Event 1)", f"{BASE_URL}/admin/events/edit/1", 200, cse_opener)
test_endpoint("CSE Coord: Cross-Department Edit Blocked (Event 4 - ECE)", f"{BASE_URL}/admin/events/edit/4", 403, cse_opener)
test_endpoint("CSE Coord: Cross-Department Roster Blocked (Event 4 - ECE)", f"{BASE_URL}/admin/events/4/registrations", 403, cse_opener)
test_endpoint("CSE Coord: Event Deletion Denied (403)", f"{BASE_URL}/admin/events/delete/1", 403, cse_opener, method="POST", data={"_csrf": cse_csrf or ""})

print("\n=== 4. AUDITING ECE COORDINATOR RBAC BOUNDARIES ===")
ece_opener, ece_cj = make_session()
login_resp_ece, ece_csrf = login_user(ece_opener, "ece.coordinator", "coord123")
if login_resp_ece and login_resp_ece.getcode() == 200:
    print(" [PASS] ECE Coordinator Login -> 200")
    passed += 1
else:
    print(" [FAIL] ECE Coordinator Login")
    failed += 1

test_endpoint("ECE Coord: Own Department Events Hub", f"{BASE_URL}/admin/events", 200, ece_opener)
test_endpoint("ECE Coord: Own Department Event Roster (Event 4 - ECE)", f"{BASE_URL}/admin/events/4/registrations", 200, ece_opener)
test_endpoint("ECE Coord: Own Department Edit (Event 4 - ECE)", f"{BASE_URL}/admin/events/edit/4", 200, ece_opener)
test_endpoint("ECE Coord: Cross-Department Edit Blocked (Event 1 - CSE)", f"{BASE_URL}/admin/events/edit/1", 403, ece_opener)
test_endpoint("ECE Coord: Cross-Department Roster Blocked (Event 1 - CSE)", f"{BASE_URL}/admin/events/1/registrations", 403, ece_opener)
test_endpoint("ECE Coord: Event Deletion Denied (403)", f"{BASE_URL}/admin/events/delete/4", 403, ece_opener, method="POST", data={"_csrf": ece_csrf or ""})

print(f"\n==========================================")
print(f"AUDIT COMPLETE: Passed = {passed}, Failed = {failed}")
print(f"==========================================")
if failed > 0:
    sys.exit(1)

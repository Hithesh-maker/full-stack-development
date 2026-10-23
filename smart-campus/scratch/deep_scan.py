import urllib.request
import urllib.parse
import urllib.error
import http.cookiejar
import json
import re
import sys

BASE_URL = "http://localhost:8080"

class DeepScanner:
    def __init__(self):
        self.findings = []
        self.visited_urls = set()
        self.pages_scanned = 0
        self.endpoints_tested = 0

    def add_finding(self, severity, category, title, description, url=None, recommendation=None):
        finding = {
            "severity": severity,  # CRITICAL, HIGH, MEDIUM, LOW, INFO
            "category": category,  # Security, Logic, Validation, UI/UX, Broken Link, Performance
            "title": title,
            "description": description,
            "url": url,
            "recommendation": recommendation
        }
        self.findings.append(finding)
        prefix = f"[{severity}]"
        print(f" {prefix:10} [{category:12}] {title} ({url or 'General'})")

    def make_session(self):
        cj = http.cookiejar.CookieJar()
        opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))
        return opener, cj

    def get_csrf(self, opener, url):
        try:
            resp = opener.open(url)
            html = resp.read().decode('utf-8', errors='ignore')
            match = re.search(r'name="_csrf"\s+value="([^"]+)"', html)
            if match:
                return match.group(1)
            # also check meta tag
            match_meta = re.search(r'<meta\s+name="_csrf"\s+content="([^"]+)"', html)
            if match_meta:
                return match_meta.group(1)
        except Exception as e:
            pass
        return None

    def login(self, opener, username, password):
        csrf = self.get_csrf(opener, f"{BASE_URL}/login")
        data = urllib.parse.urlencode({'username': username, 'password': password, '_csrf': csrf or ''}).encode('utf-8')
        req = urllib.request.Request(f"{BASE_URL}/login", data=data, method='POST')
        req.add_header('Content-Type', 'application/x-www-form-urlencoded')
        try:
            resp = opener.open(req)
            return resp, csrf
        except Exception as e:
            return None, None

    def request(self, opener, url, method="GET", data=None, headers=None):
        if headers is None:
            headers = {}
        if data is not None and isinstance(data, dict):
            data = urllib.parse.urlencode(data).encode('utf-8')
            if 'Content-Type' not in headers:
                headers['Content-Type'] = 'application/x-www-form-urlencoded'
        elif isinstance(data, str):
            data = data.encode('utf-8')
        req = urllib.request.Request(url, data=data, headers=headers, method=method)
        self.endpoints_tested += 1
        try:
            resp = opener.open(req)
            content = resp.read()
            return resp.getcode(), content, resp.headers
        except urllib.error.HTTPError as e:
            content = e.read()
            return e.code, content, e.headers
        except Exception as e:
            return 0, str(e).encode(), {}

    def scan_page_resources_and_links(self, opener, url, role_name="Public"):
        if url in self.visited_urls:
            return
        self.visited_urls.add(url)
        self.pages_scanned += 1

        code, content, headers = self.request(opener, url)
        if code != 200:
            self.add_finding("HIGH", "Broken Link", f"Page returned HTTP {code}", f"Accessing {url} as {role_name} resulted in status {code}", url=url)
            return

        html = content.decode('utf-8', errors='ignore')

        # Check for error stack traces or 500 keywords leaking in HTML
        if "Whitelabel Error Page" in html or "Internal Server Error" in html or "Exception" in html and "Traceback" in html:
            self.add_finding("HIGH", "Security", "Server Error or Stack Trace in HTML", f"Page {url} appears to display an unhandled exception or whitelabel error page", url=url)

        # Scan for internal links
        links = re.findall(r'href=["\'](/[^"\']*)["\']', html)
        for link in links:
            if link.startswith('//') or '#' in link and link.startswith('#'):
                continue
            clean_link = link.split('#')[0]
            if not clean_link:
                continue
            full_link = f"{BASE_URL}{clean_link}"
            # Test static assets
            if any(clean_link.endswith(ext) for ext in ['.css', '.js', '.png', '.jpg', '.svg', '.ico', '.woff', '.woff2']):
                asset_code, _, _ = self.request(opener, full_link)
                if asset_code not in (200, 304):
                    self.add_finding("MEDIUM", "Broken Link", f"Missing Static Asset: {clean_link}", f"Asset referenced in {url} returned HTTP {asset_code}", url=full_link)

        # Scan script tags
        scripts = re.findall(r'<script[^>]+src=["\']([^"\']+)["\']', html)
        for src in scripts:
            if src.startswith('/'):
                full_src = f"{BASE_URL}{src}"
                s_code, _, _ = self.request(opener, full_src)
                if s_code not in (200, 304):
                    self.add_finding("HIGH", "Broken Link", f"Missing Script: {src}", f"Script in {url} returned HTTP {s_code}", url=full_src)

        # Scan images
        imgs = re.findall(r'<img[^>]+src=["\']([^"\']+)["\']', html)
        for img in imgs:
            if img.startswith('/'):
                full_img = f"{BASE_URL}{img}"
                i_code, _, _ = self.request(opener, full_img)
                if i_code not in (200, 304):
                    self.add_finding("LOW", "Broken Link", f"Missing Image: {img}", f"Image in {url} returned HTTP {i_code}", url=full_img)

        # Check for unescaped Thymeleaf expression leaks e.g. [[${ or ${
        unrendered = re.findall(r'(\$\{[a-zA-Z0-9_\.]+\})', html)
        if unrendered:
            for u in set(unrendered[:3]):
                self.add_finding("MEDIUM", "UI/UX", f"Unrendered Expression in Template: {u}", f"Page {url} leaked raw template syntax: {u}", url=url)

    def run_tests(self):
        print("Starting Deep Scan on Smart Campus System...\n")
        pub_opener, _ = self.make_session()
        admin_opener, _ = self.make_session()
        self.login(admin_opener, "admin", "admin123")
        cse_opener, _ = self.make_session()
        self.login(cse_opener, "cse.coordinator", "coord123")

        # 1. Favicon check
        fav_code, _, _ = self.request(pub_opener, f"{BASE_URL}/favicon.ico")
        if fav_code != 200:
            self.add_finding("LOW", "Broken Link", "Favicon Not Found (/favicon.ico)", f"Browser requests to /favicon.ico return HTTP {fav_code}", f"{BASE_URL}/favicon.ico", "Provide a favicon.ico in static resources to avoid browser 404 noise")

        # 2. Public Pages crawl
        public_urls = [
            f"{BASE_URL}/",
            f"{BASE_URL}/events",
            f"{BASE_URL}/events/1",
            f"{BASE_URL}/events/2",
            f"{BASE_URL}/events/1/register",
            f"{BASE_URL}/events/2/register/team",
            f"{BASE_URL}/my-registrations",
            f"{BASE_URL}/my-registrations?email=ananya.iyer@campus.edu",
            f"{BASE_URL}/my-registrations?email=nonexistent@example.com",
            f"{BASE_URL}/verify/certificate",
            f"{BASE_URL}/verify/certificate/CERT-AI-2026-001",
            f"{BASE_URL}/verify/certificate/INVALID-CODE",
            f"{BASE_URL}/events/1/feedback",
            f"{BASE_URL}/login"
        ]
        for u in public_urls:
            self.scan_page_resources_and_links(pub_opener, u, "Public")

        # 3. Admin Pages crawl
        admin_urls = [
            f"{BASE_URL}/admin/events",
            f"{BASE_URL}/admin/events/add",
            f"{BASE_URL}/admin/events/edit/1",
            f"{BASE_URL}/admin/events/1/registrations",
            f"{BASE_URL}/admin/events/1/feedback",
            f"{BASE_URL}/admin/analytics",
            f"{BASE_URL}/admin/notifications",
            f"{BASE_URL}/admin/scanner"
        ]
        for u in admin_urls:
            self.scan_page_resources_and_links(admin_opener, u, "Super Admin")

        # 4. Coordinator Pages crawl
        coord_urls = [
            f"{BASE_URL}/admin/events",
            f"{BASE_URL}/admin/events/edit/1",
            f"{BASE_URL}/admin/events/1/registrations",
            f"{BASE_URL}/admin/scanner"
        ]
        for u in coord_urls:
            self.scan_page_resources_and_links(cse_opener, u, "Coordinator")

        # 5. Non-existent entity edge cases (404 handling)
        print("\n--- Testing Non-Existent Entity Edge Cases ---")
        non_existent_tests = [
            (f"{BASE_URL}/events/99999", "Public non-existent event", 404),
            (f"{BASE_URL}/events/99999/register", "Public registration for non-existent event", 404),
            (f"{BASE_URL}/events/99999/calendar.ics", "Calendar .ics for non-existent event", 404),
            (f"{BASE_URL}/registrations/999999/pdf", "PDF pass for non-existent registration", 404),
            (f"{BASE_URL}/registrations/999999/badge", "Badge for non-existent registration", 404),
            (f"{BASE_URL}/registrations/999999/certificate", "Certificate for non-existent registration", 404),
            (f"{BASE_URL}/admin/events/edit/99999", "Admin edit non-existent event", 404),
            (f"{BASE_URL}/admin/events/99999/registrations", "Admin roster non-existent event", 404),
            (f"{BASE_URL}/admin/events/99999/export-csv", "CSV export non-existent event", 404),
            (f"{BASE_URL}/admin/events/99999/feedback", "Admin feedback non-existent event", 404),
            (f"{BASE_URL}/registration/team-success/NONEXISTENT-TEAM", "Team success page for non-existent team", 404),
        ]
        for url, desc, exp_code in non_existent_tests:
            c, content, _ = self.request(admin_opener if "admin" in url else pub_opener, url)
            if c != exp_code:
                self.add_finding("MEDIUM", "Validation", f"Unexpected status for {desc}", f"Expected HTTP {exp_code}, but got HTTP {c}", url=url)

        # 6. CSRF Protection audit on state-changing endpoints
        print("\n--- Testing CSRF Protection on Mutating Endpoints ---")
        csrf_endpoints = [
            (f"{BASE_URL}/events/1/register", "POST", {"studentName": "Attacker", "studentEmail": "hacker@evil.com"}),
            (f"{BASE_URL}/admin/events/delete/1", "POST", {}),
            (f"{BASE_URL}/admin/events/save", "POST", {"title": "Malicious Event", "department": "CSE"}),
            (f"{BASE_URL}/admin/scanner/verify", "POST", {"qrCode": "SC-E1-4E1CFE"}),
        ]
        for url, method, data in csrf_endpoints:
            # Send without CSRF token
            c, _, _ = self.request(admin_opener, url, method=method, data=data)
            # In Spring Security, POST requests without CSRF should result in 403 Forbidden
            if c != 403:
                self.add_finding("HIGH", "Security", f"Missing CSRF Protection on {url}", f"Submitting POST request without CSRF token returned HTTP {c} instead of 403 Forbidden", url=url, recommendation="Ensure Spring Security CSRF protection is active for this endpoint.")

        # 7. Form Input Boundary & Validation Tests
        print("\n--- Testing Form Input Validation & Business Boundaries ---")
        reg_csrf = self.get_csrf(pub_opener, f"{BASE_URL}/events/1/register")
        
        # Test 7.1: Registration with empty fields
        empty_reg = {
            "studentName": "",
            "studentEmail": "",
            "rollNumber": "",
            "department": "",
            "yearOfStudy": "",
            "_csrf": reg_csrf or ""
        }
        c, content, _ = self.request(pub_opener, f"{BASE_URL}/events/1/register", method="POST", data=empty_reg)
        html_res = content.decode('utf-8', errors='ignore')
        if c == 200 and ("is required" in html_res.lower() or "error" in html_res.lower() or "invalid" in html_res.lower() or "please enter" in html_res.lower()):
            pass # Properly returned validation errors
        elif c == 302:
            self.add_finding("HIGH", "Validation", "Registration Accepted Empty Form Fields", "Submitting blank registration form was accepted and redirected (302)", url=f"{BASE_URL}/events/1/register")
        elif c == 500:
            self.add_finding("HIGH", "Logic", "Registration Throws 500 on Empty Input", "Submitting blank registration triggered a 500 Internal Server Error", url=f"{BASE_URL}/events/1/register")

        # Test 7.2: Registration with malicious XSS in student name
        xss_reg = {
            "studentName": "<script>alert('XSS')</script>",
            "studentEmail": "xss.test.student@campus.edu",
            "rollNumber": "23CSE999",
            "department": "Computer Science and Engineering",
            "yearOfStudy": "3",
            "_csrf": reg_csrf or ""
        }
        c, content, _ = self.request(pub_opener, f"{BASE_URL}/events/1/register", method="POST", data=xss_reg)
        # Check if roster or registration details render raw script
        roster_c, roster_content, _ = self.request(admin_opener, f"{BASE_URL}/admin/events/1/registrations")
        roster_html = roster_content.decode('utf-8', errors='ignore')
        if "<script>alert('XSS')</script>" in roster_html:
            self.add_finding("CRITICAL", "Security", "Stored Cross-Site Scripting (XSS) in Attendee Roster", "Raw script tag <script>alert('XSS')</script> was found unescaped in the attendee roster HTML", url=f"{BASE_URL}/admin/events/1/registrations", recommendation="Escape all student names with Thymeleaf th:text or HTML entity encoding.")

        # Test 7.3: Team Registration validation boundaries
        team_csrf = self.get_csrf(pub_opener, f"{BASE_URL}/events/2/register/team")
        invalid_team = {
            "teamName": "",
            "leaderName": "",
            "leaderEmail": "not-an-email",
            "members": "",
            "_csrf": team_csrf or ""
        }
        c, content, _ = self.request(pub_opener, f"{BASE_URL}/events/2/register/team", method="POST", data=invalid_team)
        if c == 500:
            self.add_finding("HIGH", "Logic", "Team Registration Throws 500 on Malformed Input", "Submitting invalid team registration data crashed with 500 error", url=f"{BASE_URL}/events/2/register/team")

        # Test 7.4: Feedback submission boundaries
        fb_csrf = self.get_csrf(pub_opener, f"{BASE_URL}/events/1/feedback")
        bad_feedback = {
            "studentEmail": "unregistered.fake@campus.edu",
            "rating": "10",  # Out of range (should be 1-5)
            "comments": "Test out of bounds rating",
            "_csrf": fb_csrf or ""
        }
        c, content, _ = self.request(pub_opener, f"{BASE_URL}/events/1/feedback", method="POST", data=bad_feedback)
        if c == 500:
            self.add_finding("MEDIUM", "Validation", "Feedback Submission Throws 500 on Out-of-Bounds Rating", "Submitting rating 10 crashed the feedback endpoint with 500 error", url=f"{BASE_URL}/events/1/feedback")

        # Test 7.5: Scanner door verification API edge cases
        scanner_csrf = self.get_csrf(admin_opener, f"{BASE_URL}/admin/scanner")
        
        # Test 7.5a: Invalid token
        c, content, _ = self.request(admin_opener, f"{BASE_URL}/admin/scanner/verify", method="POST", data={"qrCode": "FAKE-TOKEN-9999", "_csrf": scanner_csrf or ""})
        try:
            res_json = json.loads(content.decode('utf-8'))
            if res_json.get("success") is not False:
                self.add_finding("HIGH", "Logic", "Scanner Accepted Fake QR Code", "Fake token returned success=True in scanner API", url=f"{BASE_URL}/admin/scanner/verify")
        except:
            if c != 200:
                self.add_finding("MEDIUM", "Logic", f"Scanner API returned HTTP {c} on invalid token", "Scanner failed to return valid JSON on invalid QR token", url=f"{BASE_URL}/admin/scanner/verify")

        # Test 7.6: Live Q&A API boundary test
        qa_data = json.dumps({"studentName": "", "department": "", "content": ""}).encode('utf-8')
        c, content, _ = self.request(pub_opener, f"{BASE_URL}/api/events/1/questions", method="POST", data=qa_data, headers={"Content-Type": "application/json"})
        if c == 200:
            try:
                res_json = json.loads(content.decode('utf-8'))
                if "id" in res_json and not res_json.get("content"):
                    self.add_finding("MEDIUM", "Validation", "Blank Question Allowed in Live Q&A API", "The API accepted an empty question payload and stored it in the database", url=f"{BASE_URL}/api/events/1/questions")
            except:
                pass

        # Test 7.7: Superadmin vs Coordinator Privilege Escalation
        print("\n--- Testing RBAC & IDOR Boundaries ---")
        # Coordinator attempting to access superadmin analytics
        c, _, _ = self.request(cse_opener, f"{BASE_URL}/admin/analytics")
        if c == 200:
            self.add_finding("HIGH", "Security", "Coordinator Can Access SuperAdmin Analytics", "CSE Coordinator was allowed to access /admin/analytics (expected 403 Forbidden)", url=f"{BASE_URL}/admin/analytics")

        # Coordinator attempting to access notifications outbox
        c, _, _ = self.request(cse_opener, f"{BASE_URL}/admin/notifications")
        if c == 200:
            self.add_finding("HIGH", "Security", "Coordinator Can Access SuperAdmin Notification Outbox", "CSE Coordinator was allowed to access /admin/notifications (expected 403 Forbidden)", url=f"{BASE_URL}/admin/notifications")

        # Test 7.8: Certificate Verification case sensitivity and injection
        cert_res_code, cert_content, _ = self.request(pub_opener, f"{BASE_URL}/api/public/certificates/verify/cert-ai-2026-001")
        if cert_res_code != 200:
            self.add_finding("LOW", "UI/UX", "Certificate Verification is Case-Sensitive", "Searching for lowercase 'cert-ai-2026-001' returned HTTP 404 even though uppercase 'CERT-AI-2026-001' exists", url=f"{BASE_URL}/api/public/certificates/verify/cert-ai-2026-001")

        # Test 7.9: Security Headers Audit
        print("\n--- Testing Security Headers ---")
        _, _, headers = self.request(pub_opener, f"{BASE_URL}/")
        header_keys = [k.lower() for k in headers.keys()]
        if 'x-frame-options' not in header_keys and 'content-security-policy' not in header_keys:
            self.add_finding("MEDIUM", "Security", "Missing Clickjacking Protection (X-Frame-Options)", "Response headers lack X-Frame-Options or frame-ancestors in CSP", url=f"{BASE_URL}/")
        if 'x-content-type-options' not in header_keys:
            self.add_finding("LOW", "Security", "Missing X-Content-Type-Options: nosniff", "Response headers lack X-Content-Type-Options: nosniff", url=f"{BASE_URL}/")

        print("\n==========================================")
        print(f"SCAN COMPLETED: {self.pages_scanned} pages scanned, {self.endpoints_tested} requests sent.")
        print(f"Total Findings Detected: {len(self.findings)}")
        print("==========================================\n")

if __name__ == "__main__":
    scanner = DeepScanner()
    scanner.run_tests()
    with open("scratch/deep_scan_results.json", "w") as f:
        json.dump(scanner.findings, f, indent=2)

# SMART CAMPUS EVENT MANAGEMENT SYSTEM — FINAL EVALUATION & DEMO SCRIPT

## Executive Summary & Architecture
- **Tech Stack**: Spring Boot 4.1.1 (Java 23), Spring Data JPA, Hibernate, MySQL 8.0, Spring Security 6/7, Thymeleaf, Local Chart.js, ZXing QR Engine, iText PDF Engine, Html5Qrcode Scanner.
- **Test Suite**: **61 Green Tests (100% Pass Rate)** across 11 test classes covering RBAC security, certificates, team registrations, session timelines, feedback sentiment extraction, PDF passes, and QR generation.
- **Performance**: Sub-100ms page load speeds (98ms Events Directory, 15ms Visual Analytics) achieved through bulk aggregate SQL queries, HikariCP batch statement rewriting, and zero external CDN latency.

---

## Access Credentials & Role Governance Matrix

| Role | Username | Password | Permitted Department Scopes | Delete Privileges |
| :--- | :--- | :--- | :--- | :--- |
| **Super Admin** | `admin` | `admin123` | **All University Schools & Departments** | **YES (Exclusive)** |
| **CSE Coordinator** | `cse.coordinator` | `coord123` | Computer Science and Engineering | NO (Scoped to CSE) |
| **ECE Coordinator** | `ece.coordinator` | `coord123` | Electronics and Communication Engineering | NO (Scoped to ECE) |
| **Campus Coordinator** | `coordinator` | `coord123` | Computer Science and Engineering | NO (Scoped to CSE) |

---

## Comprehensive Evaluator Walkthrough (7 Final Review Features)

### Feature 1: Multi-Department Events & Sub-100ms Directory Performance
- **URL**: `http://localhost:8080/events`
- **What to Demo**:
  1. Open the directory — observe instant loading (under 100ms).
  2. Use instant search to filter by keyword (e.g. `Hackathon`), Department dropdown (`Computer Science`), or Event Category (`Workshop`).
  3. Notice real-time seat availability badges, star rating aggregates, and sold-out badges calculated via batch aggregation queries.

---

### Feature 2: Multi-Session Event Agenda & Interactive Chronological Timeline
- **URL**: `http://localhost:8080/events/1`
- **What to Demo**:
  1. Scroll down to **"⏱️ Event Agenda & Session Timeline"**.
  2. Review the structured track timeline:
     - 09:30 – 10:30: *Opening Keynote & Problem Statements* (Dr. S. Ramanathan, Auditorium Main Stage).
     - 10:30 – 13:00: *Sprint 1: Model Architecture & Prototyping* (Innovation Lab A).
     - 13:00 – 14:00: *Networking Lunch & Refreshments* (Dining Courtyard).
     - 14:00 – 17:00: *Sprint 2: Full-Stack Integration* (Innovation Lab B).
     - 17:00 – 19:00: *Final Project Demonstrations & Jury Review* (Auditorium Main Stage).
  3. Click **"📍 Campus Map & Room Locator"** to see the interactive vector map with venue pins and walking directions.

---

### Feature 3: Hackathon Team Registration (2–4 Members) with Individual Passes
- **URL**: `http://localhost:8080/events/1/register/team`
- **What to Demo**:
  1. Demonstrate the live dynamic member form: Click **"+ Add Team Member"** to dynamically add member fields (up to 4 members).
  2. Pre-seeded Team Pass Viewer: Visit `http://localhost:8080/registration/team-success/TEAM-BYTE-01`.
  3. Notice that each team member (*Rahul Sharma*, *Neha Patel*, *Vikramaditya Rao*) receives:
     - Distinct individual Token IDs (`SC-E1-RAHUL`, `SC-E1-NEHA`, `SC-E1-VIKRAM`).
     - Distinct scannable QR admission barcodes.
     - Individual PDF ticket download options.

---

### Feature 4: Public Certificate Verification Portal & Dynamic QR Verification
- **Portal URL**: `http://localhost:8080/verify/certificate`
- **Live Verified Certificate URL**: `http://localhost:8080/verify/certificate/CERT-AI-2026-001`
- **REST API Endpoint**: `http://localhost:8080/api/public/certificates/verify/CERT-AI-2026-001`
- **What to Demo**:
  1. On the verification portal, enter code: `CERT-AI-2026-001`.
  2. The cryptographic seal confirms authenticity:
     - Student: **Ananya Iyer**
     - Course: **Advanced Python & Scientific Computing Masterclass**
     - Department: **Computer Science and Engineering**
     - Dynamic embedded QR code pointing directly to this verification URL.
  3. Test an invalid code (e.g. `CERT-INVALID-999`) to show the tampered/not-found security alert.

---

### Feature 5: Multi-Channel Simulated Notification Outbox & WhatsApp 1-Click Share
- **Outbox URL**: `http://localhost:8080/admin/notifications` (Login as `admin` / `admin123`)
- **What to Demo**:
  1. Filter outbox tabs: **All Outbox**, **📧 Email**, **💬 WhatsApp**, **📱 SMS**.
  2. Click **"👁️ View Email"** on any notification to preview high-deliverability HTML templates.
  3. Student WhatsApp Pass Share:
     - Visit `http://localhost:8080/my-registrations?email=ananya.iyer@campus.edu`.
     - Click the green **"💬 WhatsApp"** button on any confirmed ticket to test the 1-click share pass link (`https://api.whatsapp.com/send?text=...`).

---

### Feature 6: Deterministic Feedback Keyword & Sentiment Analytics
- **Dashboard URL**: `http://localhost:8080/admin/analytics`
- **What to Demo**:
  1. **Sentiment Classification Breakdown**:
     - Visual proportional sentiment meter (Positive 4-5★ in emerald, Neutral 3★ in amber, Critical 1-2★ in red).
     - Overall campus satisfaction score (e.g. 4.8 / 5.0).
  2. **Extracted Recurring Feedback Themes**:
     - Deterministic keyword frequency pills extracted without external API dependency (`hands-on (3)`, `insightful (2)`, `practical (2)`, `speaker (2)`, `organization (2)`).

---

### Feature 7: Interactive Visual Analytics & Chart Suite
- **Dashboard URL**: `http://localhost:8080/admin/analytics`
- **What to Demo**:
  1. **Gate Turnout & Pass Status Breakdown**: Interactive doughnut chart (Checked-In, Confirmed, Waitlisted, Cancelled).
  2. **Attendee Department Distribution**: Bar chart comparing cross-faculty engagement.
  3. **Daily Registration Velocity**: Line chart with smooth tension curve tracking student demand.
  4. **Event Category Mix**: Curriculum distribution across Hackathons, Workshops, Seminars, and Competitions.
  5. Click **"🖨️ Print Analytics"** for an executive-ready snapshot.

---

### Feature 8: Door Gatekeeper QR Scanner
- **Scanner URL**: `http://localhost:8080/admin/scanner`
- **What to Demo**:
  1. Open the camera scanner. The Windows DirectShow lock fix prevents hardware conflicts.
  2. Type or scan token `SC-E1-RAHUL` in the manual token input and click **Verify & Check-In Pass**.
  3. Notice immediate green confirmation banner, instant conversion to `CHECKED_IN`, and real-time gatekeeper audit log.

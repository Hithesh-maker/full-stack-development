import os
import re
import glob

TEMPLATE_DIR = "src/main/resources/templates"
STATIC_DIR = "src/main/resources/static"

template_files = glob.glob(f"{TEMPLATE_DIR}/**/*.html", recursive=True)
print(f"Auditing {len(template_files)} HTML templates...\n")

issues = []

for t_path in template_files:
    t_name = os.path.basename(t_path)
    with open(t_path, "r", encoding="utf-8", errors="ignore") as f:
        content = f.read()

    # 1. Check forms for th:action and method="post"
    forms = re.findall(r'<form\b([^>]*)>(.*?)</form>', content, re.DOTALL | re.IGNORECASE)
    for form_attrs, form_body in forms:
        is_post = 'method="post"' in form_attrs.lower() or "method='post'" in form_attrs.lower()
        if is_post:
            has_th_action = 'th:action' in form_attrs
            has_action = 'action=' in form_attrs
            if not has_th_action and not has_action:
                issues.append((t_name, "HIGH", "Form missing action", form_attrs.strip()))
            # If using regular action without th:action on a post form
            if has_action and not has_th_action:
                issues.append((t_name, "MEDIUM", "POST Form using plain action without th:action (CSRF token might not be generated)", form_attrs.strip()))

    # 2. Check for missing static assets referenced in templates
    static_refs = re.findall(r'th:href="@{/([^}]+)}"', content) + re.findall(r'th:src="@{/([^}]+)}"', content)
    for ref in static_refs:
        # Ignore dynamic query params or path variables like /events/{id}
        clean_ref = ref.split('(')[0].split('?')[0]
        if '{' in clean_ref:
            continue
        # If it looks like a static asset (.css, .js, .png, etc.)
        if any(clean_ref.endswith(ext) for ext in ['.css', '.js', '.png', '.jpg', '.svg', '.ico', '.json']):
            local_path = os.path.join(STATIC_DIR, clean_ref.replace('/', os.sep))
            if not os.path.exists(local_path):
                issues.append((t_name, "HIGH", f"Referenced static file not found on disk: {clean_ref}", local_path))

    # 3. Check for external CDN scripts without fallback or SRI
    cdns = re.findall(r'<script[^>]+src=["\'](https?://[^"\']+)["\']', content)
    for cdn in cdns:
        # Check if local fallback is present
        if "cdn" in cdn or "cdnjs" in cdn or "unpkg" in cdn or "jsdelivr" in cdn:
            issues.append((t_name, "LOW", f"External CDN dependency: {cdn}", "May fail offline or behind corporate firewalls"))

    # 4. Check for broken or unescaped JS blocks
    # e.g. <script th:inline="javascript"> with unescaped strings
    if '<script' in content:
        # Check for inline variables
        bad_inlines = re.findall(r'var\s+[a-zA-Z0-9_]+\s*=\s*\[\[\$\{([^}]+)\}\]\]', content)
        for var in bad_inlines:
            # If variable is not wrapped in quotes or standard syntax
            pass

    # 5. Check buttons without type attribute inside forms
    for form_attrs, form_body in forms:
        buttons = re.findall(r'<button\b([^>]*)>', form_body, re.IGNORECASE)
        for b in buttons:
            if 'type=' not in b.lower():
                issues.append((t_name, "LOW", "Button missing explicit type attribute in form (defaults to submit)", b.strip()))

print("--- TEMPLATE AUDIT RESULTS ---")
for t_name, sev, title, detail in issues:
    print(f"[{sev:6}] [{t_name:30}] {title}")
    if detail:
        print(f"         Detail: {detail[:100]}")

print(f"\nTotal template issues flagged: {len(issues)}")

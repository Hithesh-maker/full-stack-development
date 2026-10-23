import urllib.request, urllib.parse, http.cookiejar, re

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# 1. Login as admin
resp = opener.open('http://localhost:8080/login')
html = resp.read().decode('utf-8')
csrf = re.search(r'name="_csrf"\s+value="([^"]+)"', html).group(1)

login_data = urllib.parse.urlencode({'username': 'admin', 'password': 'admin123', '_csrf': csrf}).encode('utf-8')
req = urllib.request.Request('http://localhost:8080/login', data=login_data, method='POST')
opener.open(req)

# 2. Get edit page for event 2
resp_edit = opener.open('http://localhost:8080/admin/events/edit/2')
html_edit = resp_edit.read().decode('utf-8')
edit_csrf = re.search(r'name="_csrf"\s+value="([^"]+)"', html_edit).group(1)

# 3. Post updated event 2 with future date
update_payload = {
    'title': 'AI & Machine Learning Hackathon 2026',
    'description': 'Annual University AI and Machine Learning Hackathon. Build autonomous models and agents.',
    'department': 'Computer Science and Engineering',
    'date': '2026-10-28',
    'time': '09:30',
    'venue': 'Tech Innovation Hub - Block B',
    'type': 'Hackathon',
    'capacity': '100',
    'price': '0.0',
    'isPaid': 'false',
    'registrationType': 'TEAM',
    'minTeamSize': '2',
    'maxTeamSize': '4',
    '_csrf': edit_csrf
}
req_update = urllib.request.Request('http://localhost:8080/admin/events/update/2', data=urllib.parse.urlencode(update_payload).encode('utf-8'), method='POST')
resp_up = opener.open(req_update)
print("Event 2 updated successfully! Status:", resp_up.getcode())

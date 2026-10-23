import urllib.request, urllib.parse, http.cookiejar, re

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

resp = opener.open('http://localhost:8080/login')
html = resp.read().decode('utf-8')
match = re.search(r'name="_csrf"\s+value="([^"]+)"', html)
token = match.group(1) if match else None
print('Form CSRF token:', token[:10] + '...')

data = urllib.parse.urlencode({'username': 'cse.coordinator', 'password': 'coord123', '_csrf': token}).encode('utf-8')
req = urllib.request.Request('http://localhost:8080/login', data=data, method='POST')
req.add_header('Content-Type', 'application/x-www-form-urlencoded')
resp2 = opener.open(req)
print('After login URL:', resp2.geturl())
print('After login Status:', resp2.getcode())

# Now test accessing ECE Event 4 edit
try:
    resp3 = opener.open('http://localhost:8080/admin/events/edit/4')
    print('ECE Edit Status:', resp3.getcode())
except urllib.error.HTTPError as e:
    print('ECE Edit Result:', e.code, e.reason)

# Test accessing CSE Event 1 edit
try:
    resp4 = opener.open('http://localhost:8080/admin/events/edit/1')
    print('CSE Edit Status:', resp4.getcode())
except urllib.error.HTTPError as e:
    print('CSE Edit Result:', e.code, e.reason)

# Test Delete Event 1
try:
    del_data = urllib.parse.urlencode({'_csrf': token}).encode('utf-8')
    req_del = urllib.request.Request('http://localhost:8080/admin/events/delete/1', data=del_data, method='POST')
    resp5 = opener.open(req_del)
    print('Delete Status:', resp5.getcode())
except urllib.error.HTTPError as e:
    print('Delete Result:', e.code, e.reason)

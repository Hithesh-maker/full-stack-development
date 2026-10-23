import urllib.request
import urllib.parse
import json
import re
import http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# Login
login_page = opener.open('http://localhost:8080/login').read().decode()
csrf = re.search(r'name="_csrf"\s+value="([^"]+)"', login_page).group(1)
login_data = urllib.parse.urlencode({'username': 'admin', 'password': 'admin123', '_csrf': csrf}).encode()
opener.open(urllib.request.Request('http://localhost:8080/login', data=login_data))

# Test checkin with fake token
checkin_data = json.dumps({'token': 'FAKE-TOKEN-9999'}).encode()
req = urllib.request.Request('http://localhost:8080/api/admin/checkin', data=checkin_data, method='POST')
req.add_header('Content-Type', 'application/json')
req.add_header('X-CSRF-TOKEN', csrf)

try:
    resp = opener.open(req)
    print('Status:', resp.getcode(), resp.read().decode())
except urllib.error.HTTPError as e:
    print('HTTPError:', e.code, e.read().decode())

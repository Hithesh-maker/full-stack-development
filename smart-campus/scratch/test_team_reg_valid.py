import urllib.request
import urllib.parse
import re
import http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# 1. Fetch form to get both session cookie and csrf value
resp = opener.open('http://localhost:8080/events/2/register/team')
html = resp.read().decode('utf-8')
csrf = re.search(r'name="_csrf"\s+value="([^"]+)"', html).group(1)

# Also check if XSRF cookie exists
xsrf_cookie = None
for c in cj:
    if c.name == 'XSRF-TOKEN':
        xsrf_cookie = c.value

payload = {
    "teamName": "Byte Knights",
    "leaderName": "Karan Verma",
    "leaderEmail": "karan.verma2026@campus.edu",
    "leaderDepartment": "Computer Science and Engineering",
    "members[0].name": "Siddharth Rao",
    "members[0].email": "siddharth.rao2026@campus.edu",
    "members[0].department": "Computer Science and Engineering",
    "eventId": "2",
    "_csrf": csrf
}

data = urllib.parse.urlencode(payload).encode()
req = urllib.request.Request('http://localhost:8080/events/2/register/team', data=data, method='POST')
if xsrf_cookie:
    req.add_header('X-XSRF-TOKEN', xsrf_cookie)
req.add_header('X-CSRF-TOKEN', csrf)

try:
    resp = opener.open(req)
    print("Success status:", resp.getcode())
    print("Final URL:", resp.geturl())
except urllib.error.HTTPError as e:
    print("HTTP Error:", e.code)
    print("Reason:", e.reason)
    print("Content:", e.read().decode('utf-8', errors='ignore')[:400])

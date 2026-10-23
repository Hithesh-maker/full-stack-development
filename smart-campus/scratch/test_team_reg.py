import urllib.request
import urllib.parse
import json
import re
import http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

# Get team register page
html = opener.open('http://localhost:8080/events/2/register/team').read().decode()
csrf_match = re.search(r'name="_csrf"\s+value="([^"]+)"', html)
print("CSRF Match:", csrf_match)
csrf = csrf_match.group(1) if csrf_match else ""
print("Extracted CSRF:", csrf)

team_payload = {
    "teamName": "Test Alpha Team",
    "leaderName": "Leader Test",
    "leaderEmail": "leader.test99@campus.edu",
    "leaderDepartment": "Computer Science and Engineering",
    "members[0].name": "Member One",
    "members[0].email": "member.one99@campus.edu",
    "members[0].department": "Computer Science and Engineering",
    "eventId": "2",
    "_csrf": csrf
}

data = urllib.parse.urlencode(team_payload).encode()
req = urllib.request.Request('http://localhost:8080/events/2/register/team', data=data, method='POST')

try:
    resp = opener.open(req)
    print("Response Status:", resp.getcode())
    print("URL redirected to:", resp.geturl())
except urllib.error.HTTPError as e:
    print("HTTP Error:", e.code)
    print("Body:", e.read().decode('utf-8', errors='ignore')[:500])

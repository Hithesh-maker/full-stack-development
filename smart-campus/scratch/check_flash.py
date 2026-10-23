import urllib.request
import re
import http.cookiejar

cj = http.cookiejar.CookieJar()
opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))

resp = opener.open('http://localhost:8080/events/2/register/team')
html = resp.read().decode('utf-8')

# Search for any error or message
alerts = re.findall(r'<div[^>]*class=["\'][^"\']*alert[^"\']*["\'][^>]*>(.*?)</div>', html, re.DOTALL)
print("Alerts found:", [a.strip() for a in alerts])

# Search for flash message or error in html
errs = re.findall(r'<div[^>]*class=["\'][^"\']*error[^"\']*["\'][^>]*>(.*?)</div>', html, re.DOTALL)
print("Errors found:", [e.strip() for e in errs])

import urllib.request
import re

html = urllib.request.urlopen('http://localhost:8080/events/2').read().decode()
title_m = re.search(r'<h1[^>]*>(.*?)</h1>', html, re.DOTALL)
print("Title:", title_m.group(1).strip() if title_m else "Not found")

# Check capacity and date
date_m = re.search(r'Date:.*?(\d{4}-\d{2}-\d{2})', html)
print("Date:", date_m.group(1) if date_m else "Not found")

seats_m = re.search(r'(\d+)\s+Seats Remaining', html)
print("Seats:", seats_m.group(1) if seats_m else "Not found")
print("Sold out badge:", "SOLD OUT" in html.upper())

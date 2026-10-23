import urllib.request, json

resp = urllib.request.urlopen('http://localhost:8080/api/events')
events = json.loads(resp.read().decode())
print(f"Total events: {len(events)}")
for e in events:
    print(f"ID: {e.get('id')}, Date: {e.get('date')}, RegType: {e.get('registrationType')}, Title: {e.get('title')}")

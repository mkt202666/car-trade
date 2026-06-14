import urllib.request
import json

BASE = "http://localhost:8081/api/v1/admin"

# Login
data = json.dumps({"username": "yuan2026@5d.com", "password": "admin123"}).encode()
req = urllib.request.Request(f"{BASE}/auth/login", data=data,
                            headers={"Content-Type": "application/json"})
resp = urllib.request.urlopen(req)
result = json.loads(resp.read().decode())
token = result["data"]["accessToken"]

# Test failing endpoints
failing = [
    ("GET", "/dashboard/stats/overview", "Dashboard Stats Overview"),
    ("GET", "/dashboard/trend?period=WEEK", "Dashboard Trend"),
    ("GET", "/dashboard/coupon-stats", "Coupon Stats"),
    ("GET", "/resources/configs", "Configs List"),
    ("GET", "/deposits?page=1&size=10", "Deposits List"),
]

for method, path, label in failing:
    url = f"{BASE}{path}"
    req = urllib.request.Request(url, method=method)
    req.add_header("Authorization", f"Bearer {token}")
    try:
        resp = urllib.request.urlopen(req)
        body = resp.read().decode()
        print(f"=== {label} [{resp.getcode()}] ===")
        print(json.dumps(json.loads(body), indent=2, ensure_ascii=False)[:500])
    except urllib.error.HTTPError as e:
        body = e.read().decode() if e.fp else ""
        print(f"=== {label} [{e.code}] ===")
        try:
            data = json.loads(body)
            print(json.dumps(data, indent=2, ensure_ascii=False)[:500])
        except:
            print(body[:500])
    print()

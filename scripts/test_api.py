import urllib.request
import json
import sys

BASE = "http://localhost:8081/api/v1/admin"

def login():
    data = json.dumps({"username": "yuan2026@5d.com", "password": "admin123"}).encode()
    req = urllib.request.Request(f"{BASE}/auth/login", data=data,
                                headers={"Content-Type": "application/json"})
    resp = urllib.request.urlopen(req)
    result = json.loads(resp.read().decode())
    token = result.get("data", {}).get("accessToken", "")
    print(f"Login: {'OK' if token else 'FAILED'}")
    return token

def test(method, path, token, label=""):
    url = f"{BASE}{path}"
    req = urllib.request.Request(url, method=method)
    if token:
        req.add_header("Authorization", f"Bearer {token}")
    try:
        resp = urllib.request.urlopen(req)
        code = resp.getcode()
        body = resp.read().decode()
        # Extract status from ApiResponse
        try:
            data = json.loads(body)
            api_code = data.get("code", "")
            status = "OK" if api_code in (0, 200) else f"CODE={api_code}"
        except:
            status = "OK"
        print(f"  OK   [{code}] {label or path}")
        return True
    except urllib.error.HTTPError as e:
        body = e.read().decode() if e.fp else ""
        try:
            data = json.loads(body)
            msg = data.get("message", "")[:80]
        except:
            msg = body[:80]
        print(f"  FAIL [{e.code}] {label or path} - {msg}")
        return False
    except Exception as e:
        print(f"  ERR       {label or path} - {e}")
        return False

token = login()
print(f"\n=== Testing API Endpoints ===\n")

endpoints = [
    ("GET", "/dashboard/kpi", "Dashboard KPI"),
    ("GET", "/dashboard/stats/overview", "Dashboard Stats Overview"),
    ("GET", "/dashboard/trend?period=WEEK", "Dashboard Trend"),
    ("GET", "/dashboard/car-distribution", "Car Distribution"),
    ("GET", "/dashboard/coupon-stats", "Coupon Stats"),
    ("GET", "/dashboard/approval-queue", "Approval Queue"),
    ("GET", "/dashboard/warnings", "Warnings"),
    ("GET", "/resources/banners?status=ALL", "Banners List"),
    ("GET", "/resources/configs", "Configs List"),
    ("GET", "/resources/configs/system.title", "Config by Key"),
    ("GET", "/shops?page=1&size=10", "Shops List"),
    ("GET", "/shop-reviews?status=PENDING", "Shop Reviews"),
    ("GET", "/car-library/brands", "Car Library Brands"),
    ("GET", "/cars?page=1&size=10", "Cars List"),
    ("GET", "/orders?page=1&size=10", "Orders List"),
    ("GET", "/disputes?page=1&size=10", "Disputes List"),
    ("GET", "/deposits/accounts?page=1&size=10", "Deposits Accounts"),
    ("GET", "/purchase-requests?page=1&size=10", "Purchase Requests"),
]

passed = 0
failed = 0
for method, path, label in endpoints:
    if test(method, path, token, label):
        passed += 1
    else:
        failed += 1

print(f"\n=== Result: {passed}/{passed+failed} passed, {failed} failed ===")

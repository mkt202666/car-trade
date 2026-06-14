import psycopg2

conn = psycopg2.connect(
    host='47.117.41.219', port=5432,
    database='new_car_trade', user='postgres', password='sxk#2025@'
)
cur = conn.cursor()
cur.execute("SET search_path = public")

# Check coupons table columns
cur.execute("""
    SELECT column_name, data_type
    FROM information_schema.columns
    WHERE table_name = 'coupons' AND table_schema = 'public'
    ORDER BY ordinal_position
""")
print("=== coupons table columns ===")
for row in cur.fetchall():
    print(f"  {row[0]:30s} {row[1]}")

# Check deposit_accounts columns
cur.execute("""
    SELECT column_name, data_type
    FROM information_schema.columns
    WHERE table_name = 'deposit_accounts' AND table_schema = 'public'
    ORDER BY ordinal_position
""")
print("\n=== deposit_accounts table columns ===")
for row in cur.fetchall():
    print(f"  {row[0]:30s} {row[1]}")

# Check AdminDepositController path
print("\nDone")
cur.close()
conn.close()

import json, urllib.request

CATEGORIES = ["laptops", "smartphones", "tablets", "mobile-accessories", "mens-watches"]
HEADERS = {
    "User-Agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                  "(KHTML, like Gecko) Chrome/124.0 Safari/537.36",
    "Accept": "application/json",
}

def esc(s):
    return str(s).replace("\\", "\\\\").replace("'", "''")

def fetch(url):
    req = urllib.request.Request(url, headers=HEADERS)
    with urllib.request.urlopen(req, timeout=20) as r:
        return json.load(r)

rows = []
for cat in CATEGORIES:
    try:
        data = fetch(f"https://dummyjson.com/products/category/{cat}")
        for p in data["products"]:
            rows.append(
                f"('{esc(p['title'])}','{cat}',{p['price']},"
                f"'{esc(p['description'])}','{esc(p['thumbnail'])}',{p.get('stock', 20)})"
            )
        print(f"OK {cat}: {len(data['products'])}")
    except Exception as e:
        print(f"ERROR {cat}: {e}")

if not rows:
    raise SystemExit("0 products fetched, seed.sql NOT written.")

sql = (
    "DELETE FROM products;\n"
    "INSERT INTO products (name, category, price, description, image_url, stock) VALUES\n"
    + ",\n".join(rows) + ";\n"
)

with open("seed.sql", "w", encoding="utf-8") as f:
    f.write(sql)

print(f"{len(rows)} products written to seed.sql")
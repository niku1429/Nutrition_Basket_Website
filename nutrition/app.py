from functools import wraps
from pathlib import Path
import sqlite3

from flask import Flask, flash, redirect, render_template, request, session, url_for
from werkzeug.security import check_password_hash, generate_password_hash


BASE_DIR = Path(__file__).resolve().parent.parent
DB_PATH = BASE_DIR / "nutrition.db"

app = Flask(
    __name__,
    template_folder=str(BASE_DIR / "templates"),
    static_folder=str(BASE_DIR / "static" / "static"),
)
app.config["SECRET_KEY"] = "change-this-secret-key-for-production"


def get_db():
    db = sqlite3.connect(DB_PATH)
    db.row_factory = sqlite3.Row
    return db


def column_exists(cur, table, column):
    cur.execute(f"PRAGMA table_info({table})")
    return column in [row["name"] for row in cur.fetchall()]


def init_db():
    with get_db() as db:
        cur = db.cursor()
        cur.execute(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                password_hash TEXT
            )
            """
        )

        if not column_exists(cur, "users", "password_hash"):
            cur.execute("ALTER TABLE users ADD COLUMN password_hash TEXT")

        cur.execute(
            """
            CREATE TABLE IF NOT EXISTS items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                type TEXT NOT NULL,
                calories INTEGER NOT NULL,
                vitamins TEXT NOT NULL,
                benefits TEXT NOT NULL,
                price REAL NOT NULL DEFAULT 0,
                image TEXT NOT NULL DEFAULT 'apple.jpg'
            )
            """
        )

        for column, definition in {
            "price": "REAL NOT NULL DEFAULT 0",
            "image": "TEXT NOT NULL DEFAULT 'apple.jpg'",
        }.items():
            if not column_exists(cur, "items", column):
                cur.execute(f"ALTER TABLE items ADD COLUMN {column} {definition}")

        cur.execute("SELECT COUNT(*) AS count FROM items")
        if cur.fetchone()["count"] < 12:
            cur.execute("DELETE FROM items")
            cur.executemany(
                """
                INSERT INTO items
                    (name, type, calories, vitamins, benefits, price, image)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """,
                SAMPLE_ITEMS,
            )

        db.commit()


SAMPLE_ITEMS = [
    ("Apple", "Fruit", 52, "Vitamin C, Fiber", "Supports heart health and digestion.", 35, "apple.jpg"),
    ("Banana", "Fruit", 96, "Vitamin B6, Potassium", "Boosts energy and supports muscle function.", 18, "banana.jpg"),
    ("Orange", "Fruit", 47, "Vitamin C, Folate", "Improves immunity and skin health.", 28, "orange.jpg"),
    ("Papaya", "Fruit", 43, "Vitamin A, Vitamin C", "Aids digestion and supports eye health.", 45, "papaya.jpg"),
    ("Mango", "Fruit", 60, "Vitamin A, Vitamin C", "Provides antioxidants and natural energy.", 55, "mango.jpg"),
    ("Strawberry", "Fruit", 33, "Vitamin C, Manganese", "Supports immunity with low calories.", 80, "strawberry.jpg"),
    ("Carrot", "Vegetable", 41, "Vitamin A, Biotin", "Helps vision, skin, and immune health.", 22, "carrot.jpg"),
    ("Spinach", "Vegetable", 23, "Iron, Vitamin K", "Supports blood health and strong bones.", 20, "spinach.jpg"),
    ("Broccoli", "Vegetable", 34, "Vitamin C, Vitamin K", "Supports detoxification and immunity.", 48, "broccoli.jpg"),
    ("Tomato", "Vegetable", 18, "Vitamin C, Lycopene", "Promotes heart and skin health.", 24, "tomato.jpg"),
    ("Cucumber", "Vegetable", 16, "Vitamin K, Water", "Hydrating and refreshing for weight control.", 18, "Cucumber.jpg"),
    ("Cauliflower", "Vegetable", 25, "Vitamin C, Choline", "Low calorie food for balanced meals.", 34, "cauliflower.jpg"),
    ("Almonds", "Nut", 579, "Vitamin E, Magnesium", "Supports brain, heart, and skin health.", 220, "almonds.jpg"),
    ("Walnuts", "Nut", 654, "Omega-3, Copper", "Supports brain health and healthy fats.", 260, "walnuts.jpg"),
    ("Cashew", "Nut", 553, "Magnesium, Zinc", "Supports energy and mineral intake.", 210, "cashew.jpg"),
    ("Peanuts", "Nut", 567, "Protein, Niacin", "Affordable protein for energy and satiety.", 120, "peanuts.jpg"),
    ("Pistachios", "Nut", 562, "Vitamin B6, Potassium", "Heart-friendly snack with protein.", 280, "pistachios.jpg"),
    ("Hazelnuts", "Nut", 628, "Vitamin E, Manganese", "Rich in antioxidants and healthy fats.", 300, "hazelnuts.jpg"),
]


def login_required(view):
    @wraps(view)
    def wrapped_view(*args, **kwargs):
        if "user_id" not in session:
            flash("Please login to continue.", "warning")
            return redirect(url_for("login"))
        return view(*args, **kwargs)

    return wrapped_view


def get_cart_items():
    cart = session.get("cart", {})
    if not isinstance(cart, dict) or not cart:
        return [], 0

    item_ids = list(cart.keys())
    placeholders = ",".join("?" for _ in item_ids)
    with get_db() as db:
        rows = db.execute(
            f"SELECT * FROM items WHERE id IN ({placeholders})",
            item_ids,
        ).fetchall()

    items = []
    total = 0
    for item in rows:
        qty = int(cart.get(str(item["id"]), 0))
        if qty <= 0:
            continue
        subtotal = item["price"] * qty
        total += subtotal
        items.append({"item": item, "qty": qty, "subtotal": subtotal})
    return items, total


@app.context_processor
def inject_globals():
    cart = session.get("cart", {})
    count = sum(cart.values()) if isinstance(cart, dict) else 0
    return {"cart_count": count}


@app.route("/")
def home():
    return render_template("home.html")


@app.route("/register", methods=["GET", "POST"])
def register():
    if request.method == "POST":
        name = request.form.get("name", "").strip()
        email = request.form.get("email", "").strip().lower()
        password = request.form.get("password", "")

        if not name or not email or not password:
            flash("All fields are required.", "danger")
            return render_template("register.html")
        if len(password) < 6:
            flash("Password must be at least 6 characters.", "danger")
            return render_template("register.html")

        try:
            with get_db() as db:
                db.execute(
                    "INSERT INTO users (name, email, password_hash) VALUES (?, ?, ?)",
                    (name, email, generate_password_hash(password)),
                )
                db.commit()
        except sqlite3.IntegrityError:
            flash("An account with this email already exists.", "danger")
            return render_template("register.html")

        flash("Registration successful. Please login.", "success")
        return redirect(url_for("login"))

    return render_template("register.html")


@app.route("/login", methods=["GET", "POST"])
def login():
    if request.method == "POST":
        email = request.form.get("email", "").strip().lower()
        password = request.form.get("password", "")

        if not email or not password:
            flash("Email and password are required.", "danger")
            return render_template("login.html")

        with get_db() as db:
            user = db.execute("SELECT * FROM users WHERE email = ?", (email,)).fetchone()

        valid_hash = user and user["password_hash"] and check_password_hash(user["password_hash"], password)
        valid_legacy = False
        if user:
            try:
                valid_legacy = "password" in user.keys() and user["password"] == password
            except (KeyError, IndexError):
                valid_legacy = False

        if not user or not (valid_hash or valid_legacy):
            flash("Invalid email or password.", "danger")
            return render_template("login.html")

        session.clear()
        session["user_id"] = user["id"]
        session["user_name"] = user["name"]
        flash(f"Welcome back, {user['name']}!", "success")
        return redirect(url_for("dashboard"))

    return render_template("login.html")


@app.route("/logout")
def logout():
    session.clear()
    flash("You have been logged out.", "success")
    return redirect(url_for("home"))


@app.route("/dashboard")
@login_required
def dashboard():
    with get_db() as db:
        counts = {
            row["type"]: row["count"]
            for row in db.execute("SELECT type, COUNT(*) AS count FROM items GROUP BY type").fetchall()
        }
    return render_template("dashboard.html", counts=counts)


@app.route("/items")
@login_required
def items():
    return catalog()


@app.route("/fruits")
@login_required
def fruits():
    return catalog("Fruit", "Fresh Fruits")


@app.route("/vegetables")
@login_required
def vegetables():
    return catalog("Vegetable", "Green Vegetables")


@app.route("/nuts")
@login_required
def nuts():
    return catalog("Nut", "Healthy Nuts")


def catalog(item_type=None, title="Nutrition Catalog"):
    with get_db() as db:
        if item_type:
            rows = db.execute("SELECT * FROM items WHERE type = ? ORDER BY name", (item_type,)).fetchall()
        else:
            rows = db.execute("SELECT * FROM items ORDER BY type, name").fetchall()
    return render_template("catalog.html", items=rows, title=title)


@app.route("/item/<int:item_id>")
@login_required
def item_detail(item_id):
    with get_db() as db:
        item = db.execute("SELECT * FROM items WHERE id = ?", (item_id,)).fetchone()
    if item is None:
        flash("Food item not found.", "danger")
        return redirect(url_for("items"))
    return render_template("detail.html", item=item)


@app.route("/bmi", methods=["GET", "POST"])
@login_required
def bmi():
    result = None
    if request.method == "POST":
        try:
            weight = float(request.form.get("weight", 0))
            height_cm = float(request.form.get("height", 0))
            if weight <= 0 or height_cm <= 0:
                raise ValueError
        except ValueError:
            flash("Enter a valid positive weight and height.", "danger")
            return render_template("bmi.html", result=result)

        height_m = height_cm / 100
        bmi_value = round(weight / (height_m * height_m), 1)
        if bmi_value < 18.5:
            category = "Underweight"
            suggestion = "Add calorie-dense foods like bananas, mangoes, potatoes, almonds, cashews, and smoothies."
        elif bmi_value < 25:
            category = "Normal"
            suggestion = "Maintain balance with seasonal fruits, leafy vegetables, nuts, hydration, and regular activity."
        elif bmi_value < 30:
            category = "Overweight"
            suggestion = "Prefer high-fiber fruits, cucumber, spinach, broccoli, soups, and controlled nut portions."
        else:
            category = "Obese"
            suggestion = "Focus on portion control, vegetables, low-calorie fruits, daily movement, and medical guidance."

        result = {"bmi": bmi_value, "category": category, "suggestion": suggestion}

    return render_template("bmi.html", result=result)


@app.route("/diet", methods=["GET", "POST"])
@login_required
def diet():
    plans = {
        "weight_loss": {
            "title": "Weight Loss Diet",
            "subtitle": "Light, filling meals rich in fiber and micronutrients.",
            "meals": ["Apple with cinnamon", "Spinach cucumber salad", "Broccoli soup", "Papaya bowl", "Green tea with 6 almonds"],
        },
        "balanced": {
            "title": "Balanced Diet",
            "subtitle": "Daily wellness plan with fruit, vegetables, and healthy fats.",
            "meals": ["Banana and almonds", "Mixed fruit bowl", "Vegetable sabzi", "Roasted peanuts", "Carrot cucumber salad"],
        },
        "weight_gain": {
            "title": "Weight Gain Diet",
            "subtitle": "Nutrient-dense foods for healthy calorie surplus.",
            "meals": ["Mango banana smoothie", "Potato broccoli bowl", "Cashews and walnuts", "Peanut chaat", "Fruit yogurt bowl"],
        },
        "energy": {
            "title": "Energy Boost Diet",
            "subtitle": "Quick natural energy for busy days.",
            "meals": ["Banana with peanuts", "Orange and walnuts", "Tomato spinach wrap", "Pistachio snack", "Papaya smoothie"],
        },
    }
    selected = request.form.get("goal", "balanced") if request.method == "POST" else "balanced"
    return render_template("diet.html", plans=plans, selected=selected, plan=plans[selected])


@app.route("/add-to-cart/<int:item_id>")
@login_required
def add_to_cart(item_id):
    with get_db() as db:
        item = db.execute("SELECT id FROM items WHERE id = ?", (item_id,)).fetchone()
    if item is None:
        flash("Food item not found.", "danger")
        return redirect(url_for("items"))

    cart = session.get("cart", {})
    if not isinstance(cart, dict):
        cart = {}
    cart[str(item_id)] = cart.get(str(item_id), 0) + 1
    session["cart"] = cart
    flash("Item added to cart.", "success")
    return redirect(request.referrer or url_for("cart"))


@app.route("/cart")
@login_required
def cart():
    items, total = get_cart_items()
    return render_template("cart.html", items=items, total=total)


@app.route("/update-qty/<int:item_id>/<action>")
@login_required
def update_qty(item_id, action):
    cart = session.get("cart", {})
    if isinstance(cart, dict) and str(item_id) in cart:
        if action == "inc":
            cart[str(item_id)] += 1
        elif action == "dec":
            cart[str(item_id)] -= 1
            if cart[str(item_id)] <= 0:
                cart.pop(str(item_id), None)
    session["cart"] = cart
    return redirect(url_for("cart"))


@app.route("/remove/<int:item_id>")
@login_required
def remove_item(item_id):
    cart = session.get("cart", {})
    if isinstance(cart, dict):
        cart.pop(str(item_id), None)
    session["cart"] = cart
    flash("Item removed from cart.", "success")
    return redirect(url_for("cart"))


@app.route("/checkout", methods=["GET", "POST"])
@login_required
def checkout():
    items, total = get_cart_items()
    if not items:
        flash("Your cart is empty.", "warning")
        return redirect(url_for("cart"))

    if request.method == "POST":
        name = request.form.get("name", "").strip()
        address = request.form.get("address", "").strip()
        mobile = request.form.get("mobile", "").strip()
        payment = request.form.get("payment", "").strip()
        if not name or not address or not mobile or not payment:
            flash("Please complete all checkout fields.", "danger")
            return render_template("checkout.html", items=items, total=total)

        order = {
            "name": name,
            "address": address,
            "mobile": mobile,
            "payment": payment,
            "payment_status": "Paid" if payment in {"UPI", "Card"} else "Pending: Cash on Delivery",
        }
        session.pop("cart", None)
        return render_template("order_confirm.html", items=items, total=total, order=order)

    return render_template("checkout.html", items=items, total=total)


@app.route("/order/<int:item_id>")
@login_required
def order(item_id):
    return redirect(url_for("add_to_cart", item_id=item_id))


@app.route("/cancel-order")
@login_required
def cancel_order():
    flash("Order cancelled.", "warning")
    return redirect(url_for("dashboard"))


if __name__ == "__main__":
    init_db()
    app.run(debug=True)

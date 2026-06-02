from flask import Flask, render_template, request, redirect, session
import sqlite3

app = Flask(__name__)
app.secret_key = "nutrition_secret"

def get_db():
    return sqlite3.connect("nutrition.db")

def init_db():
    db = get_db()
    cur = db.cursor()

    cur.execute("""
    CREATE TABLE IF NOT EXISTS users(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        email TEXT,
        password TEXT
    )
    """)

    cur.execute("""
    CREATE TABLE IF NOT EXISTS items(
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT,
        type TEXT,
        calories TEXT,
        vitamins TEXT,
        benefits TEXT
    )
    """)

    cur.execute("SELECT * FROM items")
    if cur.fetchone() is None:
        cur.executemany("""
        INSERT INTO items(name,type,calories,vitamins,benefits)
        VALUES(?,?,?,?,?)
        """,[
            ('Apple','Fruit','52','Vitamin C','Good for heart'),
            ('Banana','Fruit','96','Vitamin B6','Boosts energy'),
            ('Carrot','Vegetable','41','Vitamin A','Good for eyes'),
            ('Spinach','Vegetable','23','Iron','Improves blood')
        ])
    db.commit()
    db.close()

@app.route("/")
def home():
    return render_template("home.html")

@app.route("/login", methods=["GET","POST"])
def login():
    if request.method == "POST":
        email = request.form["email"]
        password = request.form["password"]

        db = get_db()
        cur = db.cursor()
        cur.execute("SELECT * FROM users WHERE email=? AND password=?", (email,password))
        user = cur.fetchone()

        if user:
            session["user"] = user[1]
            return redirect("/dashboard")
    return render_template("login.html")

@app.route("/register", methods=["GET","POST"])
def register():
    if request.method == "POST":
        name = request.form["name"]
        email = request.form["email"]
        password = request.form["password"]

        db = get_db()
        cur = db.cursor()
        cur.execute("INSERT INTO users VALUES(NULL,?,?,?)", (name,email,password))
        db.commit()
        return redirect("/login")
    return render_template("register.html")

@app.route("/dashboard")
def dashboard():
    if "user" not in session:
        return redirect("/login")
    return render_template("dashboard.html", user=session["user"])

@app.route("/items")
def items():
    if "user" not in session:
        return redirect("/login")
    db = get_db()
    cur = db.cursor()
    cur.execute("SELECT * FROM items")
    data = cur.fetchall()
    return render_template("items.html", items=data)

@app.route("/logout")
def logout():
    session.clear()
    return redirect("/")

@app.route("/fruits")
def fruits():
    if "user" not in session:
        return redirect("/login")

    db = get_db()
    cur = db.cursor()
    cur.execute("SELECT * FROM items WHERE type='Fruit'")
    fruits = cur.fetchall()
    return render_template("list.html", items=fruits, title="Fruits")


@app.route("/vegetables")
def vegetables():
    if "user" not in session:
        return redirect("/login")

    db = get_db()
    cur = db.cursor()
    cur.execute("SELECT * FROM items WHERE type='Vegetable'")
    vegetables = cur.fetchall()
    return render_template("list.html", items=vegetables, title="Vegetables")

@app.route("/nuts")
def nuts():
    if "user" not in session:
        return redirect("/login")

    db = get_db()
    cur = db.cursor()
    cur.execute("SELECT * FROM items WHERE type='Nut'")
    nuts = cur.fetchall()
    return render_template("list.html", items=nuts, title="Nuts")


@app.route("/item/<int:item_id>")
def item_detail(item_id):
    if "user" not in session:
        return redirect("/login")

    db = get_db()
    cur = db.cursor()
    cur.execute("SELECT * FROM items WHERE id=?", (item_id,))
    item = cur.fetchone()
    return render_template("detail.html", item=item)

@app.route("/diet", methods=["GET", "POST"])
def diet():
    if "user" not in session:
        return redirect("/login")

    plan = None

    if request.method == "POST":
        goal = request.form["goal"]

        if goal == "weight_loss":
            plan = {
                "title": "Weight Loss Diet",
                "details": [
                    "Morning: Apple + 5 Almonds",
                    "Breakfast: Papaya + Spinach salad",
                    "Lunch: Boiled vegetables",
                    "Evening: Green tea + Peanuts",
                    "Dinner: Vegetable soup"
                ]
            }

        elif goal == "balanced":
            plan = {
                "title": "Balanced Diet",
                "details": [
                    "Morning: Banana + Almonds",
                    "Breakfast: Mixed fruit bowl",
                    "Lunch: Mixed vegetable sabzi",
                    "Evening: Roasted nuts",
                    "Dinner: Vegetable soup + salad"
                ]
            }

        elif goal == "energy":
            plan = {
                "title": "Energy Boost Diet",
                "details": [
                    "Morning: Banana + Dates + Nuts",
                    "Breakfast: Fruit smoothie",
                    "Lunch: Potato + Broccoli",
                    "Evening: Cashews + Pistachios",
                    "Dinner: Spinach soup + Walnuts"
                ]
            }

    return render_template("diet.html", plan=plan)

@app.route("/bmi", methods=["GET", "POST"])
def bmi():
    if "user" not in session:
        return redirect("/login")

    bmi_value = None
    category = None
    diet = None

    if request.method == "POST":
        weight = float(request.form["weight"])
        height = float(request.form["height"]) / 100   # cm to meter

        bmi_value = round(weight / (height * height), 2)

        if bmi_value < 18.5:
            category = "Underweight"
            diet = [
                "Banana, Mango, Dates",
                "Potato, Broccoli",
                "Almonds, Cashews, Peanuts",
                "Fruit smoothies"
            ]

        elif bmi_value < 25:
            category = "Normal Weight"
            diet = [
                "Apple, Papaya, Orange",
                "Carrot, Spinach, Cucumber",
                "Almonds, Walnuts",
                "Balanced fruit & vegetable diet"
            ]

        else:
            category = "Overweight"
            diet = [
                "Apple, Guava, Papaya",
                "Spinach, Broccoli, Cucumber",
                "Limited nuts (Almonds)",
                "Vegetable soup & salads"
            ]

    return render_template(
        "bmi.html",
        bmi=bmi_value,
        category=category,
        diet=diet
    )

@app.route("/order/<int:item_id>", methods=["GET", "POST"])
def order(item_id):
    if "user" not in session:
        return redirect("/login")

    db = get_db()
    cur = db.cursor()

    cur.execute("SELECT name, price FROM items WHERE id=?", (item_id,))
    item = cur.fetchone()

    if request.method == "POST":
        name = request.form["name"]
        address = request.form["address"]
        pincode = request.form["pincode"]
        mobile = request.form["mobile"]
        payment = request.form["payment"]

        # 🔥 Payment status logic
        if payment == "QR Code (UPI)":
            payment_status = "Paid"
        else:
            payment_status = "Pending (Cash on Delivery)"

        return render_template(
            "order_confirm.html",
            item=item,
            name=name,
            payment=payment,
            payment_status=payment_status
        )

    return render_template("order_form.html", item=item)

@app.route("/add-to-cart/<int:item_id>")
def add_to_cart(item_id):

    cart = session.get("cart")

    # 🔥 VERY IMPORTANT FIX
    if not isinstance(cart, dict):
        cart = {}

    item_id = str(item_id)

    if item_id in cart:
        cart[item_id] += 1
    else:
        cart[item_id] = 1

    session["cart"] = cart
    return redirect("/cart")



@app.route("/cart")
def cart():
    cart = session.get("cart", {})
    if not cart:
        return render_template("cart.html", items=[], total=0)

    db = get_db()
    cur = db.cursor()

    placeholders = ",".join("?" * len(cart))
    cur.execute(
        f"SELECT id, name, price FROM items WHERE id IN ({placeholders})",
        list(cart.keys())
    )
    rows = cur.fetchall()

    items = []
    total = 0
    for r in rows:
        qty = cart[str(r[0])]
        subtotal = r[2] * qty
        total += subtotal
        items.append((r[0], r[1], r[2], qty, subtotal))

    return render_template("cart.html", items=items, total=total)


@app.route("/update-qty/<int:item_id>/<action>")
def update_qty(item_id, action):
    cart = session.get("cart", {})
    item_id = str(item_id)

    if item_id in cart:
        if action == "inc":
            cart[item_id] += 1
        elif action == "dec":
            cart[item_id] -= 1
            if cart[item_id] <= 0:
                cart.pop(item_id)

    session["cart"] = cart
    return redirect("/cart")

@app.route("/remove/<int:item_id>")
def remove_item(item_id):
    cart = session.get("cart", {})
    cart.pop(str(item_id), None)
    session["cart"] = cart
    return redirect("/cart")

@app.route("/checkout", methods=["GET", "POST"])
def checkout():
    cart = session.get("cart", {})

    if not cart:
        return redirect("/cart")

    db = get_db()
    cur = db.cursor()

    # ✅ IMPORTANT FIX: dict → list of ids
    item_ids = list(cart.keys())
    placeholders = ",".join("?" * len(item_ids))

    cur.execute(
        f"SELECT id, name, price FROM items WHERE id IN ({placeholders})",
        item_ids
    )

    rows = cur.fetchall()

    items = []
    total = 0

    for r in rows:
        qty = cart[str(r[0])]
        subtotal = r[2] * qty
        total += subtotal

        items.append({
            "id": r[0],
            "name": r[1],
            "price": r[2],
            "qty": qty,
            "subtotal": subtotal
        })

    if request.method == "POST":
        payment = request.form.get("payment")
        payment_status = "Paid" if payment == "UPI" else "Cash on Delivery"

        # clear cart
        session.pop("cart", None)

        return render_template(
            "order_confirm.html",
            items=items,
            total=total,
            payment=payment,
            payment_status=payment_status
        )

    return render_template(
        "order_form.html",
        items=items,
        total=total
    )


@app.route("/cancel-order")
def cancel_order():
    session.pop("last_order", None)
    return redirect("/dashboard")

if __name__ == "__main__":
    init_db()
    app.run(debug=True)


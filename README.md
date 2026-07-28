# Mini E-commerce Project (Spring Boot + React + PostgreSQL)

Ye ek chhota lekin **fully functional** e-commerce project hai jisme:
- Products list karna, add/edit/delete karna (**sirf logged-in Admin**, JWT-based login)
- Cart me product add karna, quantity change karna, remove karna
- Checkout karke Order banana — shipping details ke saath, stock automatically kam hota hai

## Admin Login
- Default credentials: **username: `admin`**, **password: `admin123`**
- Pehli baar backend start hone par ye user automatically ban jaata hai (`DataSeeder.java`)
- Change karne ke liye `application.properties` me `admin.default.username` / `admin.default.password` update karke database drop karein (ya seeder logic khud edit karein)
- Login hone ke baad JWT token milta hai jo browser me (`localStorage`) store hota hai aur har admin request ke saath automatically bhejta hai

## Customer Login (Shopping)
- Ab **koi bhi shopping (products dekhna, search, cart, checkout) sirf logged-in Customer hi kar sakta hai**
- Naya user `/register` page se account banata hai (naam, email, phone, password) — register hote hi turant login bhi ho jaata hai
- Purana user `/customer-login` se login karta hai
- Har customer ka apna alag cart aur order history hota hai — koi customer doosre ka cart/order nahi dekh sakta
- Admin login (`/login`) aur Customer login (`/customer-login`) **do alag systems** hain — Admin sirf products manage karta hai, Customer sirf khareedari

## ⚠️ Database ko fresh karna zaroori hai
Chunki `orders` table me naya `customer_id` column (NOT NULL) aur ek naya `customers` table add hua hai, agar aapne pehle se project chalaya hua hai, to database ko fresh karna hoga:
```sql
\c ecommerce_db
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
```
Fir backend restart karo (`mvn spring-boot:run`) — Hibernate sab naye structure ke saath bana dega.

## Project Structure
```
ecommerce-project/
├── backend/    -> Spring Boot REST API (Java + PostgreSQL)
└── frontend/   -> React app (UI)
```

## Architecture samajhne ke liye (flow)
```
Browser (React, localhost:3000)
        │  axios (HTTP calls)
        ▼
Controller  (REST endpoint receive karta hai, e.g. ProductController)
        ▼
Service     (business logic, e.g. ProductService)
        ▼
Repository  (Spring Data JPA — database queries generate karta hai)
        ▼
PostgreSQL  (actual data yahan store hota hai)
```
Isi crime pattern ko Cart aur Order ke liye bhi follow kiya gaya hai —
`CartController -> CartService -> CartItemRepository`, same for Orders.

---

## Step 1: PostgreSQL setup

1. PostgreSQL install hona chahiye (agar nahi hai to postgresql.org se le lein).
2. Terminal me:
```bash
psql -U postgres
CREATE DATABASE ecommerce_db;
\q
```
3. `backend/src/main/resources/application.properties` me apna username/password daal dein (default `postgres` / `postgres` set hai).

## Step 2: Backend run karna
```bash
cd backend
mvn spring-boot:run
```
- Pehli baar run hone par Hibernate khud tables bana dega (`ddl-auto=update`)
- `data.sql` se 6 sample products insert ho jaayenge
- Backend chalega: **http://localhost:8080**

Quick test (browser me ya curl se):
```
GET http://localhost:8080/api/products
```

## Step 3: Frontend run karna
```bash
cd frontend
npm install
npm start
```
- React app khulega: **http://localhost:3000**
- Ye automatically backend (localhost:8080) se baat karega

---

## API Endpoints (reference)

| Method | Endpoint                        | Kaam                                     | Auth needed? |
|--------|----------------------------------|--------------------------------------------|--------------|
| POST   | /api/auth/login                 | Admin login, JWT token milega              | No           |
| POST   | /api/customers/register         | Naya customer account banao (auto-login)   | No           |
| POST   | /api/customers/login             | Customer login                             | No           |
| GET    | /api/products                   | Saare products / search                    | **Yes (any login)** |
| GET    | /api/products/{id}               | Ek product                                 | **Yes (any login)** |
| POST   | /api/products                   | Naya product banao                         | **Yes (Admin)** |
| PUT    | /api/products/{id}               | Product update karo                        | **Yes (Admin)** |
| DELETE | /api/products/{id}               | Product delete karo                        | **Yes (Admin)** |
| GET    | /api/cart                       | Apna cart                                  | **Yes (Customer)** |
| POST   | /api/cart/add                   | Cart me add karo                           | **Yes (Customer)** |
| PUT    | /api/cart/update/{cartItemId}    | Quantity update karo                       | **Yes (Customer)** |
| DELETE | /api/cart/remove/{cartItemId}    | Cart se hatao                              | **Yes (Customer)** |
| DELETE | /api/cart/clear                 | Pura cart khaali karo                      | **Yes (Customer)** |
| POST   | /api/orders/checkout            | Checkout karo (body: customerName, customerEmail, customerPhone, shippingAddress) | **Yes (Customer)** |
| GET    | /api/orders                     | Apni orders (Customer) / saari orders (Admin) | **Yes** |

Protected endpoints call karne ke liye header chahiye: `Authorization: Bearer <token>` — jo login/register se milta hai. Frontend ye khud handle karta hai (`src/api.js` ka axios interceptor).

## Code kis order me padhein (samajhne ke liye)

1. `backend/.../model/` — Entities (Product, CartItem, Order, OrderItem) — ye data ka shape define karte hain
2. `backend/.../repository/` — Database access (Spring Data JPA magic)
3. `backend/.../service/` — Business logic (add to cart, checkout ka calculation)
4. `backend/.../controller/` — REST endpoints (yahan se request enter hoti hai)
5. `frontend/src/api.js` — Saare backend calls ek jagah
6. `frontend/src/pages/` — Har page ka UI + logic (ShopPage, CartPage, OrdersPage, AdminPage)

Har file me Hinglish comments hain jo bata rahe hain "kyun" likha gaya hai,
sirf "kya" likha hai wo nahi.

## Common issues
- **CORS error**: Backend ka `CorsConfig.java` sirf `http://localhost:3000` allow karta hai — agar React kisi aur port pe chal raha hai to wahan update kar lein.
- **Connection refused (DB)**: PostgreSQL chal raha hai ya nahi check karein (`pg_isready` ya services me dekh lein).
- **Port already in use**: `server.port` (backend) ya React ka port (`PORT=3001 npm start`) change kar lein.

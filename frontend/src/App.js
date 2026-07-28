import React, { useEffect, useState } from 'react';
import { BrowserRouter, Routes, Route, Link, Navigate, useLocation, useNavigate } from 'react-router-dom';
import ShopPage from './pages/ShopPage';
import CartPage from './pages/CartPage';
import AdminPage from './pages/AdminPage';
import OrdersPage from './pages/OrdersPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import CustomerLoginPage from './pages/CustomerLoginPage';
import ProtectedRoute from './components/ProtectedRoute';
import ProductDetailPage from './pages/ProductDetailPage';
import AdminDashboardPage from './pages/AdminDashboardPage';
import WishlistPage from './pages/WishlistPage';
import { getCart } from './api';
import { isLoggedIn, isAdmin, isCustomer, clearAuth, getUsername } from './auth';
import './App.css';
import AdminOrdersPage from './pages/AdminOrdersPage';
import ProfilePage from './pages/ProfilePage';

// Navbar apna alag component hai taaki cart item count har page par turant dikhe
function Navbar({ cartCount, refreshCartCount }) {
  const location = useLocation();
  const navigate = useNavigate();
  const loggedIn = isLoggedIn();
  const admin = isAdmin();
  const customer = isCustomer();

  useEffect(() => {
    if (customer) refreshCartCount();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [location]);

  const handleLogout = () => {
    clearAuth();
    navigate('/');
  };

  return (
    <nav className="navbar">
      <Link to="/" className="brand">🛍️ Ravi Enterprises</Link>
      <div className="nav-links">
        {customer && (
          <>
            <Link to="/">Shop</Link>
            <Link to="/cart">Cart ({cartCount})</Link>
			<Link to="/wishlist">Wishlist</Link>
            <Link to="/orders">Orders</Link>
			<Link to="/profile">Profile</Link>
          </>
        )}
        {admin && (
			<>
				<Link to="/admin">Admin</Link>
				<Link to="/admin/dashboard">Dashboard</Link>
				<Link to="/admin/orders">Manage Orders</Link>
			</>
			)}

        {loggedIn ? (
          <>
            <span className="admin-badge">👤 {getUsername()}</span>
            <button className="logout-btn" onClick={handleLogout}>Logout</button>
          </>
        ) : (
          <>
            <Link to="/customer-login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
}

function App() {
  const [cartCount, setCartCount] = useState(0);

  const refreshCartCount = () => {
    if (!isCustomer()) {
      setCartCount(0);
      return;
    }
    getCart()
      .then((res) => {
        const totalQty = res.data.reduce((sum, item) => sum + item.quantity, 0);
        setCartCount(totalQty);
      })
      .catch(() => setCartCount(0));
  };

  useEffect(() => {
    refreshCartCount();
  }, []);

  return (
    <BrowserRouter>
      <Navbar cartCount={cartCount} refreshCartCount={refreshCartCount} />
      <main className="container">
        <Routes>
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/customer-login" element={<CustomerLoginPage />} />
          <Route path="/login" element={<LoginPage />} />

          {/* Shop, Cart, Orders — sirf logged-in Customer ke liye */}
          <Route
            path="/"
            element={
              <ProtectedRoute role="CUSTOMER">
                <ShopPage onCartChange={refreshCartCount} />
              </ProtectedRoute>
            }
          />
          <Route
            path="/cart"
            element={
              <ProtectedRoute role="CUSTOMER">
                <CartPage onCartChange={refreshCartCount} />
              </ProtectedRoute>
            }
          />
		  
		  <Route
			path="/wishlist"
			element={
				<ProtectedRoute role="CUSTOMER">
				<WishlistPage onCartChange={refreshCartCount} />
				</ProtectedRoute>
			}
			/>

		  <Route
			path="/product/:id"
			element={
				<ProtectedRoute role="CUSTOMER">
				<ProductDetailPage onCartChange={refreshCartCount} />
				</ProtectedRoute>
			}
			/>
          <Route
            path="/orders"
            element={
              <ProtectedRoute role="CUSTOMER">
                <OrdersPage />
              </ProtectedRoute>
            }
          />
		  
			<Route
			path="/profile"
			element={
				<ProtectedRoute role="CUSTOMER">
				<ProfilePage />
				</ProtectedRoute>
			}
			/>

          {/* Admin panel — sirf logged-in Admin ke liye */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute role="ADMIN">
                <AdminPage />
              </ProtectedRoute>
            }
          />
		  
		  <Route
			path="/admin/dashboard"
			element={
				<ProtectedRoute role="ADMIN">
				<AdminDashboardPage />
				</ProtectedRoute>
			}
			/>
		  
			<Route
			path="/admin/orders"
			element={
				<ProtectedRoute role="ADMIN">
				<AdminOrdersPage />
				</ProtectedRoute>
			}
			/>
		
          <Route path="*" element={<Navigate to="/" replace />} />
       		  
        </Routes>
      </main>
    </BrowserRouter>
  );
}

export default App;

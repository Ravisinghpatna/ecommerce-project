import axios from 'axios';
import { getToken, clearAuth } from './auth';
export const getDashboard = () => api.get('/admin/dashboard');
export const getWishlist = () => api.get('/wishlist');
export const addToWishlist = (productId) => api.post('/wishlist/add', { productId });
export const removeFromWishlist = (productId) => api.delete(`/wishlist/remove/${productId}`);
export const updateOrderStatus = (orderId, status) => api.put(`/orders/${orderId}/status`, { status });
export const getProfile = () => api.get('/customers/me');
export const updateProfile = (name, phone) => api.put('/customers/me', { name, phone });
export const cancelOrder = (orderId) => api.put(`/orders/${orderId}/cancel`);
export const shipOrder = (orderId, courierPartner) => api.put(`/orders/${orderId}/ship`, { courierPartner });

// Backend ka base URL. Saare API calls yahi se guzarte hain,
// taaki agar port/URL badle to sirf ek jagah change karni pade.
const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

// Request interceptor: har request bhejne se PEHLE ye function chalta hai.
// Agar admin login ho chuka hai (token maujood hai), to use Authorization
// header me daal dete hain — taaki protected admin endpoints (add/edit/
// delete product) authenticate ho sakein.
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response interceptor: agar backend 401 (Unauthorized) bhejta hai — matlab
// token expire ho gaya ya galat hai — to purana token clear karke user ko
// login page pe bhej do.
//
// IMPORTANT: login/register endpoints khud 401 bhejte hain jab email/password
// GALAT ho — ye "session expired" nahi hai, ye sirf ek normal validation
// error hai. Agar hum in par bhi redirect kar dein, to poora page reload ho
// jaata hai aur error message dikhne se pehle hi gayab ho jaata hai
// (yahi bug ho raha tha). Isliye in auth endpoints ko yahan se exclude karte hain
// — inka error seedha calling code (LoginPage/CustomerLoginPage) ke .catch() me jaayega.
const AUTH_ENDPOINTS = ['/auth/login', '/customers/login', '/customers/register'];
 
api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response && error.response.status;
    const requestUrl = error.config?.url || '';
    const isAuthEndpoint = AUTH_ENDPOINTS.some((path) => requestUrl.includes(path));
 
    if ((status === 401 || status === 403) && !isAuthEndpoint) {
      clearAuth();
      window.location.href = '/customer-login';
    }
    return Promise.reject(error);
  }
);

// ---------------- Products ----------------
export const getProducts = () => api.get('/products');
export const getProduct = (id) => api.get(`/products/${id}`);
export const createProduct = (product) => api.post('/products', product);
export const updateProduct = (id, product) => api.put(`/products/${id}`, product);
export const deleteProduct = (id) => api.delete(`/products/${id}`);

// ---------------- Cart ----------------
export const getCart = () => api.get('/cart');
export const addToCart = (productId, quantity) =>
  api.post('/cart/add', { productId, quantity });
export const updateCartQuantity = (cartItemId, quantity) =>
  api.put(`/cart/update/${cartItemId}`, { quantity });
export const removeFromCart = (cartItemId) => api.delete(`/cart/remove/${cartItemId}`);
export const clearCart = () => api.delete('/cart/clear');

// ---------------- Orders ----------------
export const checkout = (customerDetails) => api.post('/orders/checkout', customerDetails);
export const getOrders = () => api.get('/orders');

// ---------------- Admin Auth ----------------
export const login = (username, password) => api.post('/auth/login', { username, password });

// ---------------- Customer Auth ----------------
export const registerCustomer = (name, email, password, phone) =>
  api.post('/customers/register', { name, email, password, phone });
export const loginCustomer = (email, password) =>
  api.post('/customers/login', { email, password });
  
  
  // Invoice PDF hai — isliye responseType 'blob' zaroori hai (JSON nahi, raw binary file)
export const downloadInvoice = (orderId) => {
  return api.get(`/orders/${orderId}/invoice`, { responseType: 'blob' }).then((res) => {
    const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }));
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', `invoice-order-${orderId}.pdf`);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  });
};

export default api;

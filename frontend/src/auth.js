// Auth helper — token ko localStorage me store karte hain taaki page
// refresh hone par bhi login state bana rahe.
 
const TOKEN_KEY = 'ecommerce_token';
const USERNAME_KEY = 'ecommerce_username';
const ROLE_KEY = 'ecommerce_role';
const USER_ID_KEY = 'ecommerce_user_id';
 
export function saveAuth(token, username, role, userId) {
  localStorage.setItem(TOKEN_KEY, token);
  localStorage.setItem(USERNAME_KEY, username);
  localStorage.setItem(ROLE_KEY, role);
  localStorage.setItem(USER_ID_KEY, userId);
}
 
export function getToken() {
  return localStorage.getItem(TOKEN_KEY);
}
 
export function getUsername() {
  return localStorage.getItem(USERNAME_KEY);
}
 
export function getRole() {
  return localStorage.getItem(ROLE_KEY);
}
 
export function clearAuth() {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USERNAME_KEY);
  localStorage.removeItem(ROLE_KEY);
  localStorage.removeItem(USER_ID_KEY);
}
 
function decodeTokenPayload(token) {
  try {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join('')
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}
 
export function isLoggedIn() {
  const token = getToken();
  if (!token) return false;
 
  const payload = decodeTokenPayload(token);
  if (!payload || !payload.exp) {
    clearAuth();
    return false;
  }
 
  const isExpired = Date.now() >= payload.exp * 1000;
  if (isExpired) {
    clearAuth();
    return false;
  }
 
  return true;
}
 
export function isAdmin() {
  return isLoggedIn() && getRole() === 'ADMIN';
}
 
export function isCustomer() {
  return isLoggedIn() && getRole() === 'CUSTOMER';
}
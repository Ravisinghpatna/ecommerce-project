import React from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { isLoggedIn, getRole } from '../auth';

/**
 * Ye component ek "gatekeeper" hai.
 * - role prop na do -> koi bhi logged-in user (Admin ya Customer) andar ja sakta hai
 * - role="ADMIN" do -> sirf Admin andar ja sakta hai, warna /login (admin login) bhej do
 * - role="CUSTOMER" do -> sirf Customer andar ja sakta hai, warna /customer-login bhej do
 */
function ProtectedRoute({ children, role }) {
  const location = useLocation();

  if (!isLoggedIn()) {
    const redirectTo = role === 'ADMIN' ? '/login' : '/customer-login';
    return <Navigate to={redirectTo} state={{ from: location.pathname }} replace />;
  }

  if (role && getRole() !== role) {
    
    return <Navigate to="/" replace />;
  }

  return children;
}

export default ProtectedRoute;

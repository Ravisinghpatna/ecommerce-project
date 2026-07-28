import React, { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { loginCustomer } from '../api';
import { saveAuth } from '../auth';

function CustomerLoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    loginCustomer(email, password)
      .then((res) => {
        saveAuth(res.data.token, res.data.username, res.data.role, res.data.id);
        const redirectTo = location.state?.from || '/';
        navigate(redirectTo);
      })
      .catch((err) => {
        setError(err.response?.data?.message || 'Login failed');
      })
      .finally(() => setLoading(false));
  };

  return (
    <div className="login-page">
      <form className="login-form" onSubmit={handleSubmit}>
        <h2>Login</h2>
        {location.state?.registered && (
          <div className="success-box">Account created successfully! Please log in.</div>
        )}
        {error && <div className="error-box">{error}</div>}
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </button>
        <p className="hint">
         "Register as a new user!" ✅ <Link to="/register">Create Account</Link>
        </p>
      </form>
    </div>
  );
}

export default CustomerLoginPage;

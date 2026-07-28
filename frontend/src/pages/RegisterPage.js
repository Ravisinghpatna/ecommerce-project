import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { registerCustomer } from '../api';

function RegisterPage() {
  const [form, setForm] = useState({ name: '', email: '', password: '', phone: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    registerCustomer(form.name, form.email, form.password, form.phone)
      .then(() => {
   
        navigate('/customer-login', { state: { registered: true } });
      })
      .catch((err) => {
        setError(err.response?.data?.message || 'Registration failed.');
      })
      .finally(() => setLoading(false));
  };

  return (
    <div className="login-page">
      <form className="login-form" onSubmit={handleSubmit}>
        <h2>Create Account</h2>
        {error && <div className="error-box">{error}</div>}
        <input
          name="name"
          placeholder="Full Name"
          value={form.name}
          onChange={handleChange}
          required
        />
        <input
          name="email"
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={handleChange}
          required
        />
        <input
          name="phone"
          placeholder="Phone Number"
          value={form.phone}
          onChange={handleChange}
          required
        />
        <input
          name="password"
          type="password"
          placeholder="Password (min 6 characters)"
          value={form.password}
          onChange={handleChange}
          minLength={6}
          required
        />
        <button type="submit" disabled={loading}>
          {loading ? 'Creating account...' : 'Register'}
        </button>
        <p className="hint">
          Already have an account? <Link to="/customer-login">Sign in</Link>
        </p>
      </form>
    </div>
  );
}

export default RegisterPage;

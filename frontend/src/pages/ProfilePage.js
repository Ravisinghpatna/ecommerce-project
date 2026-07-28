import React, { useEffect, useState } from 'react';
import { getProfile, updateProfile } from '../api';
 
function ProfilePage() {
  const [profile, setProfile] = useState(null);
  const [form, setForm] = useState({ name: '', phone: '' });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
 
  useEffect(() => {
    getProfile()
      .then((res) => {
        setProfile(res.data);
        setForm({ name: res.data.name, phone: res.data.phone });
      })
      .finally(() => setLoading(false));
  }, []);
 
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };
 
  const handleSubmit = (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
 
    updateProfile(form.name, form.phone)
      .then((res) => {
        setProfile(res.data);
        setSuccess('Profile updated successfully.✅');
        setTimeout(() => setSuccess(''), 2000);
      })
      .catch((err) => setError(err.response?.data?.message || 'Update fail ho gaya'));
  };
 
  if (loading) return <p>Loading profile...</p>;
  if (!profile) return <p>Profile load nahi hui.</p>;
 
  return (
    <div>
      <h1>My Profile</h1>
 
      <form className="login-form" style={{ margin: 0 }} onSubmit={handleSubmit}>
        {error && <div className="error-box">{error}</div>}
        {success && <div className="success-box">{success}</div>}
 
        {/* Email fixed rakha hai — ye login se juda hai, isliye edit nahi karne dete */}
        <label>Email</label>
        <input value={profile.email} disabled />
 
        <label>Name</label>
        <input name="name" value={form.name} onChange={handleChange} required />
 
        <label>Phone</label>
        <input name="phone" value={form.phone} onChange={handleChange} required />
 
        <button type="submit">Save Changes</button>
      </form>
    </div>
  );
}
 
export default ProfilePage;
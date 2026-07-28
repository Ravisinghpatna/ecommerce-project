import React, { useEffect, useState } from 'react';
import { getDashboard } from '../api';
 
function AdminDashboardPage() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
 
  useEffect(() => {
    getDashboard()
      .then((res) => setData(res.data))
      .catch((err) => setError(err.response?.data?.message || 'Dashboard load nahi hua'))
      .finally(() => setLoading(false));
  }, []);
 
  if (loading) return <p>Loading dashboard...</p>;
  if (error) return <div className="error-box">{error}</div>;
 
  return (
    <div>
      <h1>Dashboard</h1>
 
      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-label">Total Sales</span>
          <span className="stat-value">₹{data.totalSales.toFixed(2)}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Total Orders</span>
          <span className="stat-value">{data.totalOrders}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Total Products</span>
          <span className="stat-value">{data.totalProducts}</span>
        </div>
        <div className="stat-card">
          <span className="stat-label">Total Customers</span>
          <span className="stat-value">{data.totalCustomers}</span>
        </div>
      </div>
 
      <h2>Top Selling Products</h2>
      {data.topProducts.length === 0 ? (
        <p>Abhi tak koi order nahi hua.</p>
      ) : (
        <table className="cart-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Quantity Sold</th>
            </tr>
          </thead>
          <tbody>
            {data.topProducts.map((p) => (
              <tr key={p.productName}>
                <td>{p.productName}</td>
                <td>{p.totalQuantitySold}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
 
export default AdminDashboardPage;
import React, { useEffect, useState } from 'react';
//import { getOrders, updateOrderStatus } from '../api';
import { getOrders, updateOrderStatus, downloadInvoice } from '../api';
 
const STATUSES = ['PLACED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];
 
function AdminOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
 
  useEffect(() => {
    fetchOrders();
  }, []);
 
 const fetchOrders = () => {
  setLoading(true);

  getOrders()
    .then((res) => {
      const sortedOrders = [...res.data].sort(
        (a, b) =>
          new Date(b.orderDate).getTime() -
          new Date(a.orderDate).getTime()
      );

      setOrders(sortedOrders);
    })
    .finally(() => setLoading(false));
};
 
  const handleStatusChange = (orderId, newStatus) => {
    setError('');
    updateOrderStatus(orderId, newStatus)
      .then(() => fetchOrders())
      .catch((err) => setError(err.response?.data?.message || 'Failed to update the status.'));
  };
  
		const handleDownloadInvoice = (orderId) => {
		downloadInvoice(orderId).catch(() => setError('Failed to download the Invoice'));
		};
 
 
  if (loading) return <p>Loading orders...</p>;
  // "ALL" chuna hai to sab dikhao, warna sirf usi status wale orders
	const filteredOrders = orders
  .filter((order) => statusFilter === 'ALL' || order.status === statusFilter)
  .filter((order) => {
    if (!fromDate && !toDate) return true;
    const orderDate = new Date(order.orderDate);
 
    if (fromDate && orderDate < new Date(fromDate)) {
      return false;
    }
    if (toDate) {
      const endOfToDate = new Date(toDate);
      endOfToDate.setHours(23, 59, 59, 999);
      if (orderDate > endOfToDate) {
        return false;
      }
    }
    return true;
  });
 
	const handleClearFilters = () => {
	setStatusFilter('ALL');
	setFromDate('');
	setToDate('');
	};
 
  return (
    <div>
      <h1>Manage Orders</h1>
      {error && <div className="error-box">{error}</div>}
	
  <div className="admin-filter-row">
  <label>Filter by status: </label>
  <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
    <option value="ALL">All ({orders.length})</option>
    {STATUSES.map((s) => (
      <option key={s} value={s}>
        {s} ({orders.filter((o) => o.status === s).length})
      </option>
    ))}
  </select>
  
		<label>From: </label>
		<input type="date" value={fromDate} onChange={(e) => setFromDate(e.target.value)} />
		
		<label>To: </label>
		<input type="date" value={toDate} onChange={(e) => setToDate(e.target.value)} />
		
	{(statusFilter !== 'ALL' || fromDate || toDate) && (
	<button className="clear-filters-btn" onClick={handleClearFilters}>Clear Filters</button>
	)}
	</div>
 
			{filteredOrders.length === 0 ? (
				<p>Is status ke koi orders nahi hain.</p>
				) : (
		filteredOrders.map((order) => (
          <div key={order.id} className="order-card">
            <div className="order-header">
              <strong>Order #{order.id}</strong>
              <span>{new Date(order.orderDate).toLocaleString()}</span>
            </div>
            <p className="order-shipping">
              {order.customerName} · {order.customerPhone} · {order.shippingAddress}
            </p>
			{order.trackingId && (
				<p className="tracking-id">📦 Tracking ID: <strong>{order.trackingId}</strong></p>
			)}
			
            <ul>
              {order.items.map((item) => (
                <li key={item.id}>
                  {item.product.name} × {item.quantity} — ₹{(item.priceAtPurchase * item.quantity).toFixed(2)}
                </li>
              ))}
            </ul>
            <div className="order-status-row">
              <label>Status: </label>
              <select
                value={order.status}
                onChange={(e) => handleStatusChange(order.id, e.target.value)}
              >
                {STATUSES.map((s) => (
                  <option key={s} value={s}>{s}</option>
                ))}
              </select>
            </div>
            <p className="order-total">Total: ₹{order.totalAmount.toFixed(2)}</p>
			<button className="invoice-btn" onClick={() => handleDownloadInvoice(order.id)}>
			📄 Download Invoice
			</button>
			
          </div>
        ))
      )}
    </div>
  );
}
 
export default AdminOrdersPage;
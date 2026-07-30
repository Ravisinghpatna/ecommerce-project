import React, { useEffect, useState } from 'react';
import { getOrders, updateOrderStatus, shipOrder, downloadInvoice } from '../api';

// Filter dropdown ke liye — saare possible statuses
const STATUSES = ['PLACED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];

// Ek baar order SHIP ho jaaye, uske baad admin sirf inhi statuses me manually badal sakta hai
// (PLACED aur SHIPPED yahan nahi hain — PLACED se wapas nahi ja sakte, SHIPPED ke liye alag "Ship Order" flow hai)
const UPDATABLE_STATUSES = ['OUT_FOR_DELIVERY', 'DELIVERED', 'CANCELLED'];

const COURIERS = [
  { value: 'BLUE_DART', label: 'Blue Dart' },
  { value: 'DELHIVERY', label: 'Delhivery' },
  { value: 'SPEED_POST', label: 'Speed Post' },
  { value: 'DTDC', label: 'DTDC' },
];

function AdminOrdersPage() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [fromDate, setFromDate] = useState('');
  const [toDate, setToDate] = useState('');
  // Har order ke liye "abhi kaunsa courier dropdown me chuna hai" — default Blue Dart
  const [courierSelections, setCourierSelections] = useState({});

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
      .catch((err) => setError(err.response?.data?.message || 'Fail to update the Status'));
  };

  const handleShip = (orderId) => {
    const courierPartner = courierSelections[orderId] || COURIERS[0].value;
    setError('');
    shipOrder(orderId, courierPartner)
      .then(() => fetchOrders())
      .catch((err) => setError(err.response?.data?.message || 'Shipping failed'));
  };

  const handleDownloadInvoice = (orderId) => {
    downloadInvoice(orderId).catch(() => setError('Failed to Invoice download'));
  };

  if (loading) return <p>Loading orders...</p>;

  // Pehle status se filter karo, fir date range se (dono ek saath kaam karte hain)
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
        <p>No order for this status.</p>
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

            {order.courierPartner && (
              <p className="tracking-id">
                🚚 Courier: <strong>{order.courierPartner.replace('_', ' ')}</strong> — Tracking ID:{' '}
                <strong>{order.trackingId}</strong>
              </p>
            )}

            {/* Courier ke detailed messages, agar koi hain */}
            {order.trackingUpdates && order.trackingUpdates.length > 0 && (
              <ul className="tracking-updates-list">
                {order.trackingUpdates.map((u) => (
                  <li key={u.id}>
                    {new Date(u.timestamp).toLocaleString()} — {u.message}
                  </li>
                ))}
              </ul>
            )}

            <ul>
              {order.items.map((item) => (
                <li key={item.id}>
                  {item.product.name} × {item.quantity} — ₹{(item.priceAtPurchase * item.quantity).toFixed(2)}
                </li>
              ))}
            </ul>

            {/* PLACED order ke liye: courier chuno aur "Ship" karo */}
            {order.status === 'PLACED' && (
              <div className="ship-row">
                <select
                  value={courierSelections[order.id] || COURIERS[0].value}
                  onChange={(e) =>
                    setCourierSelections({ ...courierSelections, [order.id]: e.target.value })
                  }
                >
                  {COURIERS.map((c) => (
                    <option key={c.value} value={c.value}>{c.label}</option>
                  ))}
                </select>
                <button className="ship-btn" onClick={() => handleShip(order.id)}>
                  🚚 Ship Order
                </button>
              </div>
            )}

            {/* Ek baar SHIP ho jaaye (aur abhi Delivered/Cancelled na hua ho), tabhi ye dikhega */}
            {(order.status === 'SHIPPED' || order.status === 'OUT_FOR_DELIVERY') && (
              <div className="order-status-row">
                <label>Status: </label>
                <select
                  value={order.status}
                  onChange={(e) => handleStatusChange(order.id, e.target.value)}
                >
                  <option value={order.status} disabled hidden>{order.status}</option>
                  {UPDATABLE_STATUSES.map((s) => (
                    <option key={s} value={s}>{s}</option>
                  ))}
                </select>
              </div>
            )}

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
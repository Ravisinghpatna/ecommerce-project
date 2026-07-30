import React, { useEffect, useState } from 'react';
//import { getOrders, cancelOrder } from '../api';
import { getOrders, cancelOrder, downloadInvoice } from '../api';
 

const STEPS = [
  { key: 'PLACED', label: 'Order Confirmed', icon: '📦' },
  { key: 'SHIPPED', label: 'Shipped', icon: '🚚' },
  { key: 'OUT_FOR_DELIVERY', label: 'Out for Delivery', icon: '🛵' },
  { key: 'DELIVERED', label: 'Delivered', icon: '✅' },
];
 
const CURRENT_STATUS_MESSAGE = {
  PLACED: 'Your order has been confirmed and is being prepared.',
  SHIPPED: 'Your order has been shipped and is on its way.',
  OUT_FOR_DELIVERY: 'Your order is out for delivery — arriving today!',
  DELIVERED: 'Your order has been delivered. Enjoy!',
  CANCELLED: 'This order was cancelled.',
};
 
function formatDate(dateStr) {
  if (!dateStr) return '';
  return new Date(dateStr).toLocaleDateString('en-IN', {
    day: 'numeric',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  });
}
 
function StatusTimeline({ order }) {
  const [showDetails, setShowDetails] = useState(false);

  if (order.status === 'CANCELLED') {
    return <p className="order-cancelled">❌ Order Cancelled</p>;
  }

  const currentIndex = STEPS.findIndex((s) => s.key === order.status);

  // Har step ke liye uska actual date/time statusHistory se dhoondo
  const getStepDate = (stepKey) => {
    const entry = (order.statusHistory || []).find((h) => h.status === stepKey);
    return entry ? formatDate(entry.timestamp) : null;
  };

  return (
    <div>
      {order.status !== 'DELIVERED' && order.estimatedDelivery && (
        <p className="arriving-by">
          📅 Arriving by <strong>{formatDate(order.estimatedDelivery)}</strong>
        </p>
      )}
      {order.trackingId && (
        <p className="tracking-id">
          📦 {order.courierPartner && `${order.courierPartner.replace('_', ' ')} — `}
          Tracking ID: <strong>{order.trackingId}</strong>
        </p>
      )}
      <p className="current-status-message">{CURRENT_STATUS_MESSAGE[order.status]}</p>

      <div className="status-timeline">
        {STEPS.map((step, index) => (
          <React.Fragment key={step.key}>
            <div className={`timeline-step ${index <= currentIndex ? 'done' : ''}`}>
              <div className="timeline-dot">{index <= currentIndex ? step.icon : ''}</div>
              <span>{step.label}</span>
              {index <= currentIndex && getStepDate(step.key) && (
                <span className="timeline-date">{getStepDate(step.key)}</span>
              )}
            </div>
            {index < STEPS.length - 1 && (
              <div className={`timeline-line ${index < currentIndex ? 'done' : ''}`} />
            )}
          </React.Fragment>
        ))}
      </div>

      {/* Courier ke detailed, real-tracking-page-jaisa messages — har ghante naye aate rehte hain */}
      {order.trackingUpdates && order.trackingUpdates.length > 0 && (
        <div className="tracking-details">
          <button className="tracking-toggle-btn" onClick={() => setShowDetails(!showDetails)}>
            {showDetails ? 'Hide' : 'Show'} tracking details ({order.trackingUpdates.length})
          </button>
          {showDetails && (
            <ul className="tracking-updates-list">
              {[...order.trackingUpdates].reverse().map((u) => (
                <li key={u.id}>
                  <span className="tracking-update-time">{formatDate(u.timestamp)}</span> — {u.message}
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}
 
function OrdersPage() {
  const [orders, setOrders] = useState([]);
  const [error, setError] = useState('');
 
useEffect(() => {
  fetchOrders();
}, []);
 
const fetchOrders = () => {
  getOrders().then((res) => {
    const sortedOrders = [...res.data].sort(
      (a, b) => new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime()
    );
    setOrders(sortedOrders);
  });
};

const handleCancel = (orderId) => {
  if (!window.confirm('Are you sure you want to cancel the order?')) {
    return;
  }
  setError('');
  cancelOrder(orderId)
    .then(() => fetchOrders())
    .catch((err) => setError(err.response?.data?.message || 'Failed to cancel the order'));
};

const handleDownloadInvoice = (orderId) => {
  downloadInvoice(orderId).catch(() => setError('Failed to download the Invoice'));
};
 
  if (orders.length === 0) {
    return (
      <div>
        <h1>Orders</h1>
        <p>No order yet.</p>
      </div>
    );
  }
 
  return (
    <div>
      <h1>Orders</h1>
	  {error && <div className="error-box">{error}</div>}
      {orders.map((order) => (
        <div key={order.id} className="order-card">
          <div className="order-header">
            <strong>Order #{order.id}</strong>
            <span>{formatDate(order.orderDate)}</span>
          </div>
          <p className="order-shipping">
            {order.customerName} · {order.customerPhone} · {order.shippingAddress}
          </p>
 
          <StatusTimeline order={order} />
 
          <ul>
           {order.items.map((item) => (
  <li key={item.id}>
    <div>
      {item.product.name} × {item.quantity} — ₹
      {(item.priceAtPurchase * item.quantity).toFixed(2)}
    </div>

    <div
      style={{
        color:
          item.product.stock > 0 && item.product.stock < 10
            ? 'red'
            : item.product.stock === 0
            ? 'gray'
            : 'green',
        fontWeight:
          item.product.stock > 0 && item.product.stock < 10
            ? 'bold'
            : 'normal',
      }}
    >
      {item.product.stock > 10
        ? 'In Stock'
        : item.product.stock > 0
        ? `Only ${item.product.stock} left`
        : 'Out of Stock'}
    </div>
  </li>
))}
          </ul>
          <p className="order-total">Total: ₹{order.totalAmount.toFixed(2)}</p>
		  
			<div className="order-actions">
			<button className="invoice-btn" onClick={() => handleDownloadInvoice(order.id)}>
				📄 Download Invoice
			</button>
			{order.status === 'PLACED' && (
			<button className="cancel-order-btn" onClick={() => handleCancel(order.id)}>
				Cancel Order
			</button>
			)}
			
			</div>
			</div>
      ))}
    </div>
  );
}
 
export default OrdersPage;
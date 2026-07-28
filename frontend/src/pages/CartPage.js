import React, { useEffect, useState } from 'react';
import { getCart, updateCartQuantity, removeFromCart, checkout } from '../api';
import { useNavigate } from 'react-router-dom';

const emptyDetails = { customerName: '', customerEmail: '', customerPhone: '', shippingAddress: '' };

function CartPage({ onCartChange }) {
  const [items, setItems] = useState([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [showCheckoutForm, setShowCheckoutForm] = useState(false);
  const [details, setDetails] = useState(emptyDetails);
  const navigate = useNavigate();

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = () => {
    getCart().then((res) => setItems(res.data));
  };

  const handleQuantityChange = (cartItemId, newQty) => {
    if (newQty < 1) return;
    updateCartQuantity(cartItemId, newQty).then(() => {
      fetchCart();
      onCartChange();
    });
  };

  const handleRemove = (cartItemId) => {
    removeFromCart(cartItemId).then(() => {
      fetchCart();
      onCartChange();
    });
  };

  const handleDetailsChange = (e) => {
    setDetails({ ...details, [e.target.name]: e.target.value });
  };

  // "Checkout" button pehli baar dabane par form dikhao, details bhar kar
  // "Place Order" dabane par hi actual API call ho.
  const handlePlaceOrder = (e) => {
    e.preventDefault();
    setError('');
    checkout(details)
      .then(() => {
        setMessage('Order placed successfully! 🎉');
        onCartChange();
        setTimeout(() => navigate('/orders'), 1200);
      })
      .catch((err) => {
        setError(err.response?.data?.message || 'Checkout failed.');
      });
  };

  // Total price calculate karo (price * quantity, sabka sum)
  const total = items.reduce(
    (sum, item) => sum + item.product.price * item.quantity,
    0
  );

  if (items.length === 0) {
    return (
      <div>
        <h1>Your Cart</h1>
        <p>Your cart is empty. Add some items from the Shop page..</p>
      </div>
    );
  }

  return (
    <div>
      <h1>Your Cart</h1>
      {message && <div className="toast">{message}</div>}
      <table className="cart-table">
        <thead>
          <tr>
            <th>Product</th>
            <th>Price</th>
            <th>Quantity</th>
            <th>Subtotal</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.id}>
              <td>{item.product.name}</td>
              <td>₹{item.product.price}</td>
              <td>
                <button onClick={() => handleQuantityChange(item.id, item.quantity - 1)}>-</button>
                <span className="qty">{item.quantity}</span>
                <button onClick={() => handleQuantityChange(item.id, item.quantity + 1)}>+</button>
              </td>
              <td>₹{(item.product.price * item.quantity).toFixed(2)}</td>
              <td>
                <button className="remove-btn" onClick={() => handleRemove(item.id)}>Remove</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="cart-summary">
        <h2>Total: ₹{total.toFixed(2)}</h2>
        {!showCheckoutForm && (
          <button className="checkout-btn" onClick={() => setShowCheckoutForm(true)}>
            Checkout
          </button>
        )}
      </div>

      {showCheckoutForm && (
        <form className="checkout-form" onSubmit={handlePlaceOrder}>
          <h3>Shipping Details</h3>
          {error && <div className="error-box">{error}</div>}
          <input
            name="customerName"
            placeholder="Full Name"
            value={details.customerName}
            onChange={handleDetailsChange}
            required
          />
          <input
            name="customerEmail"
            type="email"
            placeholder="Email"
            value={details.customerEmail}
            onChange={handleDetailsChange}
            required
          />
          <input
            name="customerPhone"
            placeholder="Phone Number"
            value={details.customerPhone}
            onChange={handleDetailsChange}
            required
          />
          <textarea
            name="shippingAddress"
            placeholder="Full Shipping Address"
            value={details.shippingAddress}
            onChange={handleDetailsChange}
            required
          />
          <div className="form-actions">
            <button type="submit" className="checkout-btn">Place Order</button>
            <button type="button" onClick={() => setShowCheckoutForm(false)}>Cancel</button>
          </div>
        </form>
      )}
    </div>
  );
}

export default CartPage;

import React, { useEffect, useState } from 'react';
import { getWishlist, removeFromWishlist, addToCart } from '../api';
 
function WishlistPage({ onCartChange }) {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
 
  useEffect(() => {
    fetchWishlist();
  }, []);
 
  const fetchWishlist = () => {
    setLoading(true);
    getWishlist()
      .then((res) => setItems(res.data))
      .finally(() => setLoading(false));
  };
 
  const handleRemove = (productId) => {
    removeFromWishlist(productId).then(fetchWishlist);
  };
 
  const handleAddToCart = (product) => {
    addToCart(product.id, 1)
      .then(() => {
        setMessage(`${product.name} Added to cart ✅`);
        onCartChange();
        setTimeout(() => setMessage(''), 2000);
      })
      .catch(() => setMessage('Failed to add to cart.'));
  };
 
  if (loading) return <p>Loading wishlist...</p>;
 
  if (items.length === 0) {
    return (
      <div>
        <h1>My Wishlist</h1>
        <p>Your wishlist is empty. Click the ❤️ icon on products from the Shop page to add them to your wishlist.</p>
      </div>
    );
  }
 
  return (
    <div>
      <h1>My Wishlist</h1>
      {message && <div className="toast">{message}</div>}
      <div className="product-grid">
        {items.map((item) => (
          <div className="product-card" key={item.id}>
            <div className="product-image-wrap">
              <img src={item.product.imageUrl} alt={item.product.name} />
              <button
                className="wishlist-remove-btn"
                onClick={() => handleRemove(item.product.id)}
                title="Remove from wishlist"
              >
                ❤️
              </button>
            </div>
            <h3>{item.product.name}</h3>
            <p className="price">₹{item.product.price}</p>
            <button
              disabled={item.product.stock === 0}
              onClick={() => handleAddToCart(item.product)}
            >
              {item.product.stock === 0 ? 'Out of Stock' : 'Add to Cart'}
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}
 
export default WishlistPage;
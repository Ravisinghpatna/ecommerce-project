import React, { useEffect, useState } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { getProduct, addToCart } from '../api';

function ProductDetailPage({ onCartChange }) {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [quantity, setQuantity] = useState(1);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [selectedImage, setSelectedImage] = useState(null);

  
  useEffect(() => {
  setLoading(true);
  getProduct(id)
    .then((res) => {
      setProduct(res.data);
      setSelectedImage(res.data.imageUrl);
    })
    .catch(() => setError('Product nahi mila.'))
    .finally(() => setLoading(false));
}, [id]);

  const handleAddToCart = () => {
    addToCart(product.id, quantity)
      .then(() => {
        setMessage(
          `${quantity} × ${product.name} Added to cart successfully ✅`
        );
        onCartChange();
        setTimeout(() => setMessage(''), 2000);
      })
      .catch(() => setMessage('Failed to add the product to the cart'));
  };

  if (loading) return <p>Loading...</p>;

  if (error || !product) {
    return (
      <div>
        <p>{error || 'Product not found.'}</p>
        <Link to="/">← Go back to the shop</Link>
      </div>
    );
  }

  return (
    <div className="product-detail">
      <button className="back-link" onClick={() => navigate(-1)}>
        ← Go back
      </button>

      {message && <div className="toast">{message}</div>}

      <div className="product-detail-grid">
        <div className="product-detail-image">
  <img src={selectedImage} alt={product.name} />
 
  {(product.images && product.images.length > 0) && (
    <div className="thumbnail-strip">
      <img
        src={product.imageUrl}
        alt="thumbnail-primary"
        className={`thumbnail ${selectedImage === product.imageUrl ? 'active' : ''}`}
        onClick={() => setSelectedImage(product.imageUrl)}
      />
      {product.images.map((img) => (
        <img
          key={img.id}
          src={img.imageUrl}
          alt="thumbnail"
          className={`thumbnail ${selectedImage === img.imageUrl ? 'active' : ''}`}
          onClick={() => setSelectedImage(img.imageUrl)}
        />
      ))}
    </div>
  )}
</div>

        <div className="product-detail-info">
          {product.category && (
            <span className="category-badge-inline">
              {product.category}
            </span>
          )}

          <h1>{product.name}</h1>

          <p className="product-detail-price">
            ₹{product.price}
          </p>

          <p className="product-detail-description">
            {product.description}
          </p>

          {/* Updated Stock Display */}
          <p
            className="product-detail-stock"
            style={{
              color:
                product.stock > 0 && product.stock < 10
                  ? 'red'
                  : product.stock === 0
                  ? 'gray'
                  : 'green',
              fontWeight:
                product.stock > 0 && product.stock < 10
                  ? 'bold'
                  : 'normal',
            }}
          >
            {product.stock > 10
              ? 'In Stock'
              : product.stock > 0
              ? `Only ${product.stock} left`
              : 'Out of Stock'}
          </p>

          {product.stock > 0 && (
            <div className="quantity-selector">
              <label>Quantity:</label>

              <button
                onClick={() =>
                  setQuantity((q) => Math.max(1, q - 1))
                }
              >
                -
              </button>

              <span>{quantity}</span>

              <button
                onClick={() =>
                  setQuantity((q) =>
                    Math.min(product.stock, q + 1)
                  )
                }
              >
                +
              </button>
            </div>
          )}

          <button
            className="checkout-btn"
            disabled={product.stock === 0}
            onClick={handleAddToCart}
          >
            {product.stock === 0
              ? 'Out of Stock'
              : 'Add to Cart'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProductDetailPage;
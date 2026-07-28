import React from 'react';
import { Link } from 'react-router-dom';

function ProductCard({
  product,
  onAddToCart,
  isWishlisted,
  onToggleWishlist,
}) {
  return (
    <div className="product-card">
      <Link
        to={`/product/${product.id}`}
        style={{
          textDecoration: 'none',
          color: 'inherit',
        }}
      >
        <img
          src={product.imageUrl}
          alt={product.name}
        />

        <h3>{product.name}</h3>
      </Link>

      <button
        className={`wishlist-heart ${
          isWishlisted ? 'active' : ''
        }`}
        onClick={() => onToggleWishlist(product)}
        title={
          isWishlisted
            ? 'Remove from Wishlist'
            : 'Add to Wishlist'
        }
      >
        {isWishlisted ? '❤️' : '🤍'}
      </button>

      <p className="description">
        {product.description}
      </p>

      <p className="price">
        ₹{product.price}
      </p>

      {/* Updated Stock Display */}
      <p
        className="stock"
        style={{
          color:
            product.stock > 0 &&
            product.stock < 10
              ? 'red'
              : product.stock === 0
              ? 'gray'
              : 'green',
          fontWeight:
            product.stock > 0 &&
            product.stock < 10
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

      <button
        disabled={product.stock === 0}
        onClick={() =>
          onAddToCart(product)
        }
      >
        {product.stock === 0
          ? 'Out of Stock'
          : 'Add to Cart'}
      </button>
    </div>
  );
}

export default ProductCard;
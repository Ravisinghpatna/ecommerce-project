import React, { useEffect, useState } from 'react';
import { getProducts, addToCart, getWishlist, addToWishlist, removeFromWishlist } from '../api';
import ProductCard from '../components/ProductCard';

function ShopPage({ onCartChange }) {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('All');
  const [sortBy, setSortBy] = useState('default');
  const [wishlistIds, setWishlistIds] = useState(new Set());

  // Component mount hote hi products fetch karo (empty dependency array = sirf ek baar chalega)
  useEffect(() => {
    fetchProducts();
	fetchWishlist(); 
  }, []);

  const fetchProducts = () => {
    setLoading(true);
    getProducts()
      .then((res) => setProducts(res.data))
      .catch(() => setMessage('There was an error loading the products. Is the backend running?'))
      .finally(() => setLoading(false));
  };
  
  const fetchWishlist = () => {
  getWishlist()
    .then((res) => setWishlistIds(new Set(res.data.map((item) => item.product.id))))
    .catch(() => setWishlistIds(new Set()));
};
 
const handleToggleWishlist = (product) => {
  const isWishlisted = wishlistIds.has(product.id);
  const action = isWishlisted ? removeFromWishlist(product.id) : addToWishlist(product.id);
 
  action.then(() => {
    setWishlistIds((prev) => {
      const updated = new Set(prev);
      if (isWishlisted) {
        updated.delete(product.id);
      } else {
        updated.add(product.id);
      }
      return updated;
    });
  });
};

  const handleAddToCart = (product) => {
    addToCart(product.id, 1)
      .then(() => {
        setMessage(`${product.name} added to the cart ✅`);
        onCartChange(); // Navbar ka cart count refresh karo
        setTimeout(() => setMessage(''), 2000);
      })
      .catch(() => setMessage('Failed to add item to cart.'));
  };

  // Naam ya description me search term dhoondo (case-insensitive)
  const categories = ['All', ...new Set(products.map((p) => p.category).filter(Boolean))];
 
const filteredProducts = products
  .filter((p) => {
    const term = searchTerm.toLowerCase();
    return (
      p.name.toLowerCase().includes(term) ||
      (p.description && p.description.toLowerCase().includes(term))
    );
  })
  .filter((p) => selectedCategory === 'All' || p.category === selectedCategory)
  .sort((a, b) => {
    if (sortBy === 'price-low') return a.price - b.price;
    if (sortBy === 'price-high') return b.price - a.price;
    if (sortBy === 'name') return a.name.localeCompare(b.name);
    return 0;
  });

  if (loading) return <p>Loading products...</p>;

  return (
    <div>
      <h1>Products</h1>
      {message && <div className="toast">{message}</div>}
      <input
        className="search-box"
        type="text"
        placeholder="🔍 Search products..."
        value={searchTerm}
        onChange={(e) => setSearchTerm(e.target.value)}
      />
	  {/* 👇 YAHAN ADD KARNA HAI */}
    <div className="filter-bar">
      <div className="category-chips">
        {categories.map((cat) => (
          <button
            key={cat}
            className={`chip ${selectedCategory === cat ? 'active' : ''}`}
            onClick={() => setSelectedCategory(cat)}
          >
            {cat}
          </button>
        ))}
      </div>
 
      <select className="sort-select" value={sortBy} onChange={(e) => setSortBy(e.target.value)}>
        <option value="default">Sort: Default</option>
        <option value="price-low">Price: Low to High</option>
        <option value="price-high">Price: High to Low</option>
        <option value="name">Name: A-Z</option>
      </select>
    </div>
   
      {filteredProducts.length === 0 ? (
        <p>No products found.</p>
      ) : (
        <div className="product-grid">
	{filteredProducts.map((product) => (
		<ProductCard
		key={product.id}
		product={product}
		onAddToCart={handleAddToCart}
		isWishlisted={wishlistIds.has(product.id)}
		onToggleWishlist={handleToggleWishlist}
		/>
	))}
	</div>
      )}
    </div>
  );
}

export default ShopPage;

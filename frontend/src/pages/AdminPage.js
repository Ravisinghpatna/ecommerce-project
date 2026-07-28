import React, { useEffect, useState } from 'react';
import { getProducts, createProduct, updateProduct, deleteProduct } from '../api';

//const emptyForm = { name: '', description: '', category: '', price: '', imageUrl: '', stock: '' };
const emptyForm = { name: '', description: '', category: '', price: '', imageUrl: '', stock: '', additionalImages: '' };

function AdminPage() {
  const [products, setProducts] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null); // null = "add mode", varna "edit mode"

  useEffect(() => {
    fetchProducts();
  }, []);

  const fetchProducts = () => {
    getProducts().then((res) => setProducts(res.data));
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const payload = {
      ...form,
      price: parseFloat(form.price),
      stock: parseInt(form.stock, 10),
	  additionalImageUrls: form.additionalImages
    .split('\n')
    .map((url) => url.trim())
    .filter((url) => url.length > 0),
    };

    const action = editingId ? updateProduct(editingId, payload) : createProduct(payload);

    action.then(() => {
      setForm(emptyForm);
      setEditingId(null);
      fetchProducts();
    });
  };

  const handleEdit = (product) => {
    setEditingId(product.id);
    setForm({
  name: product.name || '',
  description: product.description || '',
  category: product.category || '',
  price: product.price,
  imageUrl: product.imageUrl || '',
  stock: product.stock,
  additionalImages: (product.images || []).map((img) => img.imageUrl).join('\n'),
    });
  };

  const handleDelete = (id) => {
    if (window.confirm('Do you want to delete this product?')) {
      deleteProduct(id).then(fetchProducts);
    }
  };

  const handleCancelEdit = () => {
    setEditingId(null);
    setForm(emptyForm);
  };

  return (
    <div>
      <h1>Admin — Manage Products</h1>

      <form className="admin-form" onSubmit={handleSubmit}>
        <h3>{editingId ? 'Edit Product' : 'Add New Product'}</h3>
        <input name="name" placeholder="Name" value={form.name} onChange={handleChange} required />
        <input name="description" placeholder="Description" value={form.description} onChange={handleChange} />
		<input name="category" placeholder="Category (e.g Accessories, Audio, Electronics"  value={form.category} onChange={handleChange} list="category-suggrstions" required />
		<datalist id="category-suggestions">
		{[...new Set(products.map((p) => p.category))].map((cat) => (
		<option key={cat} value={cat} />
		))}
		</datalist>
  
        <input name="price" type="number" step="0.01" placeholder="Price" value={form.price} onChange={handleChange} required />
        <input name="imageUrl" placeholder="Image URL" value={form.imageUrl} onChange={handleChange} />
		<textarea
		name="additionalImages"
		placeholder="Additional Image URLs — 1 URL in 1 Line (optional)"
		value={form.additionalImages}
		onChange={handleChange}
		rows={3}
		/>
        <input name="stock" type="number" placeholder="Stock" value={form.stock} onChange={handleChange} required />
        <div className="form-actions">
          <button type="submit">{editingId ? 'Update' : 'Add'} Product</button>
          {editingId && <button type="button" onClick={handleCancelEdit}>Cancel</button>}
        </div>
      </form>

      <table className="cart-table">
        <thead>
          <tr>
            <th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th></th>
          </tr>
        </thead>
        <tbody>
          {products.map((p) => (
            <tr key={p.id}>
            <td>{p.name}</td>
			<td>{p.category}</td>
			<td>₹{p.price}</td>
			<td>
				{p.stock} {p.stock === 0 && <span className="stock-badge out">Out of Stock</span>}
				{p.stock > 0 && p.stock <= 5 && <span className="stock-badge low">Low Stock</span>}
			</td>
              <td>
                <button onClick={() => handleEdit(p)}>Edit</button>
                <button className="remove-btn" onClick={() => handleDelete(p.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AdminPage;
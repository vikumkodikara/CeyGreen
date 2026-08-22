import React, { useEffect, useState } from 'react';
import {
  createProduct,
  deleteProduct,
  listFarmerProducts,
  updateProduct,
  updateProductStock,
  updateProductStatus,
} from '../../api/productApi';
import { ConfirmDialog, FarmerListingForm } from '../../components/marketplace';
import { Button } from '../../components/ui/Button';
import { Card } from '../../components/ui/Card';
import { Input } from '../../components/ui/Input';
import { PageHeader } from '../../components/layout/PageHeader';
import { Spinner } from '../../components/ui/Spinner';
import { useAuth } from '../../hooks/useAuth';
import { useToast } from '../../context/ToastContext';
import { Product, ProductCreateRequest } from '../../types/product';
import { getApiErrorMessage } from '../../utils/apiError';

const todayIso = () => new Date().toISOString().slice(0, 10);

export const FarmerProductsPage: React.FC = () => {
  const { user } = useAuth();
  const { showToast } = useToast();
  const farmerId = user?.farmerId || user?.id;

  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [actionId, setActionId] = useState<number | null>(null);
  const [showCreate, setShowCreate] = useState(false);
  const [createForm, setCreateForm] = useState<ProductCreateRequest>({
    cropName: '',
    quantity: 20,
    unitPrice: 150,
    harvestDate: todayIso(),
    location: '',
    description: '',
  });
  const [editProduct, setEditProduct] = useState<Product | null>(null);
  const [stockProduct, setStockProduct] = useState<Product | null>(null);
  const [stockQty, setStockQty] = useState(0);
  const [deleteTarget, setDeleteTarget] = useState<Product | null>(null);

  const load = async () => {
    if (!farmerId) return;
    setLoading(true);
    try {
      const page = await listFarmerProducts(farmerId, { page: 0, size: 100 });
      setProducts(page.content);
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Failed to load products.'), 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, [farmerId]);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    setActionId(-1);
    try {
      await createProduct(createForm);
      showToast('Product listed successfully', 'success');
      setShowCreate(false);
      setCreateForm({ cropName: '', quantity: 20, unitPrice: 150, harvestDate: todayIso(), location: '', description: '' });
      await load();
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Failed to create product.'), 'error');
    } finally {
      setActionId(null);
    }
  };

  const handleEditSave = async () => {
    if (!editProduct) return;
    setActionId(editProduct.id);
    try {
      await updateProduct(editProduct.id, {
        unitPrice: editProduct.unitPrice,
        quantity: editProduct.quantity,
        description: editProduct.description ?? undefined,
        location: editProduct.location,
      });
      showToast('Product updated', 'success');
      setEditProduct(null);
      await load();
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Update failed.'), 'error');
    } finally {
      setActionId(null);
    }
  };

  const handleStockSave = async () => {
    if (!stockProduct) return;
    setActionId(stockProduct.id);
    try {
      await updateProductStock(stockProduct.id, stockQty);
      showToast('Stock updated', 'success');
      setStockProduct(null);
      await load();
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Stock update failed.'), 'error');
    } finally {
      setActionId(null);
    }
  };

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setActionId(deleteTarget.id);
    try {
      await deleteProduct(deleteTarget.id);
      showToast('Product removed', 'success');
      setDeleteTarget(null);
      await load();
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Delete failed.'), 'error');
    } finally {
      setActionId(null);
    }
  };

  const toggleActive = async (product: Product) => {
    setActionId(product.id);
    try {
      await updateProductStatus(product.id, !product.active);
      showToast(product.active ? 'Listing deactivated' : 'Listing activated', 'success');
      await load();
    } catch (err) {
      showToast(getApiErrorMessage(err, 'Status update failed.'), 'error');
    } finally {
      setActionId(null);
    }
  };

  if (!farmerId) {
    return (
      <div className="marketplace-page">
        <div className="alert alert-error">Farmer profile required.</div>
      </div>
    );
  }

  return (
    <div className="marketplace-page">
      <PageHeader
        title="My Products"
        subtitle="Create and manage your marketplace listings."
        actions={<Button onClick={() => setShowCreate(true)}>+ New listing</Button>}
      />

      {loading ? (
        <Spinner />
      ) : (
        <div className="orders-table-wrap">
          <table className="marketplace-table">
            <thead>
              <tr>
                <th>Name</th>
                <th>Price</th>
                <th>Stock</th>
                <th>Status</th>
                <th>Created</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {products.map((p) => (
                <tr key={p.id}>
                  <td>{p.cropName}</td>
                  <td>Rs. {p.unitPrice.toFixed(2)}</td>
                  <td>{p.quantity} kg</td>
                  <td><span className={`status-badge status-${p.status.toLowerCase().replace('_', '-')}`}>{p.status}</span></td>
                  <td>{p.createdAt ? new Date(p.createdAt).toLocaleDateString() : '—'}</td>
                  <td className="table-actions">
                    <Button size="sm" variant="secondary" onClick={() => setEditProduct({ ...p })}>Edit</Button>
                    <Button size="sm" variant="ghost" onClick={() => { setStockProduct(p); setStockQty(p.quantity); }}>Stock</Button>
                    <Button size="sm" variant="ghost" onClick={() => toggleActive(p)} disabled={actionId === p.id}>
                      {p.active ? 'Deactivate' : 'Activate'}
                    </Button>
                    <Button size="sm" variant="danger" onClick={() => setDeleteTarget(p)}>Delete</Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {showCreate && (
        <div className="modal-overlay">
          <div className="modal-panel glass-panel">
            <FarmerListingForm value={createForm} loading={actionId === -1} onChange={setCreateForm} onSubmit={handleCreate} />
            <Button variant="ghost" onClick={() => setShowCreate(false)}>Cancel</Button>
          </div>
        </div>
      )}

      {editProduct && (
        <div className="modal-overlay">
          <div className="modal-panel glass-panel">
            <h3>Edit {editProduct.cropName}</h3>
            <Input label="Price" type="number" value={editProduct.unitPrice} onChange={(e) => setEditProduct({ ...editProduct, unitPrice: Number(e.target.value) })} />
            <Input label="Stock" type="number" value={editProduct.quantity} onChange={(e) => setEditProduct({ ...editProduct, quantity: Number(e.target.value) })} />
            <Input label="Location" value={editProduct.location} onChange={(e) => setEditProduct({ ...editProduct, location: e.target.value })} />
            <div className="modal-actions">
              <Button variant="secondary" onClick={() => setEditProduct(null)}>Cancel</Button>
              <Button onClick={handleEditSave} isLoading={actionId === editProduct.id}>Save</Button>
            </div>
          </div>
        </div>
      )}

      {stockProduct && (
        <div className="modal-overlay">
          <div className="modal-panel glass-panel">
            <h3>Update stock — {stockProduct.cropName}</h3>
            <Input label="Quantity (kg)" type="number" min={0} value={stockQty} onChange={(e) => setStockQty(Number(e.target.value))} />
            <div className="modal-actions">
              <Button variant="secondary" onClick={() => setStockProduct(null)}>Cancel</Button>
              <Button onClick={handleStockSave} isLoading={actionId === stockProduct.id}>Update</Button>
            </div>
          </div>
        </div>
      )}

      <ConfirmDialog
        open={deleteTarget != null}
        title="Delete listing"
        message={deleteTarget ? `Are you sure you want to delete "${deleteTarget.cropName}"?` : ''}
        confirmLabel="Delete"
        danger
        loading={deleteTarget != null && actionId === deleteTarget.id}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
};

export interface CartItem {
  productId: number;
  cropName: string;
  unitPrice: number;
  quantity: number;
  maxStock: number;
  imageUrl?: string | null;
  location?: string;
}

const STORAGE_KEY = 'ceygreen_cart';

export const loadCart = (): CartItem[] => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw) as CartItem[];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
};

export const saveCart = (items: CartItem[]): void => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(items));
};

export const clearCart = (): void => {
  localStorage.removeItem(STORAGE_KEY);
};

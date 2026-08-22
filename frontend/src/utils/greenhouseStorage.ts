export function farmerStorageId(user: { farmerId?: string; id?: string } | null): string {
  return (user?.farmerId || user?.id || '').trim();
}

export function greenhouseIdKey(ownerId: string): string {
  return `ceygreen.greenhouse.${ownerId}`;
}

export function greenhouseNameKey(ownerId: string): string {
  return `ceygreen.greenhouseName.${ownerId}`;
}

export function readSavedGreenhouse(ownerId: string): { id: string; name: string } {
  if (!ownerId) return { id: '', name: '' };
  try {
    return {
      id: localStorage.getItem(greenhouseIdKey(ownerId)) || '',
      name: localStorage.getItem(greenhouseNameKey(ownerId)) || '',
    };
  } catch {
    return { id: '', name: '' };
  }
}

export function saveGreenhouse(ownerId: string, id: string, name: string): void {
  try {
    localStorage.setItem(greenhouseIdKey(ownerId), id);
    localStorage.setItem(greenhouseNameKey(ownerId), name);
  } catch {
    /* private mode / quota — registration still works in memory */
  }
}

export function clearSavedGreenhouse(ownerId: string): void {
  try {
    localStorage.removeItem(greenhouseIdKey(ownerId));
    localStorage.removeItem(greenhouseNameKey(ownerId));
  } catch {
    /* ignore */
  }
}

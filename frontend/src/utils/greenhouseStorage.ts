export function farmerStorageId(user: { farmerId?: string; id?: string } | null): string {
  return (user?.farmerId || user?.id || '').trim();
}

export function greenhouseIdKey(ownerId: string): string {
  return `ceygreen.greenhouse.${ownerId}`;
}

export function greenhouseNameKey(ownerId: string): string {
  return `ceygreen.greenhouseName.${ownerId}`;
}

const LAST_KEY = 'ceygreen.lastGreenhouse';

export function readSavedGreenhouse(ownerId: string): { id: string; name: string } {
  if (!ownerId) return { id: '', name: '' };
  try {
    const id = localStorage.getItem(greenhouseIdKey(ownerId)) || '';
    const name = localStorage.getItem(greenhouseNameKey(ownerId)) || '';
    if (id) return { id, name };
  } catch {
    /* ignore */
  }
  return { id: '', name: '' };
}

export function readSavedGreenhouseForUser(user: { farmerId?: string; id?: string } | null): {
  id: string;
  name: string;
} {
  const ownerId = farmerStorageId(user);
  const fromOwner = readSavedGreenhouse(ownerId);
  if (fromOwner.id) return fromOwner;
  const fromUserId = user?.id ? readSavedGreenhouse(user.id.trim()) : { id: '', name: '' };
  if (fromUserId.id) return fromUserId;
  try {
    const last = JSON.parse(localStorage.getItem(LAST_KEY) || 'null') as {
      id?: string;
      name?: string;
      farmerId?: string;
    } | null;
    const lastOwner = (last?.farmerId || '').trim();
    if (last?.id && (!lastOwner || lastOwner === ownerId || lastOwner === (user?.id || '').trim())) {
      return { id: last.id, name: last.name || '' };
    }
  } catch {
    /* ignore */
  }
  return { id: '', name: '' };
}

export function saveGreenhouse(ownerId: string, id: string, name: string): void {
  try {
    localStorage.setItem(greenhouseIdKey(ownerId), id);
    localStorage.setItem(greenhouseNameKey(ownerId), name);
  } catch {
    /* private mode / quota — registration still works in memory */
  }
}

export function saveGreenhouseForUser(
  user: { farmerId?: string; id?: string } | null,
  id: string,
  name: string
): void {
  const farmerId = (user?.farmerId || '').trim();
  const userId = (user?.id || '').trim();
  if (farmerId) saveGreenhouse(farmerId, id, name);
  if (userId && userId !== farmerId) saveGreenhouse(userId, id, name);
  try {
    localStorage.setItem(LAST_KEY, JSON.stringify({ id, name, farmerId: farmerId || userId }));
  } catch {
    /* ignore */
  }
}

export function clearSavedGreenhouse(ownerId: string): void {
  try {
    localStorage.removeItem(greenhouseIdKey(ownerId));
    localStorage.removeItem(greenhouseNameKey(ownerId));
    localStorage.removeItem(LAST_KEY);
  } catch {
    /* ignore */
  }
}

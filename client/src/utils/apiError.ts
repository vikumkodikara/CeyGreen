/** Maps axios-style API errors to user-facing messages. */
export const getApiErrorMessage = (err: unknown, fallback: string): string => {
  if (err && typeof err === 'object' && 'response' in err) {
    const data = (err as { response?: { data?: { message?: string }; status?: number } }).response?.data;
    if (data?.message) return data.message;
    const status = (err as { response?: { status?: number } }).response?.status;
    if (status === 409) return 'Insufficient stock for this order.';
    if (status === 403) return 'You are not allowed to perform this action.';
    if (status === 401) return 'Please log in to continue.';
  }
  return fallback;
};

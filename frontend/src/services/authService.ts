import api, { TOKEN_STORAGE_KEY } from './api';
import type { AuthResponse } from '../types';

export const authService = {
  login: async (login: string, password: string): Promise<AuthResponse> => {
    const { data } = await api.post<AuthResponse>('/auth/login', { login, password });
    localStorage.setItem(TOKEN_STORAGE_KEY, data.token);
    return data;
  },

  register: async (login: string, password: string): Promise<AuthResponse> => {
    const { data } = await api.post<AuthResponse>('/auth/register', { login, password });
    localStorage.setItem(TOKEN_STORAGE_KEY, data.token);
    return data;
  },

  logout: (): void => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
  },

  getToken: (): string | null => localStorage.getItem(TOKEN_STORAGE_KEY),

  isAuthenticated: (): boolean => !!localStorage.getItem(TOKEN_STORAGE_KEY),
};

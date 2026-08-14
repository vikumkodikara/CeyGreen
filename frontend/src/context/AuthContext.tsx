import React, { createContext, useState, useEffect } from 'react';
import { User } from '../types/user';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  loginUser: (token: string, user: User) => void;
  logoutUser: () => void;
}

export const AuthContext = createContext<AuthContextType>({
  user: null,
  token: null,
  isAuthenticated: false,
  loginUser: () => {},
  logoutUser: () => {},
});

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const storedToken = localStorage.getItem('ceygreen_token');
    const storedUser = localStorage.getItem('ceygreen_user');

    if (storedToken && storedUser) {
      setToken(storedToken);
      try {
        setUser(JSON.parse(storedUser));
      } catch {
        localStorage.removeItem('ceygreen_user');
      }
    }
  }, []);

  const loginUser = (token: string, user: User) => {
    setToken(token);
    setUser(user);
    localStorage.setItem('ceygreen_token', token);
    localStorage.setItem('ceygreen_user', JSON.stringify(user));
  };

  const logoutUser = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('ceygreen_token');
    localStorage.removeItem('ceygreen_user');
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token,
        loginUser,
        logoutUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

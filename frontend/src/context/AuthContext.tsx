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
  const [user, setUser] = useState<User | null>(() => {
    const storedUser = localStorage.getItem('ceygreen_user');
    if (storedUser) {
      try {
        return JSON.parse(storedUser);
      } catch {
        localStorage.removeItem('ceygreen_user');
      }
    }
    
    // IoT Demo mode fallback if no user exists
    if (import.meta.env.VITE_IOT_ONLY === 'true') {
      const demoUser: User = {
        id: 'farmer-001',
        email: 'iot-demo@ceygreen.local',
        name: 'IoT Demo Farmer',
        role: 'FARMER',
        farmerId: 'farmer-001',
      };
      localStorage.setItem('ceygreen_user', JSON.stringify(demoUser));
      return demoUser;
    }
    
    return null;
  });

  const [token, setToken] = useState<string | null>(() => {
    const storedToken = localStorage.getItem('ceygreen_token');
    if (storedToken) return storedToken;

    if (import.meta.env.VITE_IOT_ONLY === 'true') {
      localStorage.setItem('ceygreen_token', 'iot-demo');
      return 'iot-demo';
    }

    return null;
  });

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
        isAuthenticated: !!token && !!user,
        loginUser,
        logoutUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

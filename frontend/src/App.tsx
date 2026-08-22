import React, { createContext, useContext, useMemo, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { ProtectedRoute } from './components/common/ProtectedRoute';
import { useAuth } from './hooks/useAuth';

import { Navbar } from './components/layout/Navbar';
import { Sidebar } from './components/layout/Sidebar';
import { Footer } from './components/layout/Footer';

import { AuthPage } from './pages/AuthPage';
import { DashboardPage } from './pages/DashboardPage';
import { DiagnosisPage } from './pages/DiagnosisPage';
import { TreatmentsPage } from './pages/TreatmentsPage';
import { GreenhousePage } from './pages/GreenhousePage';
import { ForumPage } from './pages/ForumPage';
import { AnalyticsPage } from './pages/AnalyticsPage';

import { MarketplaceBrowsePage } from './pages/marketplace/MarketplaceBrowsePage';
import { ProductDetailPage } from './pages/marketplace/ProductDetailPage';
import { CartPage } from './pages/marketplace/CartPage';
import { CheckoutPage } from './pages/marketplace/CheckoutPage';
import { BuyerOrdersPage } from './pages/marketplace/BuyerOrdersPage';
import { OrderDetailPage } from './pages/marketplace/OrderDetailPage';
import { FarmerDashboardPage } from './pages/marketplace/FarmerDashboardPage';
import { FarmerProductsPage } from './pages/marketplace/FarmerProductsPage';
import { FarmerOrdersPage } from './pages/marketplace/FarmerOrdersPage';

const ShellContext = createContext({
  navOpen: false,
  toggleNav: () => {},
  closeNav: () => {},
});
export const useShell = () => useContext(ShellContext);

const AppRoutes: React.FC = () => (
  <Routes>
    <Route path="/login" element={<AuthPage />} />
    <Route path="/register" element={<AuthPage />} />

    <Route path="/" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
    <Route path="/diagnosis" element={<ProtectedRoute><DiagnosisPage /></ProtectedRoute>} />
    <Route path="/treatments" element={<ProtectedRoute><TreatmentsPage /></ProtectedRoute>} />
    <Route path="/greenhouse" element={<ProtectedRoute><GreenhousePage /></ProtectedRoute>} />
    <Route path="/forum" element={<ProtectedRoute><ForumPage /></ProtectedRoute>} />
    <Route path="/analytics" element={<AnalyticsPage />} />

    <Route path="/marketplace" element={<ProtectedRoute><MarketplaceBrowsePage /></ProtectedRoute>} />
    <Route path="/marketplace/products/:id" element={<ProtectedRoute><ProductDetailPage /></ProtectedRoute>} />
    <Route path="/marketplace/cart" element={<ProtectedRoute><CartPage /></ProtectedRoute>} />
    <Route path="/marketplace/checkout" element={<ProtectedRoute><CheckoutPage /></ProtectedRoute>} />
    <Route path="/marketplace/orders" element={<ProtectedRoute><BuyerOrdersPage /></ProtectedRoute>} />
    <Route path="/marketplace/orders/:id" element={<ProtectedRoute><OrderDetailPage /></ProtectedRoute>} />

    <Route path="/farmer/dashboard" element={<ProtectedRoute><FarmerDashboardPage /></ProtectedRoute>} />
    <Route path="/farmer/products" element={<ProtectedRoute><FarmerProductsPage /></ProtectedRoute>} />
    <Route path="/farmer/orders" element={<ProtectedRoute><FarmerOrdersPage /></ProtectedRoute>} />
  </Routes>
);

const AppContent: React.FC = () => {
  const { isAuthenticated } = useAuth();
  const { pathname } = useLocation();
  const [navOpen, setNavOpen] = useState(false);
  const shell = useMemo(
    () => ({
      navOpen,
      toggleNav: () => setNavOpen((v) => !v),
      closeNav: () => setNavOpen(false),
    }),
    [navOpen]
  );
  const isAuthPage = pathname === '/login' || pathname === '/register';

  if (isAuthPage) {
    return <AppRoutes />;
  }

  return (
    <ShellContext.Provider value={shell}>
      <div className={`app-shell${navOpen ? ' nav-open' : ''}`}>
        <Navbar />
        <div className="app-body">
          {isAuthenticated && <Sidebar />}
          {navOpen && (
            <button type="button" className="nav-scrim" aria-label="Close menu" onClick={() => setNavOpen(false)} />
          )}
          <main className="app-main">
            <AppRoutes />
          </main>
        </div>
        <Footer />
      </div>
    </ShellContext.Provider>
  );
};

export const App: React.FC = () => (
  <ErrorBoundary>
    <AuthProvider>
      <ToastProvider>
        <Router>
          <AppContent />
        </Router>
      </ToastProvider>
    </AuthProvider>
  </ErrorBoundary>
);

export default App;

import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { ToastProvider } from './context/ToastContext';
import { ErrorBoundary } from './components/common/ErrorBoundary';
import { ProtectedRoute } from './components/common/ProtectedRoute';

import { Navbar } from './components/layout/Navbar';
import { Sidebar } from './components/layout/Sidebar';
import { Footer } from './components/layout/Footer';

import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
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

const AppContent: React.FC = () => (
  <div style={{ display: 'flex', flexDirection: 'column', minHeight: '100vh' }}>
    <Navbar />
    <div style={{ display: 'flex', flex: 1 }}>
      <Sidebar />
      <main style={{ flex: 1, padding: '2rem' }}>
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          <Route path="/" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
          <Route path="/diagnosis" element={<ProtectedRoute><DiagnosisPage /></ProtectedRoute>} />
          <Route path="/treatments" element={<ProtectedRoute><TreatmentsPage /></ProtectedRoute>} />
          <Route path="/greenhouse" element={<ProtectedRoute><GreenhousePage /></ProtectedRoute>} />
          <Route path="/forum" element={<ProtectedRoute><ForumPage /></ProtectedRoute>} />
          <Route path="/analytics" element={<ProtectedRoute><AnalyticsPage /></ProtectedRoute>} />

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
      </main>
    </div>
    <Footer />
  </div>
);

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

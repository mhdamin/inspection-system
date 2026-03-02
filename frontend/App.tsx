import React, { useEffect, useState } from "react";
import { Navigate, Route, Routes } from "react-router-dom";
import AuditTrail from "./components/AuditTrail";
import ChecklistHistory from "./components/ChecklistHistory";
import ChecklistManager from "./components/Checklist/ChecklistManager";
import Dashboard from "./components/Dashboard";
import DefectManagement from "./components/DefectManagement";
import Login from "./components/Login";
import Reports from "./components/Reports";
import UserManagement from "./components/UserManagement";
import VehicleManagement from "./components/VehicleManagement";
import { auth } from "./config";
import AppShell from "./layout/AppShell";

const ProtectedRoute: React.FC<{ isAuthenticated: boolean; children: React.ReactNode }> = ({
  isAuthenticated,
  children,
}) => {
  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
};

const App: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(auth.isAuthenticated());

  useEffect(() => {
    const handleAuthExpired = () => {
      setIsAuthenticated(false);
    };

    window.addEventListener("auth:expired", handleAuthExpired);
    return () => window.removeEventListener("auth:expired", handleAuthExpired);
  }, []);

  const handleLoginSuccess = () => {
    setIsAuthenticated(true);
  };

  const handleLogout = async () => {
    await auth.logout();
    setIsAuthenticated(false);
  };

  return (
    <Routes>
      <Route
        path="/"
        element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <Login onLoginSuccess={handleLoginSuccess} />}
      />

      <Route
        element={
          <ProtectedRoute isAuthenticated={isAuthenticated}>
            <AppShell onLogout={handleLogout} />
          </ProtectedRoute>
        }
      >
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/vehicles" element={<VehicleManagement />} />
        <Route path="/checklist" element={<ChecklistManager />} />
        <Route path="/history" element={<ChecklistHistory />} />
        <Route path="/defects" element={<DefectManagement />} />
        <Route path="/audit" element={<AuditTrail />} />
        <Route path="/reports" element={<Reports />} />
        <Route path="/users" element={auth.hasRole("ROLE_ADMIN") ? <UserManagement /> : <Navigate to="/dashboard" replace />} />
      </Route>

      <Route path="*" element={<Navigate to={isAuthenticated ? "/dashboard" : "/"} replace />} />
    </Routes>
  );
};

export default App;

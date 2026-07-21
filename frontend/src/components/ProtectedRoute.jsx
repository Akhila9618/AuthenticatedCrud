import React from "react";
import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../services/Contexts/AuthContext";

function ProtectedRoute() {
  const { user } = useAuth();
  const userName = user?.userName;
  return userName ? <Outlet /> : <Navigate to="/login" replace />;
}

export default ProtectedRoute;

import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

const ProtectedRoute = ({ children, requiredRole }) => {
   const { user, accessToken } = useAuth();

   console.log("ProtectedRoute Debugging:");
   console.log("[ProtectedRoute] - User Role:", user?.role);
   console.log("[ProtectedRoute] - Required Role:", requiredRole);
   console.log("[ProtectedRoute] - Access Token from Context:", accessToken);
   console.log("[ProtectedRoute] - Access Token from Local Storage:", localStorage.getItem("accessToken"));

   if (!accessToken) {
      console.warn("Access token missing. Redirecting to login...");
      return <Navigate to="/login" />;
   }

   if (requiredRole && user?.role !== requiredRole) {
      console.warn("Role mismatch. Redirecting to unauthorized...");
      return <Navigate to="/unauthorized" />;
   }

   console.log("Roles match. Rendering children.");
   return children;
};

export default ProtectedRoute;
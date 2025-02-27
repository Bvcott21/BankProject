import React, { createContext, useState, useEffect, useContext } from "react";
import authService from "../services/authService";
import { useNavigate } from "react-router-dom";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [accessToken, setAccessToken] = useState(null);
    const [isInitializing, setIsInitializing] = useState(true);

    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        const storedAccessToken = localStorage.getItem("accessToken");

        if (storedUser && storedAccessToken) {
            setUser(JSON.parse(storedUser));
            setAccessToken(storedAccessToken);
        }
        setIsInitializing(false); // Mark initialization as complete
    }, []);

    if (isInitializing) {
        return <div>Loading...</div>;
    }

    const login = async (credentials) => {
        try {
            const data = await authService.login(credentials);

            const userData = {
                username: data.username,
                role: data.role
            }

            localStorage.setItem("user", JSON.stringify(userData));
            localStorage.setItem("accessToken", data.accessToken);
            localStorage.setItem("refreshToken", data.refreshToken);

            setUser(userData);
            setAccessToken(data.accessToken);
        } catch(err) {
            console.error("login error:", err);
            throw err;
        }
        // localStorage.setItem("user", JSON.stringify(userData));
        // localStorage.setItem("accessToken", userData.accessToken);
        // setUser(userData);
        // setAccessToken(userData.accessToken);
        // console.log("Login successful. User and token set in context.");
    };

    const logout = async () => {
        try {
          // Optionally call an API endpoint to invalidate token
          await authService.logout();
        } catch (err) {
          console.error("Logout error:", err);
        } finally {
          // 1. Remove from localStorage
          localStorage.removeItem("user");
          localStorage.removeItem("role")
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
    
          // 2. Reset context
          setUser(null);
          setAccessToken(null);
    
          // 3. Navigate to login or home
        }
    }

    return (
        <AuthContext.Provider value={{ user, accessToken, isLoggedIn: !!login, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
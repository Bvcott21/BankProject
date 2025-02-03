import React, { createContext, useState, useEffect, useContext } from "react";

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

    const login = (userData) => {
        localStorage.setItem("user", JSON.stringify(userData));
        localStorage.setItem("accessToken", userData.accessToken);
        setUser(userData);
        setAccessToken(userData.accessToken);
        console.log("Login successful. User and token set in context.");
    };

    const logout = () => {
        localStorage.removeItem("user");
        localStorage.removeItem("accessToken");
        setUser(null);
        setAccessToken(null);
        console.log("Logged out successfully.");
    };

    return (
        <AuthContext.Provider value={{ user, accessToken, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
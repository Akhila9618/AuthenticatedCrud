import React, { createContext, useContext, useEffect, useState } from "react";

export const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState({});
  useEffect(() => {
    const storedData = JSON.parse(localStorage.getItem("loggedInUser"));
    if (storedData) {
      setUser(storedData);
    }
  }, []);

  const login = (userData) => {
    localStorage.setItem("loggedInUser", JSON.stringify(userData));

    setUser(userData);
  };
  const logout = () => {
      localStorage.removeItem("loggedInUser");
      localStorage.clear();
  };
  return (
    <AuthContext.Provider
      value={{
        user,
        token: user?.token,
        role: user?.role,
        menus: user?.menusData,
        login,
        logout,
        isAuthenticated: !!user,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const authProvideIns = useContext(AuthContext);
  if (!authProvideIns) {
    throw new Error("Use inside provider component only");
  }
  return authProvideIns;
};

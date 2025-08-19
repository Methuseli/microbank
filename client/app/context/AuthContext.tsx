import React, { createContext, useContext, useState, useEffect } from 'react';
import type { User } from '~/types';
import axios from 'axios';
import { baseUrl } from '~/config';
import { useNavigate } from 'react-router';
import { toast } from 'react-toastify';

interface AuthContextType {
  user: User | null;
  login: (credentials: { email: string; password: string }, { setSubmitting,
    setFieldError,
    resetForm,
  }: {
    setSubmitting: (s: boolean) => void;
    setFieldError: (field: string, message: string | undefined) => void;
    resetForm: () => void;
  }) => void;
  logout: () => void;
  loading: boolean;
  fetchUserFromSession: () => void;
  token: string | null;
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  login: () => { },
  logout: () => { },
  loading: false,
  fetchUserFromSession: () => { },
  token: null,
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    const storedToken = localStorage.getItem("token");
    if (storedToken) {
      setToken(storedToken);
    }
  }, []);

  useEffect(() => {
    if (user) {
      navigate("/frontend/dashboard");
      return;
    }
    if (!token) return;
    fetchUserFromSession();
  }, [user, token]);

  console.log("User:   ", user);

  const login = (credentials: { email: string; password: string }, { setSubmitting,
    setFieldError,
    resetForm,
  }: {
    setSubmitting: (s: boolean) => void;
    setFieldError: (field: string, message: string | undefined) => void;
    resetForm: () => void;
  }) => {
    setSubmitting(true);
    axios.post(`${baseUrl}auth/login`, credentials)
      .then(res => {
        if (res.status === 200) {
          setToken(res.data);
          localStorage.setItem("token", res.data);
          resetForm();
          toast.success("Login successful!");
          navigate("/frontend/dashboard");
        }
      })
      .catch(err => {
        toast.error("Login failed. Please check your credentials.");
        console.log("Error logging in ", err);
        const { field, message } = err.response.data;
        setFieldError(field, message);
      })
      .finally(() => {
        setSubmitting(false);
      });
  };

  const fetchUserFromSession = () => {
    setLoading(true);
    axios.get(`${baseUrl}users/current-user`, { headers: { Authorization: `Bearer ${token}` } })
      .then(res => {
        if (res.status === 200) {
          setUser(res.data);
        } else {
          setUser(null);
        }
      })
      .catch(err => {
        console.log("Error ", err);
        setUser(null);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const logout = () => {
    setLoading(true);
    axios.post(`${baseUrl}auth/logout`, {userId: user?.id}, { headers: { Authorization: `Bearer ${token}` } })
      .then(() => {
        setUser(null);
        setToken(null);
        localStorage.removeItem("token");
      })
      .catch((err) => {
        console.log("Error logging out ", err);
      }).finally(() => {
        setLoading(false);
      });
  };


  const contextValue = React.useMemo(
    () => ({ user, login, logout, loading, fetchUserFromSession, token }),
    [user, loading]
  );

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};
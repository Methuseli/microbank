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
}

const AuthContext = createContext<AuthContextType>({
  user: null,
  login: () => { },
  logout: () => { },
  loading: false,
  fetchUserFromSession: () => { },
});

export const useAuth = () => useContext(AuthContext);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (user) {
      navigate("/dashboard");
      return;
    }
    fetchUserFromSession();
  }, [user]);

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
          setUser(res.data);
          resetForm();
          toast.success("Login successful!");
          navigate("/dashboard");
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
    axios.get(`${baseUrl}users/current-user`, { withCredentials: true })
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
    axios.post(`${baseUrl}auth/logout`, {}, { withCredentials: true })
      .then(() => {
        setUser(null);
      })
      .catch((err) => {
        console.log("Error logging out ", err);
      });
  };


  const contextValue = React.useMemo(
    () => ({ user, login, logout, loading, fetchUserFromSession }),
    [user, loading]
  );

  return (
    <AuthContext.Provider value={contextValue}>
      {children}
    </AuthContext.Provider>
  );
};
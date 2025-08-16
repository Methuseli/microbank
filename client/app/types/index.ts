export interface User {
  id: string;
  email: string;
  name: string;
  balance: number;
  isBlacklisted: boolean;
  isAdmin: boolean;
  createdAt: string;
}

export interface Transaction {
  id: string;
  userId: string;
  transactionType: 'deposit' | 'withdrawal';
  amount: number;
  timestamp: string;
  description?: string;
  status: 'completed' | 'pending' | 'failed';
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
}
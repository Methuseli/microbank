export interface User {
  id: string;
  email: string;
  name: string;
  balance: number;
  blacklisted: boolean;
  role: string;
  createdAt: string;
}

export interface Transaction {
  id: string;
  accountId: string;
  transactionType: 'deposit' | 'withdrawal';
  amount: number;
  createdAt: string;
  description?: string;
}

export interface AuthState {
  user: User | null;
  isAuthenticated: boolean;
}

export interface BankAccount {
  id: string;
  userId: string;
  accountNumber: string;
  balance: number;
  createdAt: string;  
}
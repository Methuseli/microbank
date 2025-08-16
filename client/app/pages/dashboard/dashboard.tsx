import React, { useState, useEffect } from 'react';
import {
    DollarSign,
    TrendingUp,
    TrendingDown,
    Clock,
    AlertTriangle,
    Plus,
    Minus,
    Eye,
    EyeOff,
    RefreshCw
} from 'lucide-react';
import type { User, Transaction } from "~/types";

const Dashboard: React.FC = () => {
    const [showBalance, setShowBalance] = useState(true);
    const [activeTab, setActiveTab] = useState<'deposit' | 'withdrawal'>('deposit');
    const [amount, setAmount] = useState('');
    const [description, setDescription] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [error, setError] = useState('');
    const [user, setUser] = useState<User>({
        id: '1',
        name: 'John Doe',
        email: 'john.doe@example.com',
        balance: 1000,
        isBlacklisted: false,
        createdAt: new Date().toISOString(),
        isAdmin: false
    });

    const onLogout = () => {
        setUser({
            id: '1',
            name: 'John Doe',
            email: 'john.doe@example.com',
            balance: 1000,
            isBlacklisted: false,
            createdAt: new Date().toISOString(),
            isAdmin: false,
        });
    };

    // Mock transaction data
    useEffect(() => {
        const mockTransactions: Transaction[] = [
            {
                id: '1',
                userId: user.id,
                transactionType: 'deposit',
                amount: 500,
                timestamp: '2024-01-15T10:30:00Z',
                description: 'Initial deposit',
                status: 'completed'
            },
            {
                id: '2',
                userId: user.id,
                transactionType: 'withdrawal',
                amount: 150,
                timestamp: '2024-01-14T14:20:00Z',
                description: 'ATM withdrawal',
                status: 'completed'
            },
            {
                id: '3',
                userId: user.id,
                transactionType: 'deposit',
                amount: 1200,
                timestamp: '2024-01-13T09:15:00Z',
                description: 'Salary deposit',
                status: 'completed'
            },
            {
                id: '4',
                userId: user.id,
                transactionType: 'withdrawal',
                amount: 75,
                timestamp: '2024-01-12T16:45:00Z',
                description: 'Online purchase',
                status: 'completed'
            }
        ];
        setTransactions(mockTransactions);
    }, [user.id]);

    const handleTransaction = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (user.isBlacklisted) {
            setError('Your account has been blacklisted. Please contact support.');
            return;
        }

        if (!amount || parseFloat(amount) <= 0) {
            setError('Please enter a valid amount');
            return;
        }

        if (activeTab === 'withdrawal' && parseFloat(amount) > user.balance) {
            setError('Insufficient funds');
            return;
        }

        setIsLoading(true);

        // Simulate API call
        setTimeout(() => {
            const newTransaction: Transaction = {
                id: Date.now().toString(),
                userId: user.id,
                transactionType: activeTab,
                amount: parseFloat(amount),
                timestamp: new Date().toISOString(),
                description: description || `${activeTab === 'deposit' ? 'Deposit' : 'Withdrawal'} transaction`,
                status: 'completed'
            };

            setTransactions(prev => [newTransaction, ...prev]);
            setAmount('');
            setDescription('');
            setIsLoading(false);

            // In a real app, you'd update the user's balance here
            console.log('Transaction completed:', newTransaction);
        }, 1500);
    };

    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    const formatDate = (dateString: string) => {
        return new Date(dateString).toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    if (user.isBlacklisted) {
        return (
            <div className="min-h-screen bg-gray-50 flex items-center justify-center p-4">
                <div className="bg-white rounded-2xl shadow-xl p-8 max-w-md w-full text-center">
                    <div className="w-16 h-16 bg-red-100 rounded-full flex items-center justify-center mx-auto mb-4">
                        <AlertTriangle className="w-8 h-8 text-red-600" />
                    </div>
                    <h2 className="text-2xl font-bold text-gray-900 mb-2">Account Restricted</h2>
                    <p className="text-gray-600 mb-6">
                        Your account has been temporarily restricted. Please contact our support team for assistance.
                    </p>
                    <button
                        onClick={onLogout}
                        className="w-full bg-red-600 text-white py-3 px-4 rounded-xl hover:bg-red-700 transition-colors duration-200"
                    >
                        Sign Out
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50">
            {/* Header */}
            <header className="bg-white shadow-sm border-b border-gray-200">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center mr-3">
                                <DollarSign className="w-5 h-5 text-white" />
                            </div>
                            <h1 className="text-xl font-bold text-gray-900">SecureBank</h1>
                        </div>
                        <div className="flex items-center space-x-4">
                            <span className="text-sm text-gray-600">
                                Welcome, {user.name}
                            </span>
                            <button
                                onClick={onLogout}
                                className="text-sm text-gray-500 hover:text-gray-700 transition-colors duration-200"
                            >
                                Sign Out
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Balance Card */}
                    <div className="lg:col-span-1">
                        <div className="bg-gradient-to-br from-blue-600 to-blue-700 rounded-2xl p-6 text-white shadow-xl">
                            <div className="flex items-center justify-between mb-4">
                                <h3 className="text-lg font-semibold">Account Balance</h3>
                                <button
                                    onClick={() => setShowBalance(!showBalance)}
                                    className="p-2 hover:bg-white/10 rounded-lg transition-colors duration-200"
                                >
                                    {showBalance ? <EyeOff className="w-4 h-4" /> : <Eye className="w-4 h-4" />}
                                </button>
                            </div>
                            <div className="text-3xl font-bold mb-2">
                                {showBalance ? formatCurrency(user.balance) : '••••••'}
                            </div>
                            <p className="text-blue-100 text-sm">Available Balance</p>
                        </div>

                        {/* Transaction Form */}
                        <div className="bg-white rounded-2xl shadow-lg p-6 mt-6">
                            <div className="flex bg-gray-100 rounded-xl p-1 mb-6">
                                <button
                                    onClick={() => setActiveTab('deposit')}
                                    className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all duration-200 ${activeTab === 'deposit'
                                            ? 'bg-white text-green-600 shadow-sm'
                                            : 'text-gray-600 hover:text-gray-800'
                                        }`}
                                >
                                    <Plus className="w-4 h-4 inline mr-2" />
                                    Deposit
                                </button>
                                <button
                                    onClick={() => setActiveTab('withdrawal')}
                                    className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all duration-200 ${activeTab === 'withdrawal'
                                            ? 'bg-white text-red-600 shadow-sm'
                                            : 'text-gray-600 hover:text-gray-800'
                                        }`}
                                >
                                    <Minus className="w-4 h-4 inline mr-2" />
                                    Withdraw
                                </button>
                            </div>

                            <form onSubmit={handleTransaction} className="space-y-4">
                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Amount
                                    </label>
                                    <div className="relative">
                                        <DollarSign className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                                        <input
                                            type="number"
                                            step="0.01"
                                            min="0"
                                            value={amount}
                                            onChange={(e) => setAmount(e.target.value)}
                                            className="w-full pl-10 pr-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                            placeholder="0.00"
                                            required
                                        />
                                    </div>
                                </div>

                                <div>
                                    <label className="block text-sm font-medium text-gray-700 mb-2">
                                        Description (Optional)
                                    </label>
                                    <input
                                        type="text"
                                        value={description}
                                        onChange={(e) => setDescription(e.target.value)}
                                        className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                        placeholder="Transaction description"
                                    />
                                </div>

                                {error && (
                                    <div className="bg-red-50 border border-red-200 rounded-xl p-3">
                                        <p className="text-red-600 text-sm">{error}</p>
                                    </div>
                                )}

                                <button
                                    type="submit"
                                    disabled={isLoading}
                                    className={`w-full py-3 px-4 rounded-xl font-semibold transition-all duration-200 ${activeTab === 'deposit'
                                            ? 'bg-green-600 hover:bg-green-700 text-white'
                                            : 'bg-red-600 hover:bg-red-700 text-white'
                                        } disabled:opacity-50 disabled:cursor-not-allowed`}
                                >
                                    {isLoading ? (
                                        <RefreshCw className="w-4 h-4 animate-spin mx-auto" />
                                    ) : (
                                        `${activeTab === 'deposit' ? 'Deposit' : 'Withdraw'} ${amount ? formatCurrency(parseFloat(amount)) : 'Funds'}`
                                    )}
                                </button>
                            </form>
                        </div>
                    </div>

                    {/* Transaction History */}
                    <div className="lg:col-span-2">
                        <div className="bg-white rounded-2xl shadow-lg">
                            <div className="p-6 border-b border-gray-200">
                                <h3 className="text-lg font-semibold text-gray-900">Transaction History</h3>
                            </div>
                            <div className="p-6">
                                {transactions.length === 0 ? (
                                    <div className="text-center py-8">
                                        <Clock className="w-12 h-12 text-gray-300 mx-auto mb-4" />
                                        <p className="text-gray-500">No transactions yet</p>
                                    </div>
                                ) : (
                                    <div className="space-y-4">
                                        {transactions.map((transaction) => (
                                            <div
                                                key={transaction.id}
                                                className="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors duration-200"
                                            >
                                                <div className="flex items-center">
                                                    <div className={`w-10 h-10 rounded-full flex items-center justify-center mr-4 ${transaction.transactionType === 'deposit'
                                                            ? 'bg-green-100 text-green-600'
                                                            : 'bg-red-100 text-red-600'
                                                        }`}>
                                                        {transaction.transactionType === 'deposit' ? (
                                                            <TrendingUp className="w-5 h-5" />
                                                        ) : (
                                                            <TrendingDown className="w-5 h-5" />
                                                        )}
                                                    </div>
                                                    <div>
                                                        <p className="font-medium text-gray-900">
                                                            {transaction.description}
                                                        </p>
                                                        <p className="text-sm text-gray-500">
                                                            {formatDate(transaction.timestamp)}
                                                        </p>
                                                    </div>
                                                </div>
                                                <div className="text-right">
                                                    <p className={`font-semibold ${transaction.transactionType === 'deposit'
                                                            ? 'text-green-600'
                                                            : 'text-red-600'
                                                        }`}>
                                                        {transaction.transactionType === 'deposit' ? '+' : '-'}
                                                        {formatCurrency(transaction.amount)}
                                                    </p>
                                                    <p className="text-sm text-gray-500 capitalize">
                                                        {transaction.status}
                                                    </p>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Dashboard;
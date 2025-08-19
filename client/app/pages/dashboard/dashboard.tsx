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
    EyeOff
} from 'lucide-react';
import type { Transaction, BankAccount, User } from "~/types";
import axios from "axios";
import { bankingUrl } from '~/config';
import { useAuth } from '~/context/AuthContext';
import { Form, Formik } from 'formik';
import { transactionSchema } from '~/schema/transation';
import FormController from '~/utils/FormController';
import { getTransactionForm } from '~/utils/forms/transaction';
import { Bounce, toast, ToastContainer } from 'react-toastify';
import { Navigate, useNavigate } from 'react-router';

const Dashboard: React.FC = () => {
    const [showBalance, setShowBalance] = useState(true);
    const [activeTab, setActiveTab] = useState<'deposit' | 'withdraw'>('deposit');
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const navigate = useNavigate();

    const { user, logout, loading } : { user: User | null; logout: () => void; loading: boolean; } = useAuth();
    const [bankAccount, setBankAccount] = useState<BankAccount>({
        id: '1',
        userId: '',
        accountNumber: '',
        balance: 0.0,
        createdAt: new Date().toISOString()
    });

    useEffect(() => {
        if (!user && !user?.blacklisted) return;
        axios.get(`${bankingUrl}bank-accounts/${user.id}`, { withCredentials: true })
            .then(response => {
                setBankAccount(response.data);
            })
            .catch(error => {
                console.error('Error fetching bank account:', error);
            });
    }, [user])

    // Mock transaction data
    useEffect(() => {
        if (bankAccount.id === "1") return;
        axios.get(`${bankingUrl}bank-transactions/${bankAccount?.id}/transactions`, { withCredentials: true })
            .then(response => {
                setTransactions(response.data);
            })
            .catch(error => {
                console.error('Error fetching transactions:', error);
            });
    }, [bankAccount]);

    const handleTransaction = (values: { amount: number; description: string; activeTab: 'deposit' | 'withdraw' },
        {
            setSubmitting,
            setFieldError,
            resetForm,
        }: {
            setSubmitting: (s: boolean) => void;
            setFieldError: (field: string, message: string | undefined) => void;
            resetForm: () => void;
        }
    ) => {
        setSubmitting(true);
        const url = `${bankingUrl}bank-accounts/${bankAccount?.id}/${activeTab}`;
        axios
            .patch(url, { amount: values.amount, description: values.description }, { withCredentials: true })
            .then(response => {
                if (response.status === 200) {
                    toast.success("Transaction was successful!!!");
                    setBankAccount(response.data);
                    resetForm();
                }
            })
            .catch(error => {
                toast.error("Transaction failed....");
                if (error.response) {
                    const { field, message } = error.response.data;
                    setFieldError(field, message);
                }
            })
            .finally(() => {
                setSubmitting(false)
            });
    };

    if (!user) {
        return <Navigate to="/frontend/" />;
    }

    const formatCurrency = (amount: number) => {
        if (amount) {
            return new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD'
            }).format(amount);
        }
        return "0.00"
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

    const buttonText = () => {
        return activeTab === 'deposit' ? 'Deposit' : 'Withdraw';
    }

    const checkWithdrawalLimit = (amount: number) => {
        if (amount > bankAccount.balance) {
            return "Insufficient funds!!!";
        }
        return false;
    };

    if (user?.blacklisted) {
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
                        onClick={logout}
                        className="w-full bg-red-600 text-white py-3 px-4 rounded-xl hover:bg-red-700 transition-colors duration-200"
                        disabled={loading}
                    >
                        {loading ? 'Signing Out...' : 'Sign Out'}
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
                                Welcome, {user?.name}
                            </span>
                            {user?.role === "ADMIN" && <button
                                onClick={() => {
                                    navigate("/frontend/admin")
                                }}
                                className="text-sm text-blue-500 hover:text-blue-700 transition-colors duration-200"
                            >
                                Admin
                            </button>}
                            <button
                                onClick={logout}
                                className="border p-2 rounded border-red-300 hover:bg-red-100 text-sm text-red-500 hover:text-red-700 transition-colors duration-200"
                                disabled={loading}
                            >
                                {loading ? 'Signing Out...' : 'Sign Out'}
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
                                {showBalance ? formatCurrency(bankAccount?.balance) : '••••••'}
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
                                    onClick={() => setActiveTab('withdraw')}
                                    className={`flex-1 py-2 px-4 rounded-lg text-sm font-medium transition-all duration-200 ${activeTab === 'withdraw'
                                        ? 'bg-white text-red-600 shadow-sm'
                                        : 'text-gray-600 hover:text-gray-800'
                                        }`}
                                >
                                    <Minus className="w-4 h-4 inline mr-2" />
                                    Withdraw
                                </button>
                            </div>
                            <Formik
                                initialValues={{ amount: 0, description: '', activeTab: activeTab }}
                                validationSchema={transactionSchema}
                                onSubmit={handleTransaction}
                            >
                                {({ values, errors, touched, handleChange, handleBlur, isSubmitting, isValid }) => {
                                    const transactionForm = getTransactionForm({
                                        values,
                                        errors,
                                        touched,
                                        handleChange,
                                        handleBlur
                                    });

                                    return (
                                        <Form className="space-y-4">
                                            {activeTab === 'withdraw' && checkWithdrawalLimit(values.amount) && (
                                                <div className="flex items-center justify-center text-red-500 text-sm">{checkWithdrawalLimit(values.amount)}</div>
                                            )}
                                            <FormController
                                                formData={[
                                                    ...transactionForm,
                                                    {
                                                        type: "submit",
                                                        text: isSubmitting ? (
                                                            "Loading"
                                                        ) : (
                                                            `${buttonText()} ${formatCurrency(values?.amount)}`
                                                        ),
                                                        buttonClassName: `w-full py-3 px-4 rounded-xl font-semibold transition-all duration-200 ${activeTab === 'deposit'
                                                            ? 'bg-green-600 hover:bg-green-700 text-white'
                                                            : 'bg-red-600 hover:bg-red-700 text-white'
                                                            } disabled:opacity-50 disabled:cursor-not-allowed`,
                                                        spanClassName: "flex items-center justify-center",
                                                        icon: false,
                                                        disabled: isSubmitting || !isValid || (activeTab === 'withdraw' && values.amount > bankAccount.balance)
                                                    }
                                                ]} />
                                        </Form>
                                    );
                                }}
                            </Formik>
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
                                                            {formatDate(transaction.createdAt)}
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
            <ToastContainer
                position="top-right"
                autoClose={5000}
                hideProgressBar={false}
                newestOnTop={false}
                closeOnClick={false}
                rtl={false}
                pauseOnFocusLoss
                draggable
                pauseOnHover
                theme="light"
                transition={Bounce}
            />
        </div>
    );
};

export default Dashboard;
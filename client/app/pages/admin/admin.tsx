import React, { useState, useEffect } from 'react';
import {
  Users,
  Shield,
  ShieldOff,
  Search,
  Filter,
  DollarSign,
  AlertTriangle,
  CheckCircle,
  XCircle
} from 'lucide-react';
import type { User } from '~/types';
import { useAuth } from '~/context/AuthContext';
import { Navigate } from 'react-router';
import axios from 'axios';
import { baseUrl } from '~/config';
import { Bounce, toast, ToastContainer } from 'react-toastify';

interface AdminPanelProps {
  currentUser: User;
  onLogout: () => void;
}

const AdminPanel: React.FC = () => {
  const [clients, setClients] = useState<User[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterStatus, setFilterStatus] = useState<'all' | 'active' | 'blacklisted'>('all');
  const [selectedClient, setSelectedClient] = useState<User | null>(null);
  const [showClientModal, setShowClientModal] = useState(false);
  const [blacklisting, setBlacklisting] = useState(false);

  const { user, logout, token } = useAuth();

  const fetchClients = () => {
    axios.get(`${baseUrl}users/admin`, { headers: { Authorization: `Bearer ${token}` } })
      .then(response => {
        setClients(response.data);
      })
      .catch(error => {
        console.error("Error fetching clients:", error);
      });
  }

  // Mock client data
  useEffect(() => {
    if (!user || user?.role !== 'ADMIN') return;
    fetchClients();
  }, [user]);

  if (user?.role !== 'ADMIN') {
    return <Navigate to="/frontend/dashboard" />;
  }


  const toggleBlacklist = (clientId: string, blacklisted: boolean) => {
    axios.patch(`${baseUrl}admin/${clientId}`, { blacklisted: !blacklisted }, { headers: { Authorization: `Bearer ${token}` } }).
      then((response) => {
        if (response.status === 200) {
          toast.success(`Client ${!blacklisted ? 'blocked' : 'unblocked'} successfully.`);
          fetchClients();
        }
      }).catch((error) => {
        toast.error(`Failed to ${!blacklisted ? 'block' : 'unblock'} client.`);
        console.error("Error updating client status:", error);
      })
  };

  const filteredClients = clients.filter(client => {
    const matchesSearch =
      client.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
      client.email.toLowerCase().includes(searchTerm.toLowerCase());

    const matchesFilter =
      filterStatus === 'all' ||
      (filterStatus === 'active' && !client.blacklisted) ||
      (filterStatus === 'blacklisted' && client.blacklisted);

    return matchesSearch && matchesFilter;
  });

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
      day: 'numeric'
    });
  };

  const activeClients = clients.filter(client => !client.blacklisted).length;
  const blacklistedClients = clients.filter(client => client.blacklisted).length;

  const toggleClassName = (client: User) => {
    const unblock = blacklisting ? 'bg-green-100' : 'bg-green-400';
    const block = blacklisting ? 'bg-red-100' : 'bg-red-400';
    return `inline-flex items-center px-3 py-1.5 rounded-lg text-xs font-medium transition-colors duration-200 ${client.blacklisted
      ? `${unblock} text-green-700 hover:bg-green-200`
      : `${block} text-red-700 hover:bg-red-200`
      }`;
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <header className="bg-white shadow-sm border-b border-gray-200">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center h-16">
            <div className="flex items-center">
              <div className="w-8 h-8 bg-blue-600 rounded-lg flex items-center justify-center mr-3">
                <Shield className="w-5 h-5 text-white" />
              </div>
              <h1 className="text-xl font-bold text-gray-900">Admin Panel</h1>
            </div>
            <div className="flex items-center space-x-4">
              <span className="text-sm text-gray-600">
                Admin: {user?.name}
              </span>
              <button
                onClick={logout}
                className="text-sm text-gray-500 hover:text-gray-700 transition-colors duration-200"
              >
                Sign Out
              </button>
            </div>
          </div>
        </div>
      </header>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center">
              <div className="w-12 h-12 bg-blue-100 rounded-xl flex items-center justify-center">
                <Users className="w-6 h-6 text-blue-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Total Clients</p>
                <p className="text-2xl font-bold text-gray-900">{clients.length}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center">
              <div className="w-12 h-12 bg-green-100 rounded-xl flex items-center justify-center">
                <CheckCircle className="w-6 h-6 text-green-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Active Clients</p>
                <p className="text-2xl font-bold text-gray-900">{activeClients}</p>
              </div>
            </div>
          </div>

          <div className="bg-white rounded-2xl p-6 shadow-lg">
            <div className="flex items-center">
              <div className="w-12 h-12 bg-red-100 rounded-xl flex items-center justify-center">
                <XCircle className="w-6 h-6 text-red-600" />
              </div>
              <div className="ml-4">
                <p className="text-sm text-gray-600">Blacklisted</p>
                <p className="text-2xl font-bold text-gray-900">{blacklistedClients}</p>
              </div>
            </div>
          </div>
        </div>

        {/* Client Management */}
        <div className="bg-white rounded-2xl shadow-lg">
          <div className="p-6 border-b border-gray-200">
            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
              <h3 className="text-lg font-semibold text-gray-900">Client Management</h3>

              <div className="flex flex-col sm:flex-row gap-4">
                {/* Search */}
                <div className="relative">
                  <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <input
                    type="text"
                    placeholder="Search clients..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                  />
                </div>

                {/* Filter */}
                <div className="relative">
                  <Filter className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400 w-4 h-4" />
                  <select
                    value={filterStatus}
                    onChange={(e) => setFilterStatus(e.target.value as 'all' | 'active' | 'blacklisted')}
                    className="pl-10 pr-8 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent appearance-none bg-white"
                  >
                    <option value="all">All Clients</option>
                    <option value="active">Active Only</option>
                    <option value="blacklisted">Blacklisted Only</option>
                  </select>
                </div>
              </div>
            </div>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Client
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Joined
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {filteredClients.map((client) => (
                  <tr key={client.id} className="hover:bg-gray-50 transition-colors duration-200">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="w-10 h-10 bg-blue-100 rounded-full flex items-center justify-center">
                          <span className="text-blue-600 font-semibold text-sm">
                            {client.name[0]}
                          </span>
                        </div>
                        <div className="ml-4">
                          <div className="text-sm font-medium text-gray-900">
                            {client.name}
                          </div>
                          <div className="text-sm text-gray-500">{client.email}</div>
                        </div>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${client.blacklisted
                        ? 'bg-red-100 text-red-800'
                        : 'bg-green-100 text-green-800'
                        }`}>
                        {client.blacklisted ? (
                          <>
                            <AlertTriangle className="w-3 h-3 mr-1" />
                            Blacklisted
                          </>
                        ) : (
                          <>
                            <CheckCircle className="w-3 h-3 mr-1" />
                            Active
                          </>
                        )}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                      {formatDate(client.createdAt)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                      <button
                        onClick={() => toggleBlacklist(client.id, client.blacklisted)}
                        className={toggleClassName(client)}
                        disabled={blacklisting}
                      >
                        {client.blacklisted ? (
                          <>
                            <Shield className="w-3 h-3 mr-1" />
                            Unblock
                          </>
                        ) : (
                          <>
                            <ShieldOff className="w-3 h-3 mr-1" />
                            Block
                          </>
                        )}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {filteredClients.length === 0 && (
            <div className="text-center py-8">
              <Users className="w-12 h-12 text-gray-300 mx-auto mb-4" />
              <p className="text-gray-500">No clients found</p>
            </div>
          )}
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

export default AdminPanel;
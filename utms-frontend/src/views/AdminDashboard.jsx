import React, { useState, useEffect } from 'react';
import AdminRulesPage from './AdminRulesPage';
import AdminAnnouncementPanel from '../components/AdminAnnouncementPanel';
import UserListTable from '../components/UserListTable';
import AuditLogsTable from '../components/AuditLogsTable';
import UserFormModal from '../components/UserFormModal';
import { Settings, Megaphone, Users, Plus, Activity } from 'lucide-react';
import { getAllUsers, createUser, updateUser, deleteUser, getAuditLogs } from '../services/api';

export default function AdminDashboard({ user }) {
    const [activeTab, setActiveTab] = useState('rules');
    const [users, setUsers] = useState([]);
    const [auditLogs, setAuditLogs] = useState([]);
    const [isUserModalOpen, setIsUserModalOpen] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [notification, setNotification] = useState(null);

    // Fetch users when tab changes to 'users'
    // Fetch data based on active tab
    useEffect(() => {
        if (activeTab === 'users') {
            fetchUsers();
        } else if (activeTab === 'audit-logs') {
            fetchAuditLogs();
        }
    }, [activeTab]);

    const fetchUsers = async () => {
        try {
            const data = await getAllUsers();
            setUsers(data);
        } catch (error) {
            console.error("Failed to fetch users", error);
        }
    };

    const fetchAuditLogs = async () => {
        try {
            const data = await getAuditLogs();
            setAuditLogs(data);
        } catch (error) {
            console.error("Failed to fetch audit logs", error);
            showNotification('Failed to fetch audit logs', 'error');
        }
    };

    const handleCreateUser = async (userData) => {
        try {
            await createUser(userData);
            showNotification('User Created Successfully', 'success');
            setIsUserModalOpen(false);
            fetchUsers();
        } catch (error) {
            showNotification(error.message || 'Failed to create user', 'error');
        }
    };

    const handleUpdateUser = async (userData) => {
        try {
            await updateUser(editingUser.id, userData);
            showNotification('User Updated Successfully', 'success');
            setIsUserModalOpen(false);
            setEditingUser(null);
            fetchUsers();
        } catch (error) {
            showNotification(error.message || 'Failed to update user', 'error');
        }
    };

    const handleDeleteUser = async (userId) => {
        if (window.confirm("Are you sure you want to deactivate/delete this user?")) {
            try {
                await deleteUser(userId);
                showNotification('User Deactivated/Deleted Successfully', 'success');
                fetchUsers();
            } catch (error) {
                showNotification('Failed to delete user', 'error');
            }
        }
    };

    const openCreateModal = () => {
        setEditingUser(null);
        setIsUserModalOpen(true);
    };

    const openEditModal = (user) => {
        setEditingUser(user);
        setIsUserModalOpen(true);
    };

    const showNotification = (message, type) => {
        setNotification({ message, type });
        setTimeout(() => setNotification(null), 3000);
    };

    return (
        <div className="space-y-6">
            {/* Notification Banner */}
            {notification && (
                <div className={`fixed top-5 right-5 px-4 py-2 rounded shadow text-white z-50 ${notification.type === 'success' ? 'bg-green-600' : 'bg-red-600'}`}>
                    {notification.message}
                </div>
            )}

            {/* Top Bar: Title + Navigation Toggle */}
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-800">Admin Configuration Console</h2>
                <div className="flex space-x-2">
                    <button
                        onClick={() => setActiveTab('rules')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'rules'
                            ? 'bg-red-900 text-white'
                            : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Settings size={16} className="mr-2" />
                        System Rules
                    </button>
                    <button
                        onClick={() => setActiveTab('announcements')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'announcements'
                            ? 'bg-red-900 text-white'
                            : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Megaphone size={16} className="mr-2" />
                        Announcements
                    </button>
                    <button
                        onClick={() => setActiveTab('users')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'users'
                            ? 'bg-red-900 text-white'
                            : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Users size={16} className="mr-2" />
                        User Management
                    </button>
                    <button
                        onClick={() => setActiveTab('audit-logs')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'audit-logs'
                            ? 'bg-red-900 text-white'
                            : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Activity size={16} className="mr-2" />
                        Audit Logs
                    </button>
                </div>
            </div>

            {/* Main Content Area */}
            <div className="bg-white rounded-lg shadow min-h-[500px] p-6">
                {activeTab === 'rules' && (
                    <div className="animate-fade-in-up">
                        <AdminRulesPage isEmbedded={true} />
                    </div>
                )}

                {activeTab === 'announcements' && (
                    <div className="animate-fade-in-up">
                        <AdminAnnouncementPanel user={user} />
                    </div>
                )}

                {activeTab === 'users' && (
                    <div className="animate-fade-in-up">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-xl font-semibold text-gray-700">Registered Users</h3>
                            <button
                                onClick={openCreateModal}
                                className="px-3 py-2 bg-red-900 text-white rounded text-sm hover:bg-red-800 flex items-center"
                            >
                                <Plus size={16} className="mr-1" />
                                Add User
                            </button>
                        </div>
                        <UserListTable
                            users={users}
                            onEdit={openEditModal}
                            onDelete={handleDeleteUser}
                        />
                    </div>
                )}

                {activeTab === 'audit-logs' && (
                    <div className="animate-fade-in-up">
                        <div className="flex justify-between items-center mb-4">
                            <h3 className="text-xl font-semibold text-gray-700">System Audit Logs</h3>
                            <button
                                onClick={fetchAuditLogs}
                                className="text-sm text-blue-600 hover:underline"
                            >
                                Refresh
                            </button>
                        </div>
                        <AuditLogsTable logs={auditLogs} />
                    </div>
                )}
            </div>

            {/* User Form Modal */}
            <UserFormModal
                isOpen={isUserModalOpen}
                onClose={() => setIsUserModalOpen(false)}
                onSubmit={editingUser ? handleUpdateUser : handleCreateUser}
                initialData={editingUser}
            />
        </div>
    );
}

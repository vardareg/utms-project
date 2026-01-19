import React, { useState, useEffect } from 'react';
import AdminRulesPage from './AdminRulesPage';
import AdminAnnouncementPanel from '../components/AdminAnnouncementPanel';
import UserListTable from '../components/UserListTable';
import AuditLogsTable from '../components/AuditLogsTable';
import UserFormModal from '../components/UserFormModal';
import SystemHealthPanel from './SystemHealthPanel';
import { Settings, Megaphone, Users, Plus, Activity, Server } from 'lucide-react';
import { getAllUsers, createUser, updateUser, deleteUser, getAuditLogs } from '../services/api';

export default function AdminDashboard({ user: userProp }) {
    // Safety: user prop can be null after re-renders, so read from localStorage as fallback
    const user = userProp || JSON.parse(localStorage.getItem('utms_user') || 'null');

    if (!user) {
        window.location.href = '/login';
        return null;
    }

    const [activeTab, setActiveTab] = useState('rules');
    const [users, setUsers] = useState([]);
    const [auditLogs, setAuditLogs] = useState([]);
    const [isUserModalOpen, setIsUserModalOpen] = useState(false);
    const [editingUser, setEditingUser] = useState(null);
    const [notification, setNotification] = useState(null);

    // Filters
    const [roleFilter, setRoleFilter] = useState('ALL');
    const [assignmentFilter, setAssignmentFilter] = useState('ALL');
    const [statusFilter, setStatusFilter] = useState('ALL');

    // Derived state for filtering
    const filteredUsers = users.filter(user => {
        const matchesRole = roleFilter === 'ALL' || user.role === roleFilter;

        const userAssignment = user.facultyName || user.departmentName || 'N/A';
        const matchesAssignment = assignmentFilter === 'ALL' || userAssignment === assignmentFilter;

        const userStatus = user.enabled ? 'Active' : 'Inactive';
        const matchesStatus = statusFilter === 'ALL' || userStatus === statusFilter;

        return matchesRole && matchesAssignment && matchesStatus;
    });

    // Extract unique assignments for the dropdown
    const uniqueAssignments = Array.from(new Set(users.map(u => u.facultyName || u.departmentName || 'N/A'))).sort();

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
            if (error.validationErrors) {
                // Return errors to the modal
                throw error;
            }
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
            if (error.validationErrors) {
                // Return errors to the modal
                throw error;
            }
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
                    <button
                        onClick={() => setActiveTab('monitoring')}
                        className={`px-4 py-2 rounded text-sm font-medium transition flex items-center ${activeTab === 'monitoring'
                            ? 'bg-red-900 text-white'
                            : 'bg-white text-gray-600 border hover:bg-gray-50'
                            }`}
                    >
                        <Server size={16} className="mr-2" />
                        System Monitoring
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
                        <div className="flex flex-col space-y-4 mb-4">
                            <div className="flex justify-between items-center">
                                <h3 className="text-xl font-semibold text-gray-700">Registered Users</h3>
                                <button
                                    onClick={openCreateModal}
                                    className="px-3 py-2 bg-red-900 text-white rounded text-sm hover:bg-red-800 flex items-center"
                                >
                                    <Plus size={16} className="mr-1" />
                                    Add User
                                </button>
                            </div>

                            {/* Filters */}
                            <div className="flex flex-wrap gap-4 bg-gray-50 p-4 rounded-lg border border-gray-200">
                                <div className="flex flex-col space-y-1">
                                    <label className="text-xs font-semibold text-gray-500 uppercase">Role</label>
                                    <select
                                        value={roleFilter}
                                        onChange={(e) => setRoleFilter(e.target.value)}
                                        className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:ring-red-900 focus:border-red-900 bg-white"
                                    >
                                        <option value="ALL">All Roles</option>
                                        <option value="ROLE_ADMIN">Admin</option>
                                        <option value="ROLE_STUDENT">Student</option>
                                        <option value="ROLE_OIDB">OIDB</option>
                                        <option value="ROLE_YGK">YGK</option>
                                        <option value="ROLE_DEAN_OFFICE_STAFF">Dean Office</option>
                                    </select>
                                </div>

                                <div className="flex flex-col space-y-1">
                                    <label className="text-xs font-semibold text-gray-500 uppercase">Assignment</label>
                                    <select
                                        value={assignmentFilter}
                                        onChange={(e) => setAssignmentFilter(e.target.value)}
                                        className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:ring-red-900 focus:border-red-900 bg-white"
                                    >
                                        <option value="ALL">All Assignments</option>
                                        {uniqueAssignments.map((assignment, index) => (
                                            <option key={index} value={assignment}>{assignment}</option>
                                        ))}
                                    </select>
                                </div>

                                <div className="flex flex-col space-y-1">
                                    <label className="text-xs font-semibold text-gray-500 uppercase">Status</label>
                                    <select
                                        value={statusFilter}
                                        onChange={(e) => setStatusFilter(e.target.value)}
                                        className="border border-gray-300 rounded px-3 py-1.5 text-sm focus:ring-red-900 focus:border-red-900 bg-white"
                                    >
                                        <option value="ALL">All Statuses</option>
                                        <option value="Active">Active</option>
                                        <option value="Inactive">Inactive</option>
                                    </select>
                                </div>

                                <div className="flex items-end">
                                    <button
                                        onClick={() => {
                                            setRoleFilter('ALL');
                                            setAssignmentFilter('ALL');
                                            setStatusFilter('ALL');
                                        }}
                                        className="text-sm text-red-600 hover:text-red-800 underline pb-2"
                                    >
                                        Reset Filters
                                    </button>
                                </div>
                            </div>
                        </div>

                        <UserListTable
                            users={filteredUsers}
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

                {activeTab === 'monitoring' && (
                    <div className="animate-fade-in-up">
                        <SystemHealthPanel />
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

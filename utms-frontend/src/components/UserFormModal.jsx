import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';

export default function UserFormModal({ isOpen, onClose, onSubmit, initialData }) {
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        role: 'ROLE_STUDENT',
        userType: 'Student',
        enabled: true
    });

    useEffect(() => {
        if (initialData) {
            setFormData({
                username: initialData.username || '',
                email: initialData.email || '',
                password: '', // Password not editable directly here typically, or handled separately. For CREATE it is required.
                role: initialData.role || 'ROLE_STUDENT',
                userType: initialData.userType || 'Student',
                enabled: initialData.enabled !== undefined ? initialData.enabled : true
            });
        } else {
            setFormData({
                username: '',
                email: '',
                password: '',
                role: 'ROLE_STUDENT',
                userType: 'Student',
                enabled: true
            });
        }
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const isEditMode = !!initialData;

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
            <div className="bg-white rounded-lg shadow-xl w-full max-w-md p-6 relative animate-fade-in-up">
                <button
                    onClick={onClose}
                    className="absolute top-4 right-4 text-gray-500 hover:text-gray-700"
                >
                    <X size={24} />
                </button>

                <h3 className="text-xl font-bold mb-4 text-gray-800">
                    {isEditMode ? 'Edit User' : 'Create New User'}
                </h3>

                <form onSubmit={handleSubmit} className="space-y-4">
                    {!isEditMode && (
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            />
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                        />
                    </div>

                    {!isEditMode && (
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                required
                                minLength={6}
                                className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            />
                        </div>
                    )}

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Role</label>
                            <select
                                name="role"
                                value={formData.role}
                                onChange={handleChange}
                                className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            >
                                <option value="ROLE_STUDENT">Student</option>
                                <option value="ROLE_OIDB">OIDB</option>
                                <option value="ROLE_DEAN">Dean</option>
                                <option value="ROLE_YGK">YGK</option>
                                <option value="ROLE_ADMIN">Admin</option>
                            </select>
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">User Type</label>
                            <select
                                name="userType"
                                value={formData.userType}
                                onChange={handleChange}
                                className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            >
                                <option value="Student">Student</option>
                                <option value="Staff">Staff</option>
                                <option value="Faculty">Faculty</option>
                                <option value="Admin">Admin</option>
                            </select>
                        </div>
                    </div>

                    {isEditMode && (
                        <div className="flex items-center">
                            <input
                                type="checkbox"
                                name="enabled"
                                checked={formData.enabled}
                                onChange={handleChange}
                                id="enabled-check"
                                className="h-4 w-4 text-red-900 focus:ring-red-900 border-gray-300 rounded"
                            />
                            <label htmlFor="enabled-check" className="ml-2 block text-sm text-gray-900">
                                Account Enabled
                            </label>
                        </div>
                    )}

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={onClose}
                            className="px-4 py-2 text-gray-600 hover:bg-gray-100 rounded"
                        >
                            Cancel
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-red-900 text-white rounded hover:bg-red-800 transition"
                        >
                            {isEditMode ? 'Update User' : 'Create User'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}

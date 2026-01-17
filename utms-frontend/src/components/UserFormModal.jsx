import React, { useState, useEffect } from 'react';
import { X } from 'lucide-react';

export default function UserFormModal({ isOpen, onClose, onSubmit, initialData }) {
    const [errors, setErrors] = useState({});
    const [formData, setFormData] = useState({
        username: '',
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        role: 'ROLE_STUDENT',
        userType: 'Student',
        enabled: true
    });

    const [faculties, setFaculties] = useState([]);
    const [departments, setDepartments] = useState([]);

    useEffect(() => {
        // Fetch Structure Data if role requires it
        const fetchStructure = async () => {
            // Get token from utms_user object
            const utmsUser = JSON.parse(localStorage.getItem('utms_user') || '{}');
            const token = utmsUser.token;

            if (!token) {
                console.error("No authentication token found");
                return;
            }

            const headers = { Authorization: `Bearer ${token}` };

            try {
                const [facRes, deptRes] = await Promise.all([
                    fetch('http://localhost:8080/api/structure/faculties', { headers }),
                    fetch('http://localhost:8080/api/structure/departments', { headers })
                ]);
                if (facRes.ok) setFaculties(await facRes.json());
                if (deptRes.ok) setDepartments(await deptRes.json());
            } catch (err) {
                console.error("Failed to fetch structure", err);
            }
        };

        if (isOpen) fetchStructure();

        if (initialData) {
            setFormData({
                username: initialData.username || '',
                firstName: initialData.firstName || '',
                lastName: initialData.lastName || '',
                email: initialData.email || '',
                password: '',
                role: initialData.role || 'ROLE_STUDENT',
                userType: initialData.userType || 'Student',
                enabled: initialData.enabled !== undefined ? initialData.enabled : true,
                facultyId: initialData.facultyId || '',
                departmentId: initialData.departmentId || ''
            });
        } else {
            setFormData({
                username: '',
                firstName: '',
                lastName: '',
                email: '',
                password: '',
                role: 'ROLE_STUDENT',
                userType: 'Student',
                enabled: true,
                facultyId: '',
                departmentId: ''
            });
        }
    }, [initialData, isOpen]);

    if (!isOpen) return null;

    const isEditMode = !!initialData;

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;

        // Auto-assign userType based on role
        const roleToUserType = {
            'ROLE_STUDENT': 'Student',
            'ROLE_OIDB': 'Staff',
            'ROLE_DEAN_OFFICE_STAFF': 'Staff',
            'ROLE_YGK': 'Academic',
            'ROLE_ADMIN': 'Admin'
        };

        if (name === 'role') {
            setFormData(prev => ({
                ...prev,
                role: value,
                userType: roleToUserType[value] || 'Staff'
            }));
        } else {
            setFormData(prev => ({
                ...prev,
                [name]: type === 'checkbox' ? checked : value
            }));
        }

        // Clear error for field when typing
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: null }));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setErrors({});
        try {
            await onSubmit(formData);
        } catch (error) {
            if (error.validationErrors) {
                setErrors(error.validationErrors);
            }
        }
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
                                className={`w-full px-3 py-2 border rounded focus:ring-2 outline-none ${errors.username ? 'border-red-500 focus:ring-red-200' : 'focus:ring-red-900'}`}
                            />
                            {errors.username && <p className="text-red-500 text-xs mt-1">{errors.username}</p>}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                        <input
                            type="text"
                            name="firstName"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                            className={`w-full px-3 py-2 border rounded focus:ring-2 outline-none ${errors.firstName ? 'border-red-500 focus:ring-red-200' : 'focus:ring-red-900'}`}
                        />
                        {errors.firstName && <p className="text-red-500 text-xs mt-1">{errors.firstName}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
                        <input
                            type="text"
                            name="lastName"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                            className={`w-full px-3 py-2 border rounded focus:ring-2 outline-none ${errors.lastName ? 'border-red-500 focus:ring-red-200' : 'focus:ring-red-900'}`}
                        />
                        {errors.lastName && <p className="text-red-500 text-xs mt-1">{errors.lastName}</p>}
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            className={`w-full px-3 py-2 border rounded focus:ring-2 outline-none ${errors.email ? 'border-red-500 focus:ring-red-200' : 'focus:ring-red-900'}`}
                        />
                        {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email}</p>}
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
                                minLength={8}
                                className={`w-full px-3 py-2 border rounded focus:ring-2 outline-none ${errors.password ? 'border-red-500 focus:ring-red-200' : 'focus:ring-red-900'}`}
                            />
                            {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password}</p>}
                        </div>
                    )}

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Role</label>
                        <select
                            name="role"
                            value={formData.role}
                            onChange={handleChange}
                            className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                        >
                            <option value="ROLE_STUDENT">Student</option>
                            <option value="ROLE_OIDB">OIDB Staff</option>
                            <option value="ROLE_DEAN_OFFICE_STAFF">Dean's Office Staff</option>
                            <option value="ROLE_YGK">YGK Member</option>
                            <option value="ROLE_ADMIN">Admin</option>
                        </select>
                    </div>

                    {/* DYNAMIC SCOPE SELECTION */}
                    {formData.role === 'ROLE_DEAN_OFFICE_STAFF' && (
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Assign Faculty</label>
                            <select
                                name="facultyId"
                                value={formData.facultyId || ''}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            >
                                <option value="">Select Faculty...</option>
                                {faculties.map(f => (
                                    <option key={f.id} value={f.id}>{f.name}</option>
                                ))}
                            </select>
                        </div>
                    )}

                    {formData.role === 'ROLE_YGK' && (
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Assign Department</label>
                            <select
                                name="departmentId"
                                value={formData.departmentId || ''}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            >
                                <option value="">Select Department...</option>
                                {departments.map(d => (
                                    <option key={d.id} value={d.id}>{d.name}</option>
                                ))}
                            </select>
                        </div>
                    )}

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

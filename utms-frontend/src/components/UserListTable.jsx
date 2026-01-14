import React, { useState } from 'react';
import { Edit2, Trash2 } from 'lucide-react';

export default function UserListTable({ users, onEdit, onDelete }) {
    if (!users || users.length === 0) {
        return <div className="text-gray-500 text-center py-4">No users found.</div>;
    }

    return (
        <div className="overflow-x-auto">
            <table className="min-w-full bg-white border border-gray-200">
                <thead className="bg-gray-50">
                    <tr>
                        <th className="py-2 px-4 border-b text-left text-sm font-semibold text-gray-600">Username</th>
                        <th className="py-2 px-4 border-b text-left text-sm font-semibold text-gray-600">Email</th>
                        <th className="py-2 px-4 border-b text-left text-sm font-semibold text-gray-600">Role</th>
                        <th className="py-2 px-4 border-b text-left text-sm font-semibold text-gray-600">Type</th>
                        <th className="py-2 px-4 border-b text-center text-sm font-semibold text-gray-600">Status</th>
                        <th className="py-2 px-4 border-b text-center text-sm font-semibold text-gray-600">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {users.map((user) => (
                        <tr key={user.id} className="hover:bg-gray-50 transition">
                            <td className="py-2 px-4 border-b text-sm text-gray-800">{user.username}</td>
                            <td className="py-2 px-4 border-b text-sm text-gray-600">{user.email}</td>
                            <td className="py-2 px-4 border-b text-sm">
                                <span className={`px-2 py-1 rounded text-xs font-semibold
                                    ${user.role === 'ROLE_ADMIN' ? 'bg-purple-100 text-purple-800' :
                                        user.role === 'ROLE_STUDENT' ? 'bg-blue-100 text-blue-800' :
                                            'bg-gray-100 text-gray-800'}`}>
                                    {user.role}
                                </span>
                            </td>
                            <td className="py-2 px-4 border-b text-sm text-gray-600">{user.userType}</td>
                            <td className="py-2 px-4 border-b text-center">
                                <span className={`px-2 py-1 rounded-full text-xs font-bold ${user.enabled ? 'bg-green-100 text-green-700' : 'bg-red-100 text-red-700'}`}>
                                    {user.enabled ? 'Active' : 'Inactive'}
                                </span>
                            </td>
                            <td className="py-2 px-4 border-b text-center">
                                <div className="flex justify-center space-x-2">
                                    <button
                                        onClick={() => onEdit(user)}
                                        className="text-blue-600 hover:text-blue-800 p-1 rounded hover:bg-blue-50"
                                        title="Edit"
                                    >
                                        <Edit2 size={16} />
                                    </button>
                                    <button
                                        onClick={() => onDelete(user.id)}
                                        className="text-red-600 hover:text-red-800 p-1 rounded hover:bg-red-50"
                                        title={user.enabled ? "Deactivate" : "Activate"}
                                    >
                                        <Trash2 size={16} />
                                    </button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

import React from 'react';
import { Navigate } from 'react-router-dom';

/**
 * ProtectedRoute Component
 * 
 * Wraps routes that require authentication and/or specific roles.
 * - If no user is logged in, redirects to /login
 * - If user doesn't have required role, shows unauthorized message
 * - Otherwise, renders the child component
 * 
 * @param {React.ReactNode} children - The component to render if authorized
 * @param {string[]} allowedRoles - Array of allowed role strings (e.g., ['ROLE_STUDENT', 'ROLE_ADMIN'])
 */
export default function ProtectedRoute({ children, allowedRoles }) {
    const userStr = localStorage.getItem('utms_user');

    // Not logged in - redirect to login
    if (!userStr) {
        return <Navigate to="/login" replace />;
    }

    const user = JSON.parse(userStr);

    // Check if user has required role
    if (allowedRoles && allowedRoles.length > 0) {
        if (!allowedRoles.includes(user.role)) {
            return (
                <div className="min-h-screen flex items-center justify-center bg-gray-50">
                    <div className="bg-white p-8 rounded-lg shadow-md max-w-md text-center">
                        <h1 className="text-2xl font-bold text-red-600 mb-4">Unauthorized Access</h1>
                        <p className="text-gray-600 mb-4">
                            You do not have permission to access this page.
                        </p>
                        <p className="text-sm text-gray-500">
                            Your role: <span className="font-mono">{user.role}</span>
                        </p>
                        <button
                            onClick={() => window.location.href = '/'}
                            className="mt-6 bg-red-900 text-white px-6 py-2 rounded hover:bg-red-800 transition"
                        >
                            Return to Home
                        </button>
                    </div>
                </div>
            );
        }
    }

    return children;
}

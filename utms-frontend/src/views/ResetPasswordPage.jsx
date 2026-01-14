import React, { useState, useEffect } from 'react';
import { resetPassword } from '../services/api';

export default function ResetPasswordPage({ onSuccess }) {
    const [token, setToken] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        // Parse token from URL
        const params = new URLSearchParams(window.location.search);
        const tokenParam = params.get('token');
        if (tokenParam) {
            setToken(tokenParam);
        } else {
            setError('Invalid or missing token.');
        }
    }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setMessage('');

        if (newPassword !== confirmPassword) {
            setError('Passwords do not match.');
            return;
        }

        const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}$/;
        if (!passwordRegex.test(newPassword)) {
            setError("Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters.");
            return;
        }

        if (!token) {
            setError('Missing reset token.');
            return;
        }

        setLoading(true);
        try {
            const response = await resetPassword(token, newPassword);
            setMessage(response); // "Password reset successfully."
            setTimeout(() => {
                onSuccess(); // Navigate to login
            }, 2000);
        } catch (err) {
            setError(err.message || 'Failed to reset password');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
            <h2 className="text-2xl font-bold text-center text-red-900 mb-6">Reset Password</h2>

            <div className="mb-4 p-3 bg-blue-50 text-blue-800 rounded text-sm border border-blue-200">
                Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters.
            </div>

            {message && (
                <div className="mb-4 p-3 bg-green-100 text-green-700 rounded text-sm">
                    {message}
                </div>
            )}

            {error && (
                <div className="mb-4 p-3 bg-red-100 text-red-700 rounded text-sm">
                    {error}
                </div>
            )}

            <form onSubmit={handleSubmit}>
                <div className="mb-4">
                    <label className="block text-gray-700 text-sm font-bold mb-2">New Password</label>
                    <input
                        type="password"
                        value={newPassword}
                        onChange={(e) => setNewPassword(e.target.value)}
                        required
                        className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                </div>

                <div className="mb-6">
                    <label className="block text-gray-700 text-sm font-bold mb-2">Confirm Password</label>
                    <input
                        type="password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                        className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-red-500"
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading || !token}
                    className={`w-full py-2 px-4 rounded text-white font-bold transition ${loading || !token ? 'bg-gray-400 cursor-not-allowed' : 'bg-red-900 hover:bg-red-800'
                        }`}
                >
                    {loading ? 'Reseting...' : 'Reset Password'}
                </button>
            </form>
        </div>
    );
}

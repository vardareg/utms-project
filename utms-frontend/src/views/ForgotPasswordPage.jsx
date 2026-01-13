import React, { useState } from 'react';
import { forgotPassword } from '../services/api';

export default function ForgotPasswordPage({ onBack }) {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError('');
        setMessage('');

        try {
            const response = await forgotPassword(email);
            setMessage(response); // "If an account exists..."
        } catch (err) {
            setError(err.message || 'Failed to send request');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded-lg shadow-md">
            <h2 className="text-2xl font-bold text-center text-red-900 mb-6">Forgot Password</h2>

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
                    <label className="block text-gray-700 text-sm font-bold mb-2">Email Address</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        className="w-full px-3 py-2 border rounded focus:outline-none focus:ring-2 focus:ring-red-500"
                        placeholder="Enter your registered email"
                    />
                </div>

                <button
                    type="submit"
                    disabled={loading}
                    className={`w-full py-2 px-4 rounded text-white font-bold transition ${loading ? 'bg-gray-400 cursor-not-allowed' : 'bg-red-900 hover:bg-red-800'
                        }`}
                >
                    {loading ? 'Sending...' : 'Send Reset Link'}
                </button>
            </form>

            <button
                onClick={onBack}
                className="w-full mt-4 text-sm text-gray-600 hover:text-red-900"
            >
                Back to Login
            </button>
        </div>
    );
}

import React, { useState } from 'react';
import { Lock, ChevronRight, AlertCircle } from 'lucide-react';
import { API_URL, MOCK_AUTH } from '../services/api';
import NewsFeed from '../components/NewsFeed';

export default function LoginView({ onLogin, error, loading, onForgotPassword }) {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        if (username && password) {
            onLogin(username, password);
        }
    };

    return (
        <div className="min-h-[80vh] flex flex-col gap-8 items-center justify-center p-4">
            {/* Login Form Section */}
            <div className="w-full max-w-md bg-white rounded-lg shadow-xl overflow-hidden">
                <div className="bg-gray-100 px-6 py-4 border-b border-gray-200">
                    <h2 className="text-xl font-semibold text-gray-800 flex items-center">
                        <Lock className="mr-2 w-5 h-5 text-red-900" /> Secure Login
                    </h2>
                </div>
                <form onSubmit={handleSubmit} className="p-6 space-y-4">
                    {error && (
                        <div className="bg-red-50 text-red-700 p-3 rounded flex items-center text-sm">
                            <AlertCircle className="w-4 h-4 mr-2" />{error}
                        </div>
                    )}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                        <input
                            type="text"
                            className="w-full px-4 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                        <input
                            type="password"
                            className="w-full px-4 py-2 border rounded focus:ring-2 focus:ring-red-900 outline-none"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>

                    <div className="flex justify-end">
                        <button
                            type="button"
                            onClick={onForgotPassword}
                            className="text-sm text-red-900 hover:text-red-700 font-medium"
                        >
                            Forgot Password?
                        </button>
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full bg-red-900 hover:bg-red-800 text-white font-bold py-2 px-4 rounded transition flex justify-center items-center ${loading ? 'opacity-70' : ''}`}
                    >
                        {loading ? 'Authenticating...' : <>Login <ChevronRight className="ml-1 w-4 h-4" /></>}
                    </button>
                </form>
            </div>

            {/* Public Announcements Section */}
            <div className="w-full max-w-md md:max-w-lg bg-white rounded-lg shadow-lg overflow-hidden">
                <div className="bg-red-900 px-6 py-4 border-b border-red-800">
                    <h2 className="text-xl font-bold text-white">Latest Announcements</h2>
                </div>
                <div className="p-6 max-h-[500px] overflow-y-auto">
                    <NewsFeed />
                </div>
            </div>
        </div>
    );
}

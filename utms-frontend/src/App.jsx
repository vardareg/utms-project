import React, { useState, useEffect } from 'react';
import { LogOut } from 'lucide-react';
import { apiFetch, API_URL, MOCK_AUTH } from './services/api';

// Views
// Views
import LoginView from './views/LoginView';
import StudentDashboard from './views/StudentDashboard';
import OIDBDashboard from './views/OIDBDashboard';
import YGKDashboard from './views/YGKDashboard';
import DeanDashboard from './views/DeanDashboard';
import AuditLogsPage from './views/AuditLogsPage';
import ForgotPasswordPage from './views/ForgotPasswordPage';
import ResetPasswordPage from './views/ResetPasswordPage';

// ==========================================
// MAIN COMPONENT (App)
// ==========================================
export default function App() {
    const [user, setUser] = useState(null);
    const [currentView, setCurrentView] = useState('login');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        // Check URL for reset password token
        if (window.location.pathname === '/reset-password' || window.location.search.includes('token=')) {
            setCurrentView('reset-password');
            return;
        }

        const savedUser = localStorage.getItem('utms_user');
        if (savedUser) {
            const parsedUser = JSON.parse(savedUser);
            setUser(parsedUser);
            routeUser(parsedUser.role);
        }
    }, []);

    const handleLogin = async (username, password) => {
        setLoading(true);
        setError('');
        try {
            let data;
            if (MOCK_AUTH) {
                // Mock logic if needed
            } else {
                const response = await fetch(`${API_URL}/auth/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username, password }),
                });

                if (!response.ok) throw new Error('Login failed');
                data = await response.json();
            }

            const userData = { username: data.username, role: data.role, token: data.token };
            localStorage.setItem('utms_user', JSON.stringify(userData));
            setUser(userData);
            routeUser(userData.role);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem('utms_user');
        setUser(null);
        setCurrentView('login');
        // Clear URL if on reset page
        if (window.location.pathname === '/reset-password') {
            window.history.pushState({}, '', '/');
        }
    };

    const navigateToAuditLogs = () => {
        setCurrentView('audit-logs');
    };

    const navigateToDashboard = () => {
        if (!user) {
            setCurrentView('login');
            return;
        }
        switch (user.role) {
            case 'ROLE_STUDENT': setCurrentView('student-dashboard'); break;
            case 'ROLE_OIDB': setCurrentView('oidb-dashboard'); break;
            case 'ROLE_YGK': setCurrentView('ygk-dashboard'); break;
            case 'ROLE_DEAN': setCurrentView('dean-dashboard'); break;
            default: setCurrentView('login');
        }
    };

    const routeUser = (role) => {
        switch (role) {
            case 'ROLE_STUDENT': setCurrentView('student-dashboard'); break;
            case 'ROLE_OIDB': setCurrentView('oidb-dashboard'); break;
            case 'ROLE_DEAN': setCurrentView('dean-dashboard'); break;
            case 'ROLE_YGK': setCurrentView('ygk-dashboard'); break;
            default: setCurrentView('login');
        }
    };

    const renderView = () => {
        // Public pages
        if (currentView === 'forgot-password') {
            return <ForgotPasswordPage onBack={() => setCurrentView('login')} />;
        }
        if (currentView === 'reset-password') {
            return <ResetPasswordPage onSuccess={() => {
                setCurrentView('login');
                window.history.pushState({}, '', '/');
            }} />;
        }

        // If not logged in, force login view
        if (!user) return <LoginView
            onLogin={handleLogin}
            error={error}
            loading={loading}
            onForgotPassword={() => setCurrentView('forgot-password')}
        />;

        switch (currentView) {
            case 'login': return <LoginView
                onLogin={handleLogin}
                error={error}
                loading={loading}
                onForgotPassword={() => setCurrentView('forgot-password')}
            />;
            case 'student-dashboard': return <StudentDashboard user={user} />;
            case 'oidb-dashboard': return <OIDBDashboard user={user} />;
            case 'ygk-dashboard': return <YGKDashboard user={user} />;
            case 'dean-dashboard': return <DeanDashboard user={user} />;

            case 'audit-logs': return <AuditLogsPage onBack={navigateToDashboard} />;
            default: return <LoginView onLogin={handleLogin} error={error} loading={loading} onForgotPassword={() => setCurrentView('forgot-password')} />;
        }
    };

    return (
        <div className="min-h-screen bg-gray-50 font-sans text-gray-900">
            <header className="bg-red-900 text-white shadow-md">
                <div className="container mx-auto px-4 py-4 flex justify-between items-center">
                    <div className="flex items-center space-x-2">
                        <div className="w-8 h-8 bg-white rounded-full flex items-center justify-center">
                            <span className="text-red-900 font-bold text-xs">IZ</span>
                        </div>
                        <div>
                            <h1 className="text-lg font-bold leading-none">IZTECH</h1>
                            <p className="text-xs text-red-200">Undergraduate Transfer Management System</p>
                        </div>
                    </div>
                    {user && (
                        <div className="flex items-center space-x-4">
                            <span className="text-sm hidden md:inline">Welcome, {user.username}</span>
                            {(user.role === 'ROLE_OIDB' || user.role === 'ROLE_DEAN') && (
                                <button
                                    onClick={navigateToAuditLogs}
                                    className="bg-blue-800 hover:bg-blue-700 px-3 py-1 rounded text-sm transition"
                                >
                                    Audit Logs
                                </button>
                            )}
                            <button
                                onClick={handleLogout}
                                className="flex items-center space-x-1 bg-red-800 hover:bg-red-700 px-3 py-1 rounded text-sm transition"
                            >
                                <LogOut size={16} /><span>Logout</span>
                            </button>
                        </div>
                    )}
                </div>
            </header>
            <main className="container mx-auto px-4 py-8">{renderView()}</main>
            <footer className="bg-gray-200 text-center py-4 text-xs text-gray-500 mt-auto">
                &copy; 2026 IZTECH Computer Engineering - Team 3. All Rights Reserved.
            </footer>
        </div>
    );
}
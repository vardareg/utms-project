import React, { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom';
import { LogOut } from 'lucide-react';
import { apiFetch, API_URL, MOCK_AUTH } from './services/api';

// Components
import ProtectedRoute from './components/ProtectedRoute';

// Views
import LoginView from './views/LoginView';
import StudentDashboard from './views/StudentDashboard';
import OIDBDashboard from './views/OIDBDashboard';
import YGKDashboard from './views/YGKDashboard';
import DeanDashboard from './views/DeanDashboard';
import AuditLogsPage from './views/AuditLogsPage';
import ForgotPasswordPage from './views/ForgotPasswordPage';
import ResetPasswordPage from './views/ResetPasswordPage';
import AdminDashboard from './views/AdminDashboard';
import RegisterPage from './pages/RegisterPage';

// ==========================================
// LAYOUT WRAPPER (with header/footer)
// ==========================================
function AppLayout({ children }) {
    const navigate = useNavigate();
    const location = useLocation();
    const user = JSON.parse(localStorage.getItem('utms_user') || 'null');

    const handleLogout = () => {
        localStorage.removeItem('utms_user');
        navigate('/login');
    };

    const navigateToAuditLogs = () => {
        navigate('/audit-logs');
    };

    // Don't show header/footer on public pages
    const publicPaths = ['/login', '/register', '/forgot-password', '/reset-password'];
    const isPublicPage = publicPaths.includes(location.pathname);

    return (
        <div className="min-h-screen bg-gray-50 font-sans text-gray-900">
            {!isPublicPage && (
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
                                {(user.role === 'ROLE_OIDB' || user.role === 'ROLE_DEAN_OFFICE_STAFF' || user.role === 'ROLE_YGK') && (
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
            )}
            <main className={isPublicPage ? "" : "container mx-auto px-4 py-8"}>{children}</main>
            {!isPublicPage && (
                <footer className="bg-gray-200 text-center py-4 text-xs text-gray-500 mt-auto">
                    &copy; 2026 IZTECH Computer Engineering - Team 3. All Rights Reserved.
                </footer>
            )}
        </div>
    );
}

// ==========================================
// LOGIN PAGE WITH REDIRECT LOGIC
// ==========================================
function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    // Check if already logged in
    useEffect(() => {
        const savedUser = localStorage.getItem('utms_user');
        if (savedUser) {
            const user = JSON.parse(savedUser);
            routeUserToDashboard(user.role);
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

            const userData = {
                username: data.username,
                firstName: data.firstName,
                lastName: data.lastName,
                email: data.email,
                role: data.role,
                token: data.token,
                departmentId: data.departmentId,
                facultyId: data.facultyId,
                scopeName: data.scopeName
            };
            localStorage.setItem('utms_user', JSON.stringify(userData));

            // Redirect to intended page or dashboard
            const from = location.state?.from || routeUserToDashboard(userData.role);
            navigate(from);
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const routeUserToDashboard = (role) => {
        switch (role) {
            case 'ROLE_STUDENT': return '/student';
            case 'ROLE_OIDB': return '/oidb';
            case 'ROLE_DEAN_OFFICE_STAFF': return '/dean';
            case 'ROLE_YGK': return '/ygk';
            case 'ROLE_ADMIN': return '/admin';
            default: return '/login';
        }
    };

    return (
        <LoginView
            onLogin={handleLogin}
            error={error}
            loading={loading}
            onForgotPassword={() => navigate('/forgot-password')}
        />
    );
}

// ==========================================
// ROOT REDIRECT COMPONENT
// ==========================================
function RootRedirect() {
    const user = JSON.parse(localStorage.getItem('utms_user') || 'null');

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    // Route to appropriate dashboard based on role
    switch (user.role) {
        case 'ROLE_STUDENT': return <Navigate to="/student" replace />;
        case 'ROLE_OIDB': return <Navigate to="/oidb" replace />;
        case 'ROLE_DEAN_OFFICE_STAFF': return <Navigate to="/dean" replace />;
        case 'ROLE_YGK': return <Navigate to="/ygk" replace />;
        case 'ROLE_ADMIN': return <Navigate to="/admin" replace />;
        default: return <Navigate to="/login" replace />;
    }
}

// ==========================================
// MAIN COMPONENT (App)
// ==========================================
export default function App() {
    return (
        <BrowserRouter>
            <AppLayout>
                <Routes>
                    {/* Root redirect */}
                    <Route path="/" element={<RootRedirect />} />

                    {/* Public routes */}
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage onBack={() => window.location.href = '/login'} />} />
                    <Route path="/forgot-password" element={<ForgotPasswordPage onBack={() => window.location.href = '/login'} />} />
                    <Route path="/reset-password" element={<ResetPasswordPage onSuccess={() => window.location.href = '/login'} />} />

                    {/* Protected routes - Student */}
                    <Route
                        path="/student"
                        element={
                            <ProtectedRoute allowedRoles={['ROLE_STUDENT']}>
                                <StudentDashboard
                                    key={JSON.parse(localStorage.getItem('utms_user') || '{}').username || 'student'}
                                    user={JSON.parse(localStorage.getItem('utms_user'))}
                                />
                            </ProtectedRoute>
                        }
                    />

                    {/* Protected routes - OIDB */}
                    <Route
                        path="/oidb"
                        element={
                            <ProtectedRoute allowedRoles={['ROLE_OIDB']}>
                                <OIDBDashboard user={JSON.parse(localStorage.getItem('utms_user'))} />
                            </ProtectedRoute>
                        }
                    />

                    {/* Protected routes - YGK */}
                    <Route
                        path="/ygk"
                        element={
                            <ProtectedRoute allowedRoles={['ROLE_YGK']}>
                                <YGKDashboard user={JSON.parse(localStorage.getItem('utms_user'))} />
                            </ProtectedRoute>
                        }
                    />

                    {/* Protected routes - Dean */}
                    <Route
                        path="/dean"
                        element={
                            <ProtectedRoute allowedRoles={['ROLE_DEAN_OFFICE_STAFF']}>
                                <DeanDashboard user={JSON.parse(localStorage.getItem('utms_user'))} />
                            </ProtectedRoute>
                        }
                    />

                    {/* Protected routes - Admin */}
                    <Route
                        path="/admin"
                        element={
                            <ProtectedRoute allowedRoles={['ROLE_ADMIN']}>
                                <AdminDashboard
                                    user={JSON.parse(localStorage.getItem('utms_user'))}
                                    onLogout={() => {
                                        localStorage.removeItem('utms_user');
                                        window.location.href = '/login';
                                    }}
                                />
                            </ProtectedRoute>
                        }
                    />

                    {/* Protected routes - Audit Logs (multiple roles) */}
                    <Route
                        path="/audit-logs"
                        element={
                            <ProtectedRoute allowedRoles={['ROLE_OIDB', 'ROLE_YGK', 'ROLE_DEAN_OFFICE_STAFF']}>
                                <AuditLogsPage onBack={() => window.history.back()} />
                            </ProtectedRoute>
                        }
                    />

                    {/* Catch-all redirect to root */}
                    <Route path="*" element={<Navigate to="/" replace />} />
                </Routes>
            </AppLayout>
        </BrowserRouter>
    );
}
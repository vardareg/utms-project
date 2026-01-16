
// ==========================================
// CONFIGURATION & CONSTANTS
// ==========================================
export const API_URL = 'http://localhost:8080/api';

export const MOCK_AUTH = false;

// Helper to get auth header
export const getAuthHeader = () => {
    const userStr = localStorage.getItem('utms_user');
    if (userStr) {
        const user = JSON.parse(userStr);
        return { 'Authorization': `Bearer ${user.token}` };
    }
    return {};
};

// Generic fetch wrapper
export const apiFetch = async (endpoint, options = {}) => {
    const headers = {
        'Content-Type': 'application/json', // Default
        ...getAuthHeader(),
        ...options.headers,
    };

    // If body is FormData, let browser set Content-Type with boundary
    if (options.body instanceof FormData) {
        delete headers['Content-Type'];
    }

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers,
    });

    if (response.status === 204) {
        return null;
    }

    if (!response.ok) {
        // Handle 401 Unauthorized globally if needed
        if (response.status === 401) {
            // Could trigger logout here
        }
        const errorText = await response.text();
        throw new Error(errorText || `API Error: ${response.status}`);
    }

    // Check if response is JSON
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
        return await response.json();
    } else {
        return await response.text();
    }
};

// Auth Service methods
export const login = async (username, password) => {
    return await apiFetch('/auth/login', {
        method: 'POST',
        body: JSON.stringify({ username, password }),
    });
};

export const forgotPassword = async (email) => {
    return await apiFetch('/auth/forgot-password', {
        method: 'POST',
        body: JSON.stringify({ email }),
    });
};

export const resetPassword = async (token, newPassword) => {
    return await apiFetch('/auth/reset-password', {
        method: 'POST',
        body: JSON.stringify({ token, newPassword }),
    });
};

// Student Profile methods
export const getMyProfile = async () => {
    return await apiFetch('/student/profile');
};

export const updateMyProfile = async (profileData) => {
    return await apiFetch('/student/profile', {
        method: 'POST',
        body: JSON.stringify(profileData),
    });
};

// Admin User Management
export const getAllUsers = async () => {
    return await apiFetch('/admin/users');
};

export const createUser = async (userData) => {
    return await apiFetch('/admin/users', {
        method: 'POST',
        body: JSON.stringify(userData),
    });
};

export const updateUser = async (id, userData) => {
    return await apiFetch(`/admin/users/${id}`, {
        method: 'PUT',
        body: JSON.stringify(userData),
    });
};

// Audit Logs
export const getAuditLogs = async () => {
    return await apiFetch('/admin/audit-logs');
};

export const deleteUser = async (id) => {
    return await apiFetch(`/admin/users/${id}`, {
        method: 'DELETE',
    });
};

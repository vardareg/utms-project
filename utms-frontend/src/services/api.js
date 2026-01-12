
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
        'Content-Type': 'application/json',
        ...getAuthHeader(),
        ...options.headers,
    };

    const response = await fetch(`${API_URL}${endpoint}`, {
        ...options,
        headers,
    });

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

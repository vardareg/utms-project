import { useState } from 'react';
import axios from 'axios';

const RegisterPage = ({ onBack }) => {
    const [formData, setFormData] = useState({
        username: '',
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        tckn: ''
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const validateTCKN = (tckn) => {
        return /^[0-9]{11}$/.test(tckn);
    };

    const validatePassword = (password) => {
        const regex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?=\S+$).{8,}$/;
        return regex.test(password);
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setError('');
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        // Client-side validation
        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (!validateTCKN(formData.tckn)) {
            setError('TCKN must be exactly 11 digits');
            return;
        }

        if (!validatePassword(formData.password)) {
            setError('Password must be at least 8 characters long and include uppercase, lowercase, numbers, and special characters');
            return;
        }

        setLoading(true);

        try {
            const response = await axios.post('http://localhost:8080/api/auth/register', {
                username: formData.username,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                password: formData.password,
                tckn: formData.tckn
            });

            // Success - redirect to login
            alert("Registration successful. Please login with your credentials.");
            onBack();
        } catch (err) {
            setError(err.response?.data || 'Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-red-50 to-red-100 py-12 px-4">
            <div className="max-w-md w-full bg-white rounded-lg shadow-xl p-8">
                <div className="text-center mb-8">
                    <h2 className="text-3xl font-bold text-gray-900">Student Registration</h2>
                    <p className="text-gray-600 mt-2">Create your account for UTMS</p>
                </div>

                {error && (
                    <div className="mb-4 p-3 bg-red-100 border border-red-400 text-red-700 rounded">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Username</label>
                        <input
                            type="text"
                            name="username"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                        />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">First Name</label>
                            <input
                                type="text"
                                name="firstName"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Last Name</label>
                            <input
                                type="text"
                                name="lastName"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Email</label>
                        <input
                            type="email"
                            name="email"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">TCKN (Turkish ID)</label>
                        <input
                            type="text"
                            name="tckn"
                            value={formData.tckn}
                            onChange={handleChange}
                            required
                            maxLength="11"
                            pattern="[0-9]{11}"
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                            placeholder="11 digits"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Password</label>
                        <input
                            type="password"
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                        />
                        <p className="text-xs text-gray-500 mt-1">
                            Min 8 chars, include uppercase, lowercase, numbers & special characters
                        </p>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Confirm Password</label>
                        <input
                            type="password"
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-900"
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-red-900 text-white py-2 px-4 rounded-md hover:bg-red-800 disabled:opacity-50 disabled:cursor-not-allowed transition duration-200"
                    >
                        {loading ? 'Creating Account...' : 'Create Account'}
                    </button>

                    <div className="text-center mt-4">
                        <button
                            type="button"
                            onClick={onBack}
                            className="text-red-900 hover:underline text-sm font-medium bg-transparent border-none cursor-pointer"
                        >
                            Already have an account? Login here
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default RegisterPage;

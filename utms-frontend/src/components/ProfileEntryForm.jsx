import React, { useState } from 'react';
import { updateMyProfile } from '../services/api';

const ProfileEntryForm = ({ onProfileCreated }) => {
    const [formData, setFormData] = useState({
        tckn: '',
        currentUniversity: '',
        currentProgram: '',
        tckn: '',
        currentUniversity: '',
        currentProgram: '',
        overallGpa: '',
        hasDisciplinaryRecord: false
    });
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
        }));
    };

    const validate = () => {
        if (!formData.tckn || formData.tckn.length !== 11 || !/^\d+$/.test(formData.tckn)) {
            return "TCKN must be exactly 11 digits.";
        }
        const gpa = parseFloat(formData.overallGpa);
        if (isNaN(gpa) || gpa < 0 || gpa > 4) {
            return "GPA must be between 0.00 and 4.00.";
        }
        if (!formData.currentUniversity || !formData.currentProgram) {
            return "All fields are required.";
        }
        return null;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError(null);
        const validationError = validate();
        if (validationError) {
            setError(validationError);
            return;
        }

        setLoading(true);
        try {
            // Ensure GPA matches backend expectations (BigDecimal)
            const gpa = parseFloat(formData.overallGpa);
            const data = { ...formData, overallGpa: gpa };

            await updateMyProfile(data);
            if (onProfileCreated) onProfileCreated();
        } catch (err) {
            console.error(err);
            setError(err.message || "Failed to save profile. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="bg-white p-8 rounded-lg shadow-md max-w-2xl mx-auto mt-8 border border-gray-200">
            <div className="mb-6 border-b pb-4">
                <h2 className="text-2xl font-bold text-gray-800">Complete Your Student Profile</h2>
                <p className="text-gray-600 mt-2">
                    You must complete your profile information before you can apply for transfers.
                    This information will be used for all your applications.
                </p>
            </div>

            {error && (
                <div className="bg-red-50 border-l-4 border-red-500 text-red-700 p-4 mb-6 rounded" role="alert">
                    <p className="font-medium">Error</p>
                    <p>{error}</p>
                </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-5">
                <div className="grid grid-cols-1 gap-5">
                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">TCKN (Turkish Identity Number)</label>
                        <input
                            type="text"
                            name="tckn"
                            value={formData.tckn}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md shadow-sm p-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="12345678901"
                            maxLength={11}
                            required
                        />
                        <p className="text-xs text-gray-500 mt-1">Must be exactly 11 digits.</p>
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Current University</label>
                        <input
                            type="text"
                            name="currentUniversity"
                            value={formData.currentUniversity}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md shadow-sm p-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="e.g. Istanbul Technical University"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Current Program (Department)</label>
                        <input
                            type="text"
                            name="currentProgram"
                            value={formData.currentProgram}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md shadow-sm p-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="e.g. Computer Engineering"
                            required
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-semibold text-gray-700 mb-1">Overall GPA</label>
                        <input
                            type="number"
                            name="overallGpa"
                            value={formData.overallGpa}
                            onChange={handleChange}
                            className="w-full border border-gray-300 rounded-md shadow-sm p-2 focus:ring-blue-500 focus:border-blue-500"
                            placeholder="e.g. 3.50"
                            step="0.01"
                            min="0"
                            max="4"
                            required
                        />
                        <p className="text-xs text-gray-500 mt-1">Enter your current GPA on a 4.00 scale.</p>
                    </div>

                    <div className="bg-yellow-50 p-4 rounded-md border border-yellow-200">
                        <label className="flex items-start space-x-3">
                            <input
                                type="checkbox"
                                name="hasDisciplinaryRecord"
                                checked={formData.hasDisciplinaryRecord}
                                onChange={handleChange}
                                className="h-5 w-5 text-red-600 focus:ring-red-500 border-gray-300 rounded mt-0.5"
                            />
                            <div>
                                <span className="text-sm font-bold text-gray-800">I have an active disciplinary record / penalty.</span>
                                <p className="text-xs text-gray-600 mt-1">
                                    Check this box if you have any active disciplinary actions on your record.
                                    <strong>Note:</strong> Students with active disciplinary penalties are not eligible for transfer.
                                </p>
                            </div>
                        </label>
                    </div>
                </div>

                <div className="pt-4">
                    <button
                        type="submit"
                        disabled={loading}
                        className={`w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 ${loading ? 'opacity-70 cursor-not-allowed' : ''}`}
                    >
                        {loading ? (
                            <span className="flex items-center">
                                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Saving Profile...
                            </span>
                        ) : 'Save Profile & Continue'}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default ProfileEntryForm;

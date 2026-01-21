// views/Register.jsx
import RoleButton from "../components/register/RoleButton";
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Register() {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [role, setRole] = useState('Listener'); 
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const { register } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        // Walidacja
        if (password !== confirmPassword) {
            return setError('Passwords do not match');
        }

        if (password.length < 9) {
            return setError('Password must be at least 9 characters');
        }

        if (!username || !email) {
            return setError('Please fill all fields');
        }

        if (!role) {
            return setError('Please select a role');
        }

        setLoading(true);

        try {
            const result = await register(username, email, password, role);
            
            if (result.success) {
                navigate('/');
            } else {
                setError(result.error);
            }
        } catch (err) {
            setError('Registration failed. Please try again.');
        } finally {
            setLoading(false);
        }
    };

    const handleRoleSelect = (selectedRole) => {
        setRole(selectedRole);
    };

    return (
        <div className="min-h-screen flex items-center justify-center p-4 mt-20">
            <div className="w-full max-w-md">
                <div className="bg-linear-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-8 shadow-2xl">
                    <div className="text-center mb-8">
                        <h1 className="text-3xl font-bold text-white mb-2">Create Account</h1>
                        <p className="text-gray-400">Join our music community</p>
                    </div>

                    <div className="text-center mb-6">
                        <span className="text-gray-400">Already have an account? </span>
                        <Link to="/login" className="text-purple-500 hover:text-purple-400 font-medium">
                            Sign In
                        </Link>
                    </div>

                    {/* Błąd */}
                    {error && (
                        <div className="mb-6 p-3 bg-red-900/30 border border-red-700 rounded-lg">
                            <p className="text-red-300 text-sm">{error}</p>
                        </div>
                    )}

                    {/* Formularz */}
                    <form onSubmit={handleSubmit}>
                        {/* Wybór roli */}
                        <div className="mb-6">
                            <label className="block text-gray-300 text-sm font-medium mb-3">
                                Select Your Role
                            </label>
                            <div className="flex gap-4">
                                <RoleButton 
                                    value="Listener" 
                                    selected={role}
                                    onClick={handleRoleSelect}
                                />
                                <RoleButton 
                                    value="Artist" 
                                    selected={role}
                                    onClick={handleRoleSelect}
                                />
                            </div>
                        </div>

                        {/* Username */}
                        <div className="mb-4">
                            <label className="block text-gray-300 text-sm font-medium mb-2">
                                Username
                            </label>
                            <div className="relative">
                                <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                                    👤
                                </span>
                                <input
                                    type="text"
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 bg-[#1a1a1a] border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="Music Enjoyer 3000"
                                    required
                                />
                            </div>
                        </div>

                        {/* Email */}
                        <div className="mb-4">
                            <label className="block text-gray-300 text-sm font-medium mb-2">
                                Email
                            </label>
                            <div className="relative">
                                <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                                    📧
                                </span>
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 bg-[#1a1a1a] border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="you@example.com"
                                    required
                                />
                            </div>
                        </div>

                        {/* Password */}
                        <div className="mb-4">
                            <label className="block text-gray-300 text-sm font-medium mb-2">
                                Password
                            </label>
                            <div className="relative">
                                <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                                    🔑
                                </span>
                                <input
                                    type="password"
                                    value={password}
                                    onChange={(e) => setPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 bg-[#1a1a1a] border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="Minimum 9 characters"
                                    minLength="9"
                                    required
                                />
                            </div>
                        </div>

                        {/* Confirm Password */}
                        <div className="mb-6">
                            <label className="block text-gray-300 text-sm font-medium mb-2">
                                Confirm Password
                            </label>
                            <div className="relative">
                                <span className="absolute left-3 top-1/2 transform -translate-y-1/2 text-gray-400">
                                    🔑
                                </span>
                                <input
                                    type="password"
                                    value={confirmPassword}
                                    onChange={(e) => setConfirmPassword(e.target.value)}
                                    className="w-full pl-10 pr-4 py-3 bg-[#1a1a1a] border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                    placeholder="Repeat your password"
                                    required
                                />
                            </div>
                        </div>

                        {/* Submit Button */}
                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-gradient-to-r from-purple-700 to-purple-900 hover:from-purple-600 hover:to-purple-800 text-white font-medium py-3 px-6 rounded-lg transition-all duration-300 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                        >
                            {loading ? (
                                <>
                                    <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent"></div>
                                    Creating Account...
                                </>
                            ) : (
                                <>
                                    Create Account
                                    <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" className="bi bi-arrow-right" viewBox="0 0 16 16">
                                        <path fillRule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h11.793l-3.147-3.146a.5.5 0 0 1 .708-.708l4 4a.5.5 0 0 1 0 .708l-4 4a.5.5 0 0 1-.708-.708L13.293 8.5H1.5A.5.5 0 0 1 1 8z"/>
                                    </svg>
                                </>
                            )}
                        </button>
                    </form>

                    {/* Terms */}
                    <div className="mt-6 text-center">
                        <p className="text-xs text-gray-500">
                            By creating an account, you agree to our Terms of Service and Privacy Policy
                        </p>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Register;
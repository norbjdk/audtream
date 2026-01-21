import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { userAPI, tracksAPI } from '../services/api';

function Profile() {
    const { user, fetchCurrentUser, logout } = useAuth();
    const navigate = useNavigate();
    
    // State dla danych profilu
    const [profileData, setProfileData] = useState({
        username: '',
        email: '',
        bio: '',
        location: '',
        website: '',
        socialLinks: {
            twitter: '',
            instagram: '',
            spotify: ''
        }
    });
    
    // State dla statystyk
    const [stats, setStats] = useState({
        totalTracks: 0,
        totalLikes: 0,
        totalPlays: 0,
        topGenres: [],
        joinedDate: ''
    });
    
    // State dla edycji
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [updating, setUpdating] = useState(false);
    const [error, setError] = useState('');
    const [success, setSuccess] = useState('');
    
    // Pobierz dane profilu i statystyki
    useEffect(() => {
        if (user) {
            loadProfileData();
            loadUserStats();
        }
    }, [user]);

    const loadProfileData = () => {
        setProfileData({
            username: user.username || '',
            email: user.email || '',
            bio: user.bio || '',
            location: user.location || '',
            website: user.website || '',
            socialLinks: user.socialLinks || {
                twitter: '',
                instagram: '',
                spotify: ''
            }
        });
    };

    const loadUserStats = async () => {
        try {
            setLoading(true);
            
            // Pobierz utwory użytkownika
            const tracksResponse = await tracksAPI.getUserTracks();
            const tracks = Array.isArray(tracksResponse.data) ? tracksResponse.data : [];
            
            // Oblicz statystyki
            const totalLikes = tracks.reduce((sum, track) => sum + (track.likes || 0), 0);
            const totalPlays = tracks.reduce((sum, track) => sum + (track.plays || 0), 0);
            
            // Znajdź najpopularniejsze gatunki
            const genreCount = {};
            tracks.forEach(track => {
                if (track.genre) {
                    genreCount[track.genre] = (genreCount[track.genre] || 0) + 1;
                }
            });
            
            const topGenres = Object.entries(genreCount)
                .sort(([,a], [,b]) => b - a)
                .slice(0, 3)
                .map(([genre]) => genre);
            
            setStats({
                totalTracks: tracks.length,
                totalLikes: totalLikes,
                totalPlays: totalPlays,
                topGenres: topGenres,
                joinedDate: user.createdAt || new Date().toISOString()
            });
            
        } catch (err) {
            console.error('Error loading stats:', err);
            setError('Failed to load statistics');
        } finally {
            setLoading(false);
        }
    };

    // Obsługa zmian w formularzu
    const handleInputChange = (e) => {
        const { name, value } = e.target;
        
        if (name.startsWith('social.')) {
            const socialKey = name.split('.')[1];
            setProfileData(prev => ({
                ...prev,
                socialLinks: {
                    ...prev.socialLinks,
                    [socialKey]: value
                }
            }));
        } else {
            setProfileData(prev => ({
                ...prev,
                [name]: value
            }));
        }
    };

    // Zapisz zmiany profilu
    const handleSaveProfile = async (e) => {
        e.preventDefault();
        setUpdating(true);
        setError('');
        setSuccess('');

        try {
            // Tutaj dodaj endpoint do aktualizacji profilu w backendzie
            // Na razie symulujemy sukces
            // const response = await userAPI.updateProfile(profileData);
            
            // Tymczasowo: zapisz w localStorage
            const updatedUser = {
                ...user,
                ...profileData,
                updatedAt: new Date().toISOString()
            };
            
            localStorage.setItem('user', JSON.stringify(updatedUser));
            
            setSuccess('Profile updated successfully!');
            setIsEditing(false);
            
            // Odśwież dane użytkownika
            setTimeout(() => {
                fetchCurrentUser();
            }, 1000);
            
        } catch (err) {
            setError('Failed to update profile: ' + (err.message || 'Unknown error'));
        } finally {
            setUpdating(false);
        }
    };

    // Format daty
    const formatDate = (dateString) => {
        if (!dateString) return 'Unknown';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'long',
                day: 'numeric'
            });
        } catch (err) {
            return 'Invalid date';
        }
    };

    // Oblicz ile czasu minęło od rejestracji
    const calculateTimeSinceJoin = (joinDate) => {
        if (!joinDate) return '';
        const join = new Date(joinDate);
        const now = new Date();
        const diffTime = Math.abs(now - join);
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        if (diffDays < 30) return `${diffDays} day${diffDays !== 1 ? 's' : ''}`;
        if (diffDays < 365) {
            const months = Math.floor(diffDays / 30);
            return `${months} month${months !== 1 ? 's' : ''}`;
        }
        const years = Math.floor(diffDays / 365);
        return `${years} year${years !== 1 ? 's' : ''}`;
    };

    // Jeśli użytkownik nie jest zalogowany
    if (!user) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-8">
                <div className="max-w-4xl mx-auto">
                    <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-8 text-center">
                        <div className="text-6xl mb-6">🔒</div>
                        <h1 className="text-3xl font-bold mb-4">Authentication Required</h1>
                        <p className="text-gray-400 mb-8">
                            Please log in to view your profile.
                        </p>
                        <button
                            onClick={() => navigate('/login')}
                            className="px-6 py-3 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-lg transition"
                        >
                            Go to Login
                        </button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-4 md:p-8 mt-20 mb-20">
            <div className="max-w-6xl mx-auto">
                {/* Nagłówek */}
                <div className="mb-8">
                    <h1 className="text-3xl md:text-4xl font-bold mb-2">Profile</h1>
                    <p className="text-gray-400">Manage your account and view statistics</p>
                </div>

                {/* Wiadomości */}
                {error && (
                    <div className="mb-6 p-4 bg-red-900/30 border border-red-700 rounded-lg">
                        <p className="text-red-300">{error}</p>
                    </div>
                )}
                
                {success && (
                    <div className="mb-6 p-4 bg-green-900/30 border border-green-700 rounded-lg">
                        <p className="text-green-300">{success}</p>
                    </div>
                )}

                {/* Główna sekcja */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
                    {/* Lewa kolumna - Informacje o profilu */}
                    <div className="lg:col-span-2 space-y-8">
                        {/* Karta profilu */}
                        <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-2xl p-6">
                            <div className="flex flex-col md:flex-row gap-6">
                                {/* Awatar */}
                                <div className="flex-shrink-0">
                                    <div className="w-32 h-32 bg-gradient-to-br from-purple-600 via-pink-600 to-orange-500 rounded-full flex items-center justify-center text-white text-4xl font-bold">
                                        {user.username?.charAt(0).toUpperCase() || 'U'}
                                    </div>
                                </div>
                                
                                {/* Informacje podstawowe */}
                                <div className="flex-1">
                                    <div className="flex justify-between items-start mb-4">
                                        <div>
                                            <h2 className="text-2xl font-bold">{user.username}</h2>
                                            <p className="text-gray-400">{user.email}</p>
                                            <div className="flex items-center gap-2 mt-2">
                                                <span className={`px-3 py-1 rounded-full text-sm font-medium ${
                                                    user.role === 'Artist' 
                                                        ? 'bg-purple-900/50 text-purple-300' 
                                                        : 'bg-blue-900/50 text-blue-300'
                                                }`}>
                                                    {user.role === 'Artist' ? '🎤 Artist' : '👂 Listener'}
                                                </span>
                                                <span className="text-gray-500">•</span>
                                                <span className="text-gray-400 text-sm">
                                                    Joined {formatDate(stats.joinedDate)}
                                                </span>
                                            </div>
                                        </div>
                                        
                                        {/* Przyciski akcji */}
                                        <div className="flex gap-2">
                                            <button
                                                onClick={() => setIsEditing(!isEditing)}
                                                className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition flex items-center gap-2"
                                            >
                                                {isEditing ? (
                                                    <>
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                                                        </svg>
                                                        Cancel
                                                    </>
                                                ) : (
                                                    <>
                                                        <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                                        </svg>
                                                        Edit Profile
                                                    </>
                                                )}
                                            </button>
                                            <button
                                                onClick={logout}
                                                className="px-4 py-2 bg-red-900/30 hover:bg-red-800/30 text-red-300 rounded-lg transition flex items-center gap-2"
                                            >
                                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                                </svg>
                                                Logout
                                            </button>
                                        </div>
                                    </div>
                                    
                                    {/* Bio */}
                                    {user.bio && !isEditing && (
                                        <div className="mb-6">
                                            <p className="text-gray-300">{user.bio}</p>
                                        </div>
                                    )}
                                    
                                    {/* Lokalizacja i strona */}
                                    <div className="flex flex-wrap gap-4">
                                        {user.location && !isEditing && (
                                            <div className="flex items-center gap-2 text-gray-400">
                                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z" />
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 11a3 3 0 11-6 0 3 3 0 016 0z" />
                                                </svg>
                                                <span>{user.location}</span>
                                            </div>
                                        )}
                                        
                                        {user.website && !isEditing && (
                                            <div className="flex items-center gap-2 text-gray-400">
                                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M21 12a9 9 0 01-9 9m9-9a9 9 0 00-9-9m9 9H3m9 9a9 9 0 01-9-9m9 9c1.657 0 3-4.03 3-9s-1.343-9-3-9m0 18c-1.657 0-3-4.03-3-9s1.343-9 3-9m-9 9a9 9 0 019-9" />
                                                </svg>
                                                <a 
                                                    href={user.website.startsWith('http') ? user.website : `https://${user.website}`}
                                                    target="_blank"
                                                    rel="noopener noreferrer"
                                                    className="text-purple-400 hover:text-purple-300"
                                                >
                                                    {user.website.replace(/^https?:\/\//, '')}
                                                </a>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </div>

                        {/* Formularz edycji */}
                        {isEditing && (
                            <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-2xl p-6">
                                <h3 className="text-xl font-bold mb-6">Edit Profile</h3>
                                <form onSubmit={handleSaveProfile}>
                                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-6">
                                        {/* Username */}
                                        <div>
                                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                                Username
                                            </label>
                                            <input
                                                type="text"
                                                name="username"
                                                value={profileData.username}
                                                onChange={handleInputChange}
                                                className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                placeholder="Enter username"
                                            />
                                        </div>
                                        
                                        {/* Email */}
                                        <div>
                                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                                Email
                                            </label>
                                            <input
                                                type="email"
                                                name="email"
                                                value={profileData.email}
                                                onChange={handleInputChange}
                                                className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                placeholder="Enter email"
                                            />
                                        </div>
                                        
                                        {/* Bio */}
                                        <div className="md:col-span-2">
                                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                                Bio
                                            </label>
                                            <textarea
                                                name="bio"
                                                value={profileData.bio}
                                                onChange={handleInputChange}
                                                rows="3"
                                                className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600 resize-none"
                                                placeholder="Tell us about yourself..."
                                            />
                                        </div>
                                        
                                        {/* Location */}
                                        <div>
                                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                                Location
                                            </label>
                                            <input
                                                type="text"
                                                name="location"
                                                value={profileData.location}
                                                onChange={handleInputChange}
                                                className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                placeholder="City, Country"
                                            />
                                        </div>
                                        
                                        {/* Website */}
                                        <div>
                                            <label className="block text-sm font-medium text-gray-300 mb-2">
                                                Website
                                            </label>
                                            <input
                                                type="url"
                                                name="website"
                                                value={profileData.website}
                                                onChange={handleInputChange}
                                                className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                placeholder="https://example.com"
                                            />
                                        </div>
                                    </div>
                                    
                                    {/* Social Links */}
                                    <div className="mb-8">
                                        <h4 className="text-lg font-medium mb-4">Social Links</h4>
                                        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                                            <div>
                                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                                    Twitter
                                                </label>
                                                <div className="flex">
                                                    <span className="px-3 py-3 bg-gray-800 border border-r-0 border-gray-700 rounded-l-lg text-gray-400">
                                                        @
                                                    </span>
                                                    <input
                                                        type="text"
                                                        name="social.twitter"
                                                        value={profileData.socialLinks.twitter}
                                                        onChange={handleInputChange}
                                                        className="flex-1 px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-r-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                        placeholder="username"
                                                    />
                                                </div>
                                            </div>
                                            
                                            <div>
                                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                                    Instagram
                                                </label>
                                                <div className="flex">
                                                    <span className="px-3 py-3 bg-gray-800 border border-r-0 border-gray-700 rounded-l-lg text-gray-400">
                                                        @
                                                    </span>
                                                    <input
                                                        type="text"
                                                        name="social.instagram"
                                                        value={profileData.socialLinks.instagram}
                                                        onChange={handleInputChange}
                                                        className="flex-1 px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-r-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                        placeholder="username"
                                                    />
                                                </div>
                                            </div>
                                            
                                            <div>
                                                <label className="block text-sm font-medium text-gray-300 mb-2">
                                                    Spotify
                                                </label>
                                                <input
                                                    type="text"
                                                    name="social.spotify"
                                                    value={profileData.socialLinks.spotify}
                                                    onChange={handleInputChange}
                                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                                    placeholder="Spotify Artist ID"
                                                />
                                            </div>
                                        </div>
                                    </div>
                                    
                                    {/* Przyciski formularza */}
                                    <div className="flex justify-end gap-4">
                                        <button
                                            type="button"
                                            onClick={() => setIsEditing(false)}
                                            className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition"
                                        >
                                            Cancel
                                        </button>
                                        <button
                                            type="submit"
                                            disabled={updating}
                                            className="px-6 py-3 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-lg transition disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
                                        >
                                            {updating ? (
                                                <>
                                                    <div className="animate-spin rounded-full h-5 w-5 border-2 border-white border-t-transparent"></div>
                                                    Saving...
                                                </>
                                            ) : (
                                                'Save Changes'
                                            )}
                                        </button>
                                    </div>
                                </form>
                            </div>
                        )}

                        {/* Statystyki */}
                        <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-2xl p-6">
                            <h3 className="text-xl font-bold mb-6">Statistics</h3>
                            {loading ? (
                                <div className="flex justify-center py-8">
                                    <div className="animate-spin rounded-full h-8 w-8 border-2 border-purple-600 border-t-transparent"></div>
                                </div>
                            ) : (
                                <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                                    <div className="bg-gray-900/50 rounded-lg p-4">
                                        <p className="text-gray-400 text-sm mb-1">Total Tracks</p>
                                        <p className="text-2xl font-bold">{stats.totalTracks}</p>
                                    </div>
                                    <div className="bg-gray-900/50 rounded-lg p-4">
                                        <p className="text-gray-400 text-sm mb-1">Total Likes</p>
                                        <p className="text-2xl font-bold">{stats.totalLikes}</p>
                                    </div>
                                    <div className="bg-gray-900/50 rounded-lg p-4">
                                        <p className="text-gray-400 text-sm mb-1">Total Plays</p>
                                        <p className="text-2xl font-bold">{stats.totalPlays}</p>
                                    </div>
                                    <div className="bg-gray-900/50 rounded-lg p-4">
                                        <p className="text-gray-400 text-sm mb-1">Member For</p>
                                        <p className="text-2xl font-bold">{calculateTimeSinceJoin(stats.joinedDate)}</p>
                                    </div>
                                </div>
                            )}
                            
                            {/* Top Genres */}
                            {stats.topGenres.length > 0 && (
                                <div className="mt-6">
                                    <h4 className="text-lg font-medium mb-4">Top Genres</h4>
                                    <div className="flex flex-wrap gap-2">
                                        {stats.topGenres.map((genre, index) => (
                                            <span 
                                                key={genre}
                                                className="px-4 py-2 bg-gradient-to-r from-purple-900/30 to-pink-900/30 text-purple-300 rounded-full text-sm font-medium"
                                            >
                                                {genre} {index === 0 && '👑'}
                                            </span>
                                        ))}
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>

                    {/* Prawa kolumna - Akcje i ustawienia */}
                    <div className="space-y-8">
                        {/* Quick Actions */}
                        <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-2xl p-6">
                            <h3 className="text-xl font-bold mb-6">Quick Actions</h3>
                            <div className="space-y-3">
                                <button
                                    onClick={() => navigate('/upload')}
                                    className="w-full flex items-center gap-3 px-4 py-3 bg-gray-800 hover:bg-gray-700 rounded-lg transition text-left"
                                >
                                    <div className="w-10 h-10 bg-purple-900/30 rounded-lg flex items-center justify-center">
                                        <svg className="w-5 h-5 text-purple-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                                        </svg>
                                    </div>
                                    <div>
                                        <p className="font-medium">Upload Track</p>
                                        <p className="text-sm text-gray-400">Share your music</p>
                                    </div>
                                </button>
                                
                                <button
                                    onClick={() => navigate('/library')}
                                    className="w-full flex items-center gap-3 px-4 py-3 bg-gray-800 hover:bg-gray-700 rounded-lg transition text-left"
                                >
                                    <div className="w-10 h-10 bg-blue-900/30 rounded-lg flex items-center justify-center">
                                        <svg className="w-5 h-5 text-blue-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7v8a2 2 0 002 2h6M8 7V5a2 2 0 012-2h4.586a1 1 0 01.707.293l4.414 4.414a1 1 0 01.293.707V15a2 2 0 01-2 2h-2M8 7H6a2 2 0 00-2 2v10a2 2 0 002 2h8a2 2 0 002-2v-2" />
                                        </svg>
                                    </div>
                                    <div>
                                        <p className="font-medium">View Library</p>
                                        <p className="text-sm text-gray-400">Your music collection</p>
                                    </div>
                                </button>
                                
                                {user.role === 'Artist' && (
                                    <button
                                        onClick={() => navigate('/artist/dashboard')}
                                        className="w-full flex items-center gap-3 px-4 py-3 bg-gray-800 hover:bg-gray-700 rounded-lg transition text-left"
                                    >
                                        <div className="w-10 h-10 bg-pink-900/30 rounded-lg flex items-center justify-center">
                                            <svg className="w-5 h-5 text-pink-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z" />
                                            </svg>
                                        </div>
                                        <div>
                                            <p className="font-medium">Artist Dashboard</p>
                                            <p className="text-sm text-gray-400">Analytics & insights</p>
                                        </div>
                                    </button>
                                )}
                            </div>
                        </div>

                        {/* Account Settings */}
                        <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-2xl p-6">
                            <h3 className="text-xl font-bold mb-6">Account Settings</h3>
                            <div className="space-y-4">
                                <button
                                    onClick={() => navigate('/settings/password')}
                                    className="w-full flex justify-between items-center px-4 py-3 bg-gray-800 hover:bg-gray-700 rounded-lg transition"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 bg-gray-700 rounded-full flex items-center justify-center">
                                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                                            </svg>
                                        </div>
                                        <span>Change Password</span>
                                    </div>
                                    <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                    </svg>
                                </button>
                                
                                <button
                                    onClick={() => navigate('/settings/notifications')}
                                    className="w-full flex justify-between items-center px-4 py-3 bg-gray-800 hover:bg-gray-700 rounded-lg transition"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 bg-gray-700 rounded-full flex items-center justify-center">
                                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9" />
                                            </svg>
                                        </div>
                                        <span>Notification Settings</span>
                                    </div>
                                    <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                    </svg>
                                </button>
                                
                                <button
                                    onClick={() => navigate('/settings/privacy')}
                                    className="w-full flex justify-between items-center px-4 py-3 bg-gray-800 hover:bg-gray-700 rounded-lg transition"
                                >
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 bg-gray-700 rounded-full flex items-center justify-center">
                                            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z" />
                                            </svg>
                                        </div>
                                        <span>Privacy & Security</span>
                                    </div>
                                    <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                                    </svg>
                                </button>
                            </div>
                        </div>

                        {/* Danger Zone */}
                        <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-2xl p-6 border border-red-900/30">
                            <h3 className="text-xl font-bold mb-4 text-red-300">Danger Zone</h3>
                            <p className="text-gray-400 text-sm mb-6">
                                These actions are irreversible. Please proceed with caution.
                            </p>
                            <div className="space-y-3">
                                <button
                                    onClick={() => {/* Tutaj implementacja deaktywacji konta */}}
                                    className="w-full px-4 py-3 bg-red-900/20 hover:bg-red-800/30 text-red-300 rounded-lg transition text-left border border-red-800/30"
                                >
                                    Deactivate Account
                                </button>
                                <button
                                    onClick={() => {
                                        if (window.confirm('Are you sure you want to delete your account? This action cannot be undone.')) {
                                            // Tutaj implementacja usuwania konta
                                        }
                                    }}
                                    className="w-full px-4 py-3 bg-red-900/30 hover:bg-red-800/40 text-red-400 rounded-lg transition text-left border border-red-800/50"
                                >
                                    Delete Account
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export default Profile;
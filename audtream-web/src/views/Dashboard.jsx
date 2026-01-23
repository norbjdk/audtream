import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Dashboard() {
    const { user } = useAuth();
    const navigate = useNavigate();

    const [stats, setStats] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchDashboardData = async () => {
            try {
                setLoading(true);
                setError('');

                const token = localStorage.getItem('token');
                if (!token) {
                    navigate('/login');
                    return;
                }

                console.log('Fetching dashboard stats...');
                const response = await fetch('http://localhost:8080/api/users/dashboard/stats', {
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Accept': 'application/json'
                    }
                });

                console.log('Response status:', response.status);

                if (!response.ok) {
                    throw new Error(`Server error: ${response.status}`);
                }

                const data = await response.json();
                console.log('Dashboard data:', data);
                setStats(data);

            } catch (err) {
                console.error('Dashboard error:', err);
                setError('Connection failed. Using demo data.');

                // Mock data
                setTimeout(() => {
                    setStats({
                        totalTracks: 8,
                        totalPlays: 1245,
                        totalLikes: 89,
                        topGenre: "Electronic",
                        averageDuration: 245,
                        mostPlayedTrack: "Midnight Dreams",
                        mostPlayedTrackPlays: 567
                    });
                    setLoading(false);
                }, 500);
            } finally {
                setLoading(false);
            }
        };

        fetchDashboardData();
    }, [navigate]);

    const formatDuration = (seconds) => {
        if (!seconds) return '0:00';
        const mins = Math.floor(seconds / 60);
        const secs = Math.floor(seconds % 60);
        return `${mins}:${secs.toString().padStart(2, '0')}`;
    };

    if (loading) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-8">
                <div className="max-w-6xl mx-auto">
                    <div className="bg-gray-800/50 rounded-2xl p-8 text-center">
                        <div className="animate-spin rounded-full h-12 w-12 border-4 border-purple-500 border-t-transparent mx-auto mb-4"></div>
                        <h2 className="text-xl font-bold">Loading Dashboard...</h2>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-4 md:p-6">
            <div className="max-w-6xl mx-auto">
                {/* Header */}
                <div className="mb-8">
                    <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-6">
                        <div className="flex items-center gap-3">
                            <div className="w-10 h-10 bg-gradient-to-r from-purple-600 to-pink-600 rounded-lg flex items-center justify-center">
                                <span className="text-xl">📊</span>
                            </div>
                            <div>
                                <h1 className="text-2xl md:text-3xl font-bold">Artist Dashboard</h1>
                                <p className="text-gray-400">Welcome, {user?.username || 'Artist'}</p>
                            </div>
                        </div>

                        <div className="flex gap-2">
                            <button
                                onClick={() => window.location.reload()}
                                className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition"
                            >
                                Refresh
                            </button>
                            <button
                                onClick={() => navigate('/upload')}
                                className="px-4 py-2 bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 rounded-lg transition"
                            >
                                Upload Track
                            </button>
                        </div>
                    </div>

                    {error && (
                        <div className="mb-4 p-3 bg-yellow-900/30 rounded-lg text-sm">
                            <p className="text-yellow-300">{error}</p>
                        </div>
                    )}
                </div>

                {/* Main Stats */}
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
                    <div className="bg-gray-800/50 rounded-xl p-5">
                        <div className="flex justify-between items-center mb-2">
                            <h3 className="text-gray-400 text-sm">Total Tracks</h3>
                            <span className="text-xl">🎵</span>
                        </div>
                        <p className="text-3xl font-bold">{stats?.totalTracks || 0}</p>
                    </div>

                    <div className="bg-gray-800/50 rounded-xl p-5">
                        <div className="flex justify-between items-center mb-2">
                            <h3 className="text-gray-400 text-sm">Total Plays</h3>
                            <span className="text-xl">▶️</span>
                        </div>
                        <p className="text-3xl font-bold">{stats?.totalPlays?.toLocaleString() || 0}</p>
                    </div>

                    <div className="bg-gray-800/50 rounded-xl p-5">
                        <div className="flex justify-between items-center mb-2">
                            <h3 className="text-gray-400 text-sm">Total Likes</h3>
                            <span className="text-xl">❤️</span>
                        </div>
                        <p className="text-3xl font-bold">{stats?.totalLikes?.toLocaleString() || 0}</p>
                    </div>

                    <div className="bg-gray-800/50 rounded-xl p-5">
                        <div className="flex justify-between items-center mb-2">
                            <h3 className="text-gray-400 text-sm">Top Genre</h3>
                            <span className="text-xl">🎷</span>
                        </div>
                        <p className="text-2xl font-bold">{stats?.topGenre || 'None'}</p>
                    </div>
                </div>

                {/* Detailed Stats */}
                <div className="grid grid-cols-1 lg:grid-cols-3 gap-6 mb-8">
                    <div className="bg-gray-800/50 rounded-xl p-6">
                        <h3 className="text-lg font-bold mb-4">Average Duration</h3>
                        <div className="text-center">
                            <p className="text-4xl font-bold mb-2">{formatDuration(stats?.averageDuration || 0)}</p>
                            <p className="text-gray-400">per track</p>
                        </div>
                    </div>

                    <div className="bg-gray-800/50 rounded-xl p-6">
                        <h3 className="text-lg font-bold mb-4">Most Popular Track</h3>
                        {stats?.mostPlayedTrack ? (
                            <div className="text-center">
                                <p className="text-xl font-bold mb-2">{stats.mostPlayedTrack}</p>
                                <p className="text-3xl font-bold">{stats.mostPlayedTrackPlays?.toLocaleString() || 0}</p>
                                <p className="text-gray-400">plays</p>
                            </div>
                        ) : (
                            <p className="text-gray-400 text-center">No tracks yet</p>
                        )}
                    </div>

                    <div className="bg-gray-800/50 rounded-xl p-6">
                        <h3 className="text-lg font-bold mb-4">Performance</h3>
                        <div className="space-y-4">
                            <div>
                                <div className="flex justify-between mb-1">
                                    <span className="text-gray-400">Plays per track</span>
                                    <span>
                                        {stats?.totalTracks > 0
                                            ? Math.round(stats.totalPlays / stats.totalTracks)
                                            : 0}
                                    </span>
                                </div>
                                <div className="h-2 bg-gray-700 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-gradient-to-r from-purple-500 to-pink-500 rounded-full"
                                        style={{
                                            width: `${Math.min(100, ((stats?.totalPlays || 0) / Math.max(stats?.totalTracks || 1, 1)) * 10)}%`
                                        }}
                                    ></div>
                                </div>
                            </div>

                            <div>
                                <div className="flex justify-between mb-1">
                                    <span className="text-gray-400">Engagement rate</span>
                                    <span>
                                        {stats?.totalPlays > 0
                                            ? `${((stats.totalLikes / stats.totalPlays) * 100).toFixed(1)}%`
                                            : '0%'}
                                    </span>
                                </div>
                                <div className="h-2 bg-gray-700 rounded-full overflow-hidden">
                                    <div
                                        className="h-full bg-gradient-to-r from-blue-500 to-cyan-500 rounded-full"
                                        style={{
                                            width: `${stats?.totalPlays > 0
                                                ? Math.min(100, ((stats.totalLikes / stats.totalPlays) * 100))
                                                : 0}%`
                                        }}
                                    ></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Quick Actions */}
                <div className="bg-gray-800/50 rounded-xl p-6 mb-8">
                    <h3 className="text-lg font-bold mb-4">Quick Actions</h3>
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
                        <button
                            onClick={() => navigate('/upload')}
                            className="p-4 bg-gradient-to-r from-purple-600/20 to-pink-600/20 hover:from-purple-600/30 hover:to-pink-600/30 rounded-lg transition border border-purple-600/20"
                        >
                            <span className="block text-xl mb-2">⬆️</span>
                            <span className="font-medium">Upload</span>
                        </button>

                        <button
                            onClick={() => navigate('/library')}
                            className="p-4 bg-gradient-to-r from-blue-600/20 to-cyan-600/20 hover:from-blue-600/30 hover:to-cyan-600/30 rounded-lg transition border border-blue-600/20"
                        >
                            <span className="block text-xl mb-2">📚</span>
                            <span className="font-medium">Library</span>
                        </button>

                        <button
                            onClick={() => navigate('/profile')}
                            className="p-4 bg-gradient-to-r from-green-600/20 to-emerald-600/20 hover:from-green-600/30 hover:to-emerald-600/30 rounded-lg transition border border-green-600/20"
                        >
                            <span className="block text-xl mb-2">👤</span>
                            <span className="font-medium">Profile</span>
                        </button>

                        <button
                            onClick={() => navigate('/settings')}
                            className="p-4 bg-gradient-to-r from-yellow-600/20 to-orange-600/20 hover:from-yellow-600/30 hover:to-orange-600/30 rounded-lg transition border border-yellow-600/20"
                        >
                            <span className="block text-xl mb-2">⚙️</span>
                            <span className="font-medium">Settings</span>
                        </button>
                    </div>
                </div>

                {/* Empty State */}
                {(!stats || stats.totalTracks === 0) && (
                    <div className="bg-gray-800/50 rounded-xl p-8 text-center">
                        <div className="text-4xl mb-4">🚀</div>
                        <h3 className="text-xl font-bold mb-4">Start Your Journey</h3>
                        <p className="text-gray-400 mb-6">
                            Upload your first track to see your statistics
                        </p>
                        <button
                            onClick={() => navigate('/upload')}
                            className="px-6 py-3 bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-500 hover:to-pink-500 rounded-lg transition font-medium"
                        >
                            Upload First Track
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboard;
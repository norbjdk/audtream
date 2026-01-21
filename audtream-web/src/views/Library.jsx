import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { tracksAPI } from '../services/api';
import LibraryIcon from "../assets/icons/library.png";

function Library() {
    const { user } = useAuth();
    const navigate = useNavigate();
    
    // State dla utworów - upewnijmy się że to zawsze array
    const [tracks, setTracks] = useState([]);
    const [filteredTracks, setFilteredTracks] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');
    
    // State dla wyszukiwania i filtrów
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedGenre, setSelectedGenre] = useState('All');
    const [sortBy, setSortBy] = useState('newest');
    const [currentPage, setCurrentPage] = useState(1);
    const itemsPerPage = 12;
    
    // Genres lista
    const genres = [
        'All', 'Rock', 'Pop', 'Hip Hop', 'Jazz', 'Electronic', 
        'R&B', 'Metal', 'Classical', 'Country', 'Folk', 'Blues',
        'Reggae', 'Punk', 'Funk', 'Soul', 'Disco', 'Techno',
        'House', 'Trance', 'Drum & Bass', 'Dubstep', 'Trap', 'Lo-fi'
    ];
    
    // Sort options
    const sortOptions = [
        { value: 'newest', label: 'Newest First' },
        { value: 'oldest', label: 'Oldest First' },
        { value: 'title-asc', label: 'Title A-Z' },
        { value: 'title-desc', label: 'Title Z-A' },
        { value: 'artist-asc', label: 'Artist A-Z' },
        { value: 'artist-desc', label: 'Artist Z-A' },
        { value: 'duration-asc', label: 'Shortest First' },
        { value: 'duration-desc', label: 'Longest First' }
    ];

    // Pobierz utwory z backendu - z lepszą obsługą błędów
    useEffect(() => {
        fetchTracks();
    }, []);

    const fetchTracks = async () => {
        try {
            setLoading(true);
            setError('');
            const response = await tracksAPI.getUserTracks();
            
            // Debug: sprawdź co zwraca backend
            console.log('API Response:', response);
            console.log('Response data:', response.data);
            
            // Upewnij się że tracks jest tablicą
            let tracksData = response.data;
            
            if (!tracksData) {
                console.warn('No data returned from API');
                tracksData = [];
            } else if (!Array.isArray(tracksData)) {
                console.warn('API did not return an array:', typeof tracksData);
                // Spróbuj przekonwertować jeśli to obiekt
                if (tracksData && typeof tracksData === 'object') {
                    tracksData = Object.values(tracksData);
                } else {
                    tracksData = [];
                }
            }
            
            setTracks(tracksData);
            setFilteredTracks(tracksData);
            
        } catch (err) {
            console.error('Error fetching tracks:', err);
            setError('Failed to load tracks: ' + (err.message || 'Unknown error'));
            setTracks([]);
            setFilteredTracks([]);
        } finally {
            setLoading(false);
        }
    };

    // Filtrowanie i sortowanie - tylko jeśli tracks jest tablicą
    useEffect(() => {
        if (!Array.isArray(tracks)) {
            setFilteredTracks([]);
            return;
        }
        
        let result = [...tracks];
        
        // Wyszukiwanie
        if (searchTerm) {
            const term = searchTerm.toLowerCase();
            result = result.filter(track => 
                track && 
                track.title && track.title.toLowerCase().includes(term) ||
                track.artist && track.artist.toLowerCase().includes(term) ||
                (track.album && track.album.toLowerCase().includes(term)) ||
                (track.genre && track.genre.toLowerCase().includes(term))
            );
        }
        
        // Filtrowanie po gatunku
        if (selectedGenre !== 'All') {
            result = result.filter(track => track && track.genre === selectedGenre);
        }
        
        // Sortowanie z bezpiecznymi wartościami
        switch (sortBy) {
            case 'newest':
                result.sort((a, b) => {
                    const dateA = a.createdAt ? new Date(a.createdAt) : new Date(0);
                    const dateB = b.createdAt ? new Date(b.createdAt) : new Date(0);
                    return dateB - dateA;
                });
                break;
            case 'oldest':
                result.sort((a, b) => {
                    const dateA = a.createdAt ? new Date(a.createdAt) : new Date(0);
                    const dateB = b.createdAt ? new Date(b.createdAt) : new Date(0);
                    return dateA - dateB;
                });
                break;
            case 'title-asc':
                result.sort((a, b) => (a.title || '').localeCompare(b.title || ''));
                break;
            case 'title-desc':
                result.sort((a, b) => (b.title || '').localeCompare(a.title || ''));
                break;
            case 'artist-asc':
                result.sort((a, b) => (a.artist || '').localeCompare(b.artist || ''));
                break;
            case 'artist-desc':
                result.sort((a, b) => (b.artist || '').localeCompare(a.artist || ''));
                break;
            case 'duration-asc':
                result.sort((a, b) => (a.duration || 0) - (b.duration || 0));
                break;
            case 'duration-desc':
                result.sort((a, b) => (b.duration || 0) - (a.duration || 0));
                break;
            default:
                break;
        }
        
        setFilteredTracks(result);
        setCurrentPage(1);
    }, [searchTerm, selectedGenre, sortBy, tracks]);

    // Paginacja - tylko jeśli filteredTracks jest tablicą
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentTracks = Array.isArray(filteredTracks) 
        ? filteredTracks.slice(indexOfFirstItem, indexOfLastItem)
        : [];
    const totalPages = Array.isArray(filteredTracks)
        ? Math.ceil(filteredTracks.length / itemsPerPage)
        : 0;

    // Obsługa usuwania utworu
    const handleDeleteTrack = async (trackId) => {
        if (!window.confirm('Are you sure you want to delete this track?')) return;
        
        try {
            await tracksAPI.deleteTrack(trackId);
            // Zaktualizuj listę lokalnie
            setTracks(tracks.filter(track => track.id !== trackId));
        } catch (err) {
            setError('Failed to delete track: ' + (err.message || 'Unknown error'));
        }
    };

    // Format czasu - z zabezpieczeniem
    const formatDuration = (seconds) => {
        if (!seconds && seconds !== 0) return '0:00';
        const secs = parseInt(seconds) || 0;
        const mins = Math.floor(secs / 60);
        const remainingSecs = Math.floor(secs % 60);
        return `${mins}:${remainingSecs.toString().padStart(2, '0')}`;
    };

    // Format daty - z zabezpieczeniem
    const formatDate = (dateString) => {
        if (!dateString) return 'Unknown date';
        try {
            const date = new Date(dateString);
            return date.toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });
        } catch (err) {
            return 'Invalid date';
        }
    };

    // Pobierz unikalne gatunki - z zabezpieczeniem
    const uniqueGenres = Array.isArray(tracks) 
        ? [...new Set(tracks.map(t => t.genre).filter(Boolean))]
        : [];

    // Oblicz całkowity czas - z zabezpieczeniem
    const totalDuration = Array.isArray(tracks)
        ? tracks.reduce((sum, track) => sum + (parseInt(track.duration) || 0), 0)
        : 0;

    // Jeśli nie ma utworów (lub tracks nie jest tablicą)
    if (!loading && (!Array.isArray(tracks) || tracks.length === 0)) {
        return (
            <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-8 mt-20 mb-20">
                <div className="max-w-4xl mx-auto">
                    <div className="p-8 md:p-12">
                        <div className="flex items-center gap-4 mb-8">
                            <img src={LibraryIcon} alt="Library" className="h-10" />
                            <h1 className="text-3xl font-bold">Your Library</h1>
                        </div>
                        
                        {error ? (
                            <div className="text-center py-16">
                                <div className="text-6xl mb-6">⚠️</div>
                                <h2 className="text-2xl font-bold mb-4">Error Loading Library</h2>
                                <p className="text-gray-400 mb-8 max-w-md mx-auto">
                                    {error}
                                </p>
                                <div className="flex gap-4 justify-center">
                                    <button
                                        onClick={fetchTracks}
                                        className="px-6 py-3 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-lg transition"
                                    >
                                        Try Again
                                    </button>
                                    <button
                                        onClick={() => navigate('/upload')}
                                        className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition"
                                    >
                                        Upload Track
                                    </button>
                                </div>
                            </div>
                        ) : (
                            <div className="text-center py-16">
                                <div className="text-6xl mb-6">📚</div>
                                <h2 className="text-2xl font-bold mb-4">Your library is empty</h2>
                                <p className="text-gray-400 mb-8 max-w-md mx-auto">
                                    {Array.isArray(tracks) 
                                        ? "You haven't added any tracks yet. Start building your music collection!"
                                        : "Unable to load tracks. Please try again or contact support."
                                    }
                                </p>
                                <div className="flex gap-4 justify-center">
                                    <button
                                        onClick={() => navigate('/upload')}
                                        className="px-6 py-3 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-lg transition"
                                    >
                                        Upload Your First Track
                                    </button>
                                    <button
                                        onClick={() => navigate('/')}
                                        className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition"
                                    >
                                        Browse Music
                                    </button>
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gradient-to-b from-gray-900 to-black text-white p-4 md:p-8 mt-20 mb-20">
            <div className="max-w-7xl mx-auto">
                {/* Nagłówek */}
                <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-6 md:p-8 mb-8">
                    <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
                        <div className="flex items-center gap-4">
                            <img src={LibraryIcon} alt="Library" className="h-10" />
                            <div>
                                <h1 className="text-3xl font-bold">Your Library</h1>
                                <p className="text-gray-400">
                                    {Array.isArray(tracks) ? tracks.length : 0} track{Array.isArray(tracks) && tracks.length !== 1 ? 's' : ''} • {user?.username || 'User'}
                                </p>
                            </div>
                        </div>
                        
                        <div className="flex gap-3">
                            <button
                                onClick={fetchTracks}
                                className="px-4 py-2 bg-gray-700 hover:bg-gray-600 rounded-lg transition flex items-center gap-2"
                                title="Refresh library"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
                                </svg>
                                Refresh
                            </button>
                            <button
                                onClick={() => navigate('/upload')}
                                className="px-6 py-3 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-lg transition flex items-center gap-2"
                            >
                                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                                </svg>
                                Upload New Track
                            </button>
                        </div>
                    </div>

                    {/* Debug info - możesz to później usunąć */}
                    {process.env.NODE_ENV === 'development' && (
                        <div className="mb-4 p-3 bg-blue-900/20 rounded-lg text-sm">
                            <p className="text-blue-300">
                                Debug: tracks is {Array.isArray(tracks) ? 'array' : typeof tracks} with {Array.isArray(tracks) ? tracks.length : 'N/A'} items
                            </p>
                        </div>
                    )}

                    {/* Wyszukiwanie */}
                    <div className="mb-8">
                        <div className="flex flex-col md:flex-row gap-4">
                            <div className="flex-1 flex">
                                <input
                                    type="text"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    placeholder="Search tracks, artists, albums..."
                                    className="w-full px-6 py-3 bg-gray-900/70 border border-gray-700 rounded-l-lg text-white placeholder-gray-500 focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                />
                                <button className="px-6 bg-gradient-to-r from-purple-700 to-pink-700 hover:from-purple-600 hover:to-pink-600 rounded-r-lg flex items-center gap-2">
                                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" viewBox="0 0 16 16">
                                        <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0"/>
                                    </svg>
                                </button>
                            </div>
                            
                            {/* Sortowanie */}
                            <div className="md:w-64">
                                <select
                                    value={sortBy}
                                    onChange={(e) => setSortBy(e.target.value)}
                                    className="w-full px-4 py-3 bg-gray-900/70 border border-gray-700 rounded-lg text-white focus:outline-none focus:border-purple-600 focus:ring-1 focus:ring-purple-600"
                                >
                                    {sortOptions.map(option => (
                                        <option key={option.value} value={option.value}>
                                            {option.label}
                                        </option>
                                    ))}
                                </select>
                            </div>
                        </div>
                    </div>

                    {/* Filtry - Gatunki */}
                    <div className="mb-6">
                        <h2 className="text-xl font-bold mb-4">Genres</h2>
                        <div className="flex flex-wrap gap-2">
                            {genres.map(genre => (
                                <button
                                    key={genre}
                                    onClick={() => setSelectedGenre(genre)}
                                    className={`px-4 py-2 rounded-full transition ${
                                        selectedGenre === genre
                                            ? 'bg-gradient-to-r from-purple-600 to-pink-600 text-white'
                                            : 'bg-gray-800 hover:bg-gray-700 text-gray-300'
                                    }`}
                                >
                                    {genre}
                                </button>
                            ))}
                        </div>
                    </div>

                    {/* Statystyki */}
                    <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-8">
                        <div className="bg-gray-900/50 rounded-lg p-4">
                            <p className="text-gray-400 text-sm">Total Tracks</p>
                            <p className="text-2xl font-bold">{Array.isArray(tracks) ? tracks.length : 0}</p>
                        </div>
                        <div className="bg-gray-900/50 rounded-lg p-4">
                            <p className="text-gray-400 text-sm">Genres</p>
                            <p className="text-2xl font-bold">{uniqueGenres.length}</p>
                        </div>
                        <div className="bg-gray-900/50 rounded-lg p-4">
                            <p className="text-gray-400 text-sm">Total Duration</p>
                            <p className="text-2xl font-bold">{formatDuration(totalDuration)}</p>
                        </div>
                        <div className="bg-gray-900/50 rounded-lg p-4">
                            <p className="text-gray-400 text-sm">Showing</p>
                            <p className="text-2xl font-bold">{Array.isArray(filteredTracks) ? filteredTracks.length : 0}</p>
                        </div>
                    </div>
                </div>

                {/* Błąd */}
                {error && (
                    <div className="mb-6 p-4 bg-red-900/30 border border-red-700 rounded-lg">
                        <p className="text-red-300">{error}</p>
                        <button
                            onClick={fetchTracks}
                            className="mt-2 text-sm text-red-300 hover:text-red-200 underline"
                        >
                            Try Again
                        </button>
                    </div>
                )}

                {/* Lista utworów */}
                <div className="bg-gradient-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-6">
                    {loading ? (
                        <div className="flex justify-center py-16">
                            <div className="animate-spin rounded-full h-12 w-12 border-4 border-purple-600 border-t-transparent"></div>
                        </div>
                    ) : (
                        <>
                            {/* Tabela utworów - tylko jeśli mamy dane */}
                            {Array.isArray(currentTracks) && currentTracks.length > 0 ? (
                                <>
                                    <div className="overflow-x-auto">
                                        <table className="w-full">
                                            <thead>
                                                <tr className="border-b border-gray-800">
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">#</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Title</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Artist</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Album</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Genre</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Duration</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Added</th>
                                                    <th className="text-left py-4 px-4 text-gray-400 font-medium">Actions</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                {currentTracks.map((track, index) => (
                                                    <tr 
                                                        key={track.id || index} 
                                                        className="border-b border-gray-800/50 hover:bg-gray-800/30 transition-colors"
                                                    >
                                                        <td className="py-4 px-4">
                                                            <span className="text-gray-400">{indexOfFirstItem + index + 1}</span>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <div className="flex items-center gap-3">
                                                                <div className="w-10 h-10 bg-gradient-to-br from-purple-900/30 to-pink-900/30 rounded flex items-center justify-center">
                                                                    <span className="text-lg">🎵</span>
                                                                </div>
                                                                <div>
                                                                    <p className="font-medium">{track.title || 'Untitled'}</p>
                                                                    {track.description && (
                                                                        <p className="text-sm text-gray-400 truncate max-w-xs">
                                                                            {track.description}
                                                                        </p>
                                                                    )}
                                                                </div>
                                                            </div>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <p className="text-gray-300">{track.artist || 'Unknown Artist'}</p>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <p className="text-gray-400">{track.album || '-'}</p>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <span className="inline-block px-3 py-1 bg-purple-900/30 text-purple-300 rounded-full text-sm">
                                                                {track.genre || 'Unknown'}
                                                            </span>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <p className="text-gray-400">{formatDuration(track.duration)}</p>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <p className="text-gray-400 text-sm">{formatDate(track.createdAt)}</p>
                                                        </td>
                                                        <td className="py-4 px-4">
                                                            <div className="flex items-center gap-2">
                                                                <button
                                                                    onClick={() => navigate(`/track/${track.id}`)}
                                                                    className="p-2 text-gray-400 hover:text-white hover:bg-gray-800 rounded transition"
                                                                    title="View details"
                                                                >
                                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />
                                                                    </svg>
                                                                </button>
                                                                <button
                                                                    onClick={() => track.id && navigate(`/track/${track.id}/edit`)}
                                                                    className="p-2 text-gray-400 hover:text-blue-400 hover:bg-gray-800 rounded transition"
                                                                    title="Edit"
                                                                    disabled={!track.id}
                                                                >
                                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z" />
                                                                    </svg>
                                                                </button>
                                                                <button
                                                                    onClick={() => track.id && handleDeleteTrack(track.id)}
                                                                    className="p-2 text-gray-400 hover:text-red-400 hover:bg-gray-800 rounded transition"
                                                                    title="Delete"
                                                                    disabled={!track.id}
                                                                >
                                                                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                                                                    </svg>
                                                                </button>
                                                            </div>
                                                        </td>
                                                    </tr>
                                                ))}
                                            </tbody>
                                        </table>
                                    </div>

                                    {/* Paginacja */}
                                    {totalPages > 1 && (
                                        <div className="flex justify-center items-center gap-2 mt-8">
                                            <button
                                                onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                                                disabled={currentPage === 1}
                                                className="px-4 py-2 bg-gray-800 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-700 transition"
                                            >
                                                Previous
                                            </button>
                                            
                                            {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                                                let pageNum;
                                                if (totalPages <= 5) {
                                                    pageNum = i + 1;
                                                } else if (currentPage <= 3) {
                                                    pageNum = i + 1;
                                                } else if (currentPage >= totalPages - 2) {
                                                    pageNum = totalPages - 4 + i;
                                                } else {
                                                    pageNum = currentPage - 2 + i;
                                                }
                                                
                                                return (
                                                    <button
                                                        key={pageNum}
                                                        onClick={() => setCurrentPage(pageNum)}
                                                        className={`w-10 h-10 rounded-lg transition ${
                                                            currentPage === pageNum
                                                                ? 'bg-gradient-to-r from-purple-600 to-pink-600'
                                                                : 'bg-gray-800 hover:bg-gray-700'
                                                        }`}
                                                    >
                                                        {pageNum}
                                                    </button>
                                                );
                                            })}
                                            
                                            <button
                                                onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                                                disabled={currentPage === totalPages}
                                                className="px-4 py-2 bg-gray-800 rounded-lg disabled:opacity-50 disabled:cursor-not-allowed hover:bg-gray-700 transition"
                                            >
                                                Next
                                            </button>
                                        </div>
                                    )}

                                    {/* Informacja o wynikach */}
                                    <div className="mt-6 text-center text-gray-400">
                                        <p>
                                            Showing {indexOfFirstItem + 1}-{Math.min(indexOfLastItem, filteredTracks.length)} of {filteredTracks.length} tracks
                                            {searchTerm && ` for "${searchTerm}"`}
                                            {selectedGenre !== 'All' && ` in ${selectedGenre}`}
                                        </p>
                                    </div>
                                </>
                            ) : (
                                <div className="text-center py-16">
                                    <div className="text-6xl mb-6">🔍</div>
                                    <h3 className="text-xl font-bold mb-4">No tracks found</h3>
                                    <p className="text-gray-400 mb-6">
                                        {searchTerm || selectedGenre !== 'All' 
                                            ? 'Try changing your search or filters'
                                            : 'Upload your first track to get started'
                                        }
                                    </p>
                                    <button
                                        onClick={() => {
                                            setSearchTerm('');
                                            setSelectedGenre('All');
                                        }}
                                        className="px-6 py-3 bg-gray-700 hover:bg-gray-600 rounded-lg transition"
                                    >
                                        Clear Filters
                                    </button>
                                </div>
                            )}
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Library;
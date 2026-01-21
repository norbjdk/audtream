import { Link, useNavigate } from "react-router-dom";
import Logo from "../assets/images/logo.png";
import HamburgerMenu from "../assets/icons/hamburger-menu.svg";
import Search from "../components/Search";
import { useAuth } from "../context/AuthContext";
import { useState, useEffect } from "react";

function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

    const handleLogout = () => {
        logout();
        setIsDropdownOpen(false);
    };

    const closeDropdown = () => {
        setIsDropdownOpen(false);
    };

    // Zamknij dropdown kiedy klikniesz poza nim
    useEffect(() => {
        const handleClickOutside = (event) => {
            if (!event.target.closest('.dropdown-container')) {
                setIsDropdownOpen(false);
            }
        };

        document.addEventListener('click', handleClickOutside);
        return () => document.removeEventListener('click', handleClickOutside);
    }, []);

    return( 
        <nav className="bg-[#0a0a0a] shadow-md py-4 font-mono w-full h-auto fixed top-0 z-50">
            {/* Desktop */}
            <div className="container mx-auto hidden md:grid grid-cols-3 items-center text-sm">
                {/* LEFT */}
                <div className="flex items-center gap-8">
                    <Link to="/library" className="text-white hover:text-gray-300 transition">
                        Library
                    </Link>
                    <Search />
                </div>
                {/* CENTER */}
                <div className="flex justify-center">
                    <Link to="/">
                        <img src={Logo} alt="Logo" className="h-16" />
                    </Link>
                </div>
                {/* RIGHT */}
                <div className="flex justify-end items-center gap-4 2xl:gap-6">
                    <label className="font-bold bg-gradient-to-r from-green-500 to-red-600 bg-clip-text text-transparent">
                        <span className="text-white">🎄 </span>Merry Christmas<span className="text-white"> ☃️</span>
                    </label>
                    <Link to="/download" className="text-white hover:text-gray-300">Download</Link>
                    <Link to="/support" className="text-white hover:text-gray-300">Support</Link>
                    
                    {/* Dropdown dla zalogowanego użytkownika */}
                    {user ? (
                        <div className="relative dropdown-container">
                            <button 
                                onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                className="flex items-center gap-2 text-white hover:text-gray-300 focus:outline-none"
                            >
                                <div className="flex items-center gap-2">
                                    <div className="w-8 h-8 bg-gradient-to-r from-purple-600 to-pink-600 rounded-full flex items-center justify-center text-white font-bold">
                                        {user.username.charAt(0).toUpperCase()}
                                    </div>
                                    <span className="font-medium">{user.username}</span>
                                    <svg 
                                        className={`w-4 h-4 transition-transform ${isDropdownOpen ? 'rotate-180' : ''}`}
                                        fill="none" 
                                        stroke="currentColor" 
                                        viewBox="0 0 24 24"
                                    >
                                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
                                    </svg>
                                </div>
                            </button>
                            
                            {/* Dropdown menu */}
                            {isDropdownOpen && (
                                <div className="absolute right-0 mt-2 w-48 bg-[#1a1a1a] border border-gray-800 rounded-lg shadow-xl py-2 z-50">
                                    {/* Nagłówek z danymi użytkownika */}
                                    <div className="px-4 py-3 border-b border-gray-800">
                                        <p className="text-white font-medium">{user.username}</p>
                                        <p className="text-gray-400 text-sm">{user.email}</p>
                                        <p className="text-purple-400 text-xs mt-1">
                                            {user.role === 'Artist' ? '🎤 Artist' : '👂 Listener'}
                                        </p>
                                    </div>
                                    
                                    {/* Linki */}
                                    <Link 
                                        to="/profile" 
                                        onClick={closeDropdown}
                                        className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                    >
                                        <svg className="w-5 h-5 mr-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z" />
                                        </svg>
                                        Profile
                                    </Link>
                                    
                                    {user.role === 'Artist' && (
                                        <Link 
                                            to="/upload" 
                                            onClick={closeDropdown}
                                            className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                        >
                                            <svg className="w-5 h-5 mr-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                                            </svg>
                                            Upload Music
                                        </Link>
                                    )}
                                    
                                    <Link 
                                        to="/settings" 
                                        onClick={closeDropdown}
                                        className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                    >
                                        <svg className="w-5 h-5 mr-3 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                                        </svg>
                                        Settings
                                    </Link>
                                    
                                    <div className="border-t border-gray-800 my-2"></div>
                                    
                                    <button
                                        onClick={handleLogout}
                                        className="flex items-center w-full text-left px-4 py-3 text-red-400 hover:bg-[#2a2a2a] transition"
                                    >
                                        <svg className="w-5 h-5 mr-3" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1" />
                                        </svg>
                                        Logout
                                    </button>
                                </div>
                            )}
                        </div>
                    ) : (
                        /* Dla niezalogowanych użytkowników */
                        <div className="relative group">
                            <Link to="/login" className="text-white hover:text-gray-300">Account</Link>
                            <div className="absolute bg-[#1a1a1a] hidden group-hover:flex flex-col gap-3 top-full -left-5 px-5 py-5 rounded-b-lg border border-gray-800">
                                <Link to="/login" className="text-white hover:text-gray-300">Login</Link>
                                <div className="bg-purple-600 h-px w-full"></div>
                                <Link to="/register" className="text-white hover:text-gray-300">Register</Link>
                            </div>
                        </div>
                    )}
                </div>
            </div>
            
            {/* Mobile */}
            <div className="container mx-auto md:hidden flex flex-row items-center text-sm justify-between px-4">
                {/* LEFT */}
                <div>
                    <Link to="/">
                        <img src={Logo} alt="Logo" className="h-16" />
                    </Link>
                </div>
                
                {/* RIGHT - Mobile menu */}
                <div className="relative dropdown-container">
                    <button
                        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
                        className="p-2"
                    >
                        <img src={HamburgerMenu} alt="Hamburger Menu" className="h-10" />
                    </button>
                    
                    {/* Mobile dropdown menu */}
                    {isMobileMenuOpen && (
                        <div className="absolute right-0 mt-2 w-64 bg-[#1a1a1a] border border-gray-800 rounded-lg shadow-xl py-2 z-50">
                            {/* Jeśli zalogowany - pokaż dane użytkownika */}
                            {user && (
                                <div className="px-4 py-3 border-b border-gray-800">
                                    <div className="flex items-center gap-3">
                                        <div className="w-10 h-10 bg-gradient-to-r from-purple-600 to-pink-600 rounded-full flex items-center justify-center text-white font-bold text-lg">
                                            {user.username.charAt(0).toUpperCase()}
                                        </div>
                                        <div>
                                            <p className="text-white font-medium">{user.username}</p>
                                            <p className="text-gray-400 text-sm">{user.email}</p>
                                        </div>
                                    </div>
                                </div>
                            )}
                            
                            {/* Linki mobilne */}
                            <Link 
                                to="/library" 
                                onClick={() => setIsMobileMenuOpen(false)}
                                className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                            >
                                📚 Library
                            </Link>
                            <Link 
                                to="/download" 
                                onClick={() => setIsMobileMenuOpen(false)}
                                className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                            >
                                ⬇️ Download
                            </Link>
                            <Link 
                                to="/artists" 
                                onClick={() => setIsMobileMenuOpen(false)}
                                className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                            >
                                🎤 Artists
                            </Link>
                            <Link 
                                to="/support" 
                                onClick={() => setIsMobileMenuOpen(false)}
                                className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                            >
                                ❓ Support
                            </Link>
                            
                            {/* Jeśli zalogowany - dodatkowe opcje */}
                            {user && user.role === 'Artist' && (
                                <Link 
                                    to="/upload" 
                                    onClick={() => setIsMobileMenuOpen(false)}
                                    className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                >
                                    🎵 Upload Music
                                </Link>
                            )}
                            
                            {/* Jeśli zalogowany - profil i logout */}
                            {user ? (
                                <>
                                    <div className="border-t border-gray-800 my-2"></div>
                                    <Link 
                                        to="/profile" 
                                        onClick={() => setIsMobileMenuOpen(false)}
                                        className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                    >
                                        👤 Profile
                                    </Link>
                                    <button
                                        onClick={handleLogout}
                                        className="flex items-center w-full text-left px-4 py-3 text-red-400 hover:bg-[#2a2a2a] transition"
                                    >
                                        🚪 Logout
                                    </button>
                                </>
                            ) : (
                                <>
                                    <div className="border-t border-gray-800 my-2"></div>
                                    <Link 
                                        to="/login" 
                                        onClick={() => setIsMobileMenuOpen(false)}
                                        className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                    >
                                        🔑 Login
                                    </Link>
                                    <Link 
                                        to="/register" 
                                        onClick={() => setIsMobileMenuOpen(false)}
                                        className="flex items-center px-4 py-3 text-white hover:bg-[#2a2a2a] transition"
                                    >
                                        ✍️ Register
                                    </Link>
                                </>
                            )}
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
}

export default Navbar;
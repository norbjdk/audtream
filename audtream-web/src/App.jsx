import "./styles/global.css";
import { Routes, Route } from "react-router-dom"; // Usuń BrowserRouter stąd
import { AuthProvider } from './context/AuthContext';
import MainLayout from "./layouts/MainLayout";
import Library from "./views/Library";
import Home from "./views/Home";
import Login from "./views/Login";
import Register from "./views/Register";
import Profile from "./views/Profile";
import Song from "./views/Song";
import Download from "./views/Download";
import PrivateRoute from "./components/PrivateRoute";
import Upload from "./views/Upload";

function App() {
    return (
        <AuthProvider>
            <Routes>
                <Route element={<MainLayout />}>
                    <Route path="/" element={<Home />} />
                    <Route path="/library" element={<Library />} />
                    <Route path="/download" element={<Download />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/track/:id" element={<Song />} />
                    <Route path="/profile" element={<PrivateRoute><Profile /></PrivateRoute>} />
                    <Route 
                        path="/upload" 
                        element={
                            <PrivateRoute>
                                <Upload />
                            </PrivateRoute>
                        } 
                    />
                </Route>
            </Routes>
        </AuthProvider>
    );
}

export default App;
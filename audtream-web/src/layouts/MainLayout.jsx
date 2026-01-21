import { Outlet } from "react-router-dom";
import Footer from "../components/Footer";
import Navbar from "../components/Navbar";
import Background from "../assets/images/brick-wall.svg"

function MainLayout() {
    return (
        <div className="flex flex-col items-center min-h-screen bg-[#212121] justify-center">
            <Navbar />
            <main className="container mt-15 2xl:min-h-screen">
                <Outlet />
            </main>
            <Footer />
        </div>
    );
}

export default MainLayout;
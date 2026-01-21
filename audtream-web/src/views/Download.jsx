import AudtreamApp from "../assets/images/audtream-app.png";
import WindowsIcon from "../assets/icons/windows.svg";
import MacOSIcon from "../assets/icons/macos.svg";
import { Link } from "react-router-dom";

function Download() {
    return(
        <div className="container bg-linear-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-8 md:p-12 mb-10 flex flex-col gap-3 mt-25 w-full">
            <div className="px-6">
              <span className="text-2xl md:text-3xl font-extrabold leading-tight text-white">Download</span>
            </div>
            <div className="flex flex-row justify-between gap-5 w-auto p-3">
              <div className="flex-2">
                <img src={ AudtreamApp } alt="AudTream Application View" className="mask-radial-[100%_100%] mask-radial-from-75% mask-radial-at-left rounded-lg" />
              </div>
              <div className="flex-1 bg-linear-to-br from-[#292828] to-[#1d1c1c] rounded-2xl p-8 md:p-12 mb-10 flex flex-col gap-3 items-center w-auto">
                <span className="text-gray-300 font-bold text-md text-mono text-left">Full experience start here...</span>
                <span className="text-white font-extrabold text-2xl text-left">Our <span className="text-purple-400">Desktop App</span> is at your fingertips.</span>
                <div className="flex-1 flex flex-col p-3 w-full items-center justify-center gap-10">
                    <Link className="w-full block group transform hover:bg-stone-700 transition-all duration-300 p-3 rounded-md">
                        <div className="flex flex-row gap-4 items-center justify-between w-full">
                            <span className="text-white font-sans text-lg">Download for <br/> Windows 11</span>
                            <img src={ WindowsIcon } alt="Windows" />
                        </div>
                    </Link>
                    <div className="w-full h-1 bg-purple-400"></div>
                    <Link className="w-full block group transform hover:bg-stone-700 transition-all duration-300 p-3 rounded-md">
                        <div className="flex flex-row gap-4 items-center justify-between w-full">
                            <span className="text-white font-sans text-lg">Download for <br/> MacOS</span>
                            <img src={ MacOSIcon } alt="MacOS" />
                        </div>
                    </Link>
              </div>
              </div>
            </div>
        </div>
    );
}

export default Download;
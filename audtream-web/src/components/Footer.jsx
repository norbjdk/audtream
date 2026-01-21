import { Link } from "react-router-dom";
import FacebookIcon from "../assets/icons/socials/facebook.svg";
import InstagramIcon from "../assets/icons/socials/instagram.svg";
import XIcon from "../assets/icons/socials/x.svg";
import Logo from "../assets/icons/logo.png";

function Footer() {
    return(
        <footer className="bg-[rgb(10,10,10)] py-6 text-center text-sm text-gray-200 font-mono md:font-mono w-full">
            <div className="flex flex-col justify-center gap-4">
                <div className="flex flex-row justify-evenly">
                    <Link to="/tos">Terms of Service</Link>
                    <Link to="privacy">Privacy Policy</Link>
                </div>
                <div className="flex flex-row justify-between">
                    <div className="flex h-auto px-4">
                        <Link to="/" className="h-auto w-8">
                            <img src={ Logo } alt="AudTream" />
                        </Link>
                    </div>
                    <div className="flex flex-row justify-end gap-4 h-auto px-4">
                            <Link to="https://www.instagram.com/" className="h-auto w-8">
                                <img src={ InstagramIcon } alt="Instagram" />
                            </Link>
                            <Link to="https://www.facebook.com/" className="h-auto w-8">
                                <img src={ FacebookIcon } alt="Facebook" />
                            </Link>
                            <Link to="https://x.com/" className="h-auto w-8">
                                <img src={ XIcon } alt="X" />
                            </Link>
                    </div>
                </div>
                <div>
                    <p>&#169; AudTream</p>
                </div>
            </div>
        </footer>
    );
}

export default Footer;
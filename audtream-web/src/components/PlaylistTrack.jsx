import { Link } from "react-router-dom";
import formatTime from "../utils/formatTime.js";

function PlaylistTrack({id, title, length, listened, link}) {
    return(
        <Link to={`/track/${id}`} className="block group">
            <div className="bg-[#131111] py-4 rounded-xl transform hover:shadow-purple-800 hover:shadow-xl/30 transition-all duration-300">
                <div className="flex flex-row px-3">
                    <span className="flex-2">
                        { title }
                    </span>
                    <span className="flex-1">
                        { listened }
                    </span>
                    <span className="">
                        { formatTime(length) }
                    </span>
                </div>
            </div>
        </Link>
    );
}

export default PlaylistTrack;
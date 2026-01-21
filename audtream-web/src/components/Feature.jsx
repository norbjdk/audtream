import { Link } from "react-router-dom";

function Feature({ title, author, link, cover }) {
    return (
        <Link to={link} className="block group">
            <div className="flex flex-col bg-[#020508] rounded-xl overflow-hidden shadow-md hover:shadow-xl transition-all duration-300 hover:-translate-y-1 h-full border border-gray-100 dark:border-gray-700">
                <div className="relative pb-[100%] overflow-hidden bg-gray-100 dark:bg-gray-700">
                    <img 
                        src={cover} 
                        alt={`${title} - ${author}`}
                        className="absolute w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                    />
                </div>
                <div className="p-4 grow">
                    <h3 className="font-bold text-white text-base truncate mb-1">
                        {title}
                    </h3>
                    <p className="text-gray-300 text-sm truncate">
                        {author}
                    </p>
                </div>
                <div className="px-4 pb-3">
                    <div className="h-1 w-8 bg-purple-600 rounded-full transform group-hover:w-full transition-all duration-300"></div>
                </div>
            </div>
        </Link>
    );
}

export default Feature;
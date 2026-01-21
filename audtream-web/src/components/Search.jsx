import { useEffect, useState } from "react";
import { Link } from "react-router-dom";

function Search() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (query.length < 2) {
      setResults([]);
      setIsLoading(false);
      return;
    }

    setIsLoading(true);

    const timeout = setTimeout(() => {
      fetch(`/api/search?q=${query}`)
        .then(res => res.json())
        .then(data => {
          setResults(data);
          setIsLoading(false);
        })
        .catch(() => {
          setResults([]);
          setIsLoading(false);
        });
    }, 300);

    return () => clearTimeout(timeout);
  }, [query]);

  const closeDropdown = () => {
  setQuery("");
  setResults([]);
};

  return (
    <div className="relative w-full max-w-xs">
      <input
        value={query}
        onChange={e => setQuery(e.target.value)}
        placeholder="Explore music, artists and playlists"
        className="border-b-2 border-purple-500 bg-transparent p-1 text-gray-50 w-full"
      />

      {/* Dropdown */}
      {query.length >= 2 && (
        <div className="absolute top-full left-0 w-full bg-[#0a0a0a] shadow-md mt-2 rounded z-50">

          {/* Loader */}
          {isLoading && (
            <div className="p-3 text-sm text-gray-400">
              Searching...
            </div>
          )}

          {/* No results */}
          {!isLoading && results.length === 0 && (
            <div className="p-3 text-sm text-gray-400">
              No results found
            </div>
          )}

          {/* Results */}
          {!isLoading && results.length > 0 && (
            <ul>
              {results.map(item => (
                <li key={item.id}>
                  <Link
                    to={`/artist/${item.id}`}
                    onClick={closeDropdown}
                    className="block p-3 text-sm text-white hover:bg-purple-900/30">
                    {item.name}
                  </Link>
                </li>
              ))}
            </ul>
          )}

        </div>
      )}
    </div>
  );
}

export default Search;

import Hero from "../assets/images/hero.png";
import Feature from "../components/Feature";
import Listener from "../assets/images/listener.png";
import Artist from "../assets/images/artist.png";
import trendingData from "../constants/trending.json"
import likeItData from "../constants/mightlikeit.json"

function Home() {
    return(
        <div className="flex flex-col items-center justify-center w-full text-white gap-4">
          { /* Hero */}
          <div className="container mx-auto flex flex-col md:flex-row items-center gap-10 px-6 py-20">
            <div className="flex-1 text-center md:text-left space-y-6">
              <h1 className="text-5xl md:text-6xl font-extrabold leading-tight">
                Tekst1 <br /> SubTekst
              </h1>
              <p className="text-gray-400 text-lg md:text-xl max-w-md">
                Witaj w <span className="text-purple-400 font-semibold">AudTream</span> — potem jakis tekst sie wymysli.
              </p>
              <div className="flex justify-center md:justify-start gap-4">
                <button className="px-6 py-3 bg-purple-600 hover:bg-purple-700 rounded-full font-semibold transition-all cursor-pointer">
                  Rozpocznij
                </button>
                <button className="px-6 py-3 border border-gray-400 hover:border-white rounded-full font-semibold transition-all cursor-pointer">
                  Dowiedz się więcej
                </button>
              </div>
            </div>
            <div className="flex-1 flex justify-center">
              <img
                src={Hero}
                alt="AudTream hero"
                className="w-full max-w-xl rounded-2xl shadow-2xl" 
              />
            </div>
          </div>
          { /* Trends */}
          <div className="container bg-linear-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-8 md:p-12 mb-10 flex flex-col gap-3">
            <div className="px-6">
              <span className="text-2xl md:text-3xl font-extrabold leading-tight">Trending now</span>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4 p-4">
              {trendingData.songs.map((song, index) => (
                <Feature 
                  title={song.title}
                  author={song.author}
                  link={"/track/" + song.id}
                  cover={song.cover}
                />
              ))}
            </div>
          </div>
          {/* Encourage */}
          <div className="container bg-linear-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-8 md:p-12 mb-10">
            <div className="text-center mb-12">
              <h2 className="text-4xl md:text-5xl font-bold text-white mb-4">
                Choose Your <span className="text-purple-400">Path</span>
              </h2>
              <p className="text-gray-400 text-lg">
                The best experiences for listeners and creators
              </p>
            </div>
            <div className="flex flex-col md:flex-row gap-8 max-w-4xl mx-auto">
              <div className="flex-1 group cursor-pointer">
                <div className="relative overflow-hidden rounded-2xl bg-gray-900/50 border border-gray-800 hover:border-green-500/50 transition-all duration-300">
                  <div className="relative h-48 overflow-hidden">
                    <img 
                      src={Listener} 
                      alt="Listener" 
                      className="w-full h-full object-cover opacity-50 group-hover:opacity-60 transition-opacity duration-500"
                    />
                    <div className="absolute inset-0 bg-linear-to-t from-gray-900 via-transparent to-transparent"></div>
                  </div>
                  
                  <div className="p-6">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="w-10 h-10 rounded-full bg-green-900/30 flex items-center justify-center">
                        <svg className="w-6 h-6 text-green-400" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zM9.555 7.168A1 1 0 008 8v4a1 1 0 001.555.832l3-2a1 1 0 000-1.664l-3-2z" clipRule="evenodd" />
                        </svg>
                      </div>
                      <h3 className="text-2xl font-bold text-white">Listener</h3>
                    </div>
                    
                    <p className="text-gray-400 mb-6">
                      Discover, stream, and enjoy music from around the world
                    </p>
                    
                    <button className="w-full py-3 bg-gray-800 hover:bg-green-900/30 text-white rounded-lg font-medium flex items-center justify-center gap-2 transition-all duration-300 group-hover:shadow-lg group-hover:shadow-green-500/10">
                      <span>Explore Music</span>
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>

              <div className="flex-1 group cursor-pointer">
                <div className="relative overflow-hidden rounded-2xl bg-gray-900/50 border border-gray-800 hover:border-amber-500/50 transition-all duration-300">
                  <div className="relative h-48 overflow-hidden">
                    <img 
                      src={Artist} 
                      alt="Artist" 
                      className="w-full h-full object-cover opacity-50 group-hover:opacity-60 transition-opacity duration-500"
                    />
                    <div className="absolute inset-0 bg-linear-to-t from-gray-900 via-transparent to-transparent"></div>
                  </div>
                  
                  <div className="p-6">
                    <div className="flex items-center gap-3 mb-4">
                      <div className="w-10 h-10 rounded-full bg-amber-900/30 flex items-center justify-center">
                        <svg className="w-6 h-6 text-amber-400" fill="currentColor" viewBox="0 0 20 20">
                          <path fillRule="evenodd" d="M7 4a3 3 0 016 0v4a3 3 0 11-6 0V4zm4 10.93A7.001 7.001 0 0017 8a1 1 0 10-2 0A5 5 0 015 8a1 1 0 00-2 0 7.001 7.001 0 006 6.93V17H6a1 1 0 100 2h8a1 1 0 100-2h-3v-2.07z" clipRule="evenodd" />
                        </svg>
                      </div>
                      <h3 className="text-2xl font-bold text-white">Artist</h3>
                    </div>
                    
                    <p className="text-gray-400 mb-6">
                      Share your sound, connect with fans, and grow your career
                    </p>
                    
                    <button className="w-full py-3 bg-gray-800 hover:bg-amber-900/30 text-white rounded-lg font-medium flex items-center justify-center gap-2 transition-all duration-300 group-hover:shadow-lg group-hover:shadow-amber-500/10">
                      <span>Start Creating</span>
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
                      </svg>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
          { /* You might like it */}
          <div className="container bg-linear-to-br from-[#141414] to-[#0a0a0a] rounded-3xl p-8 md:p-12 mb-10 flex flex-col gap-3">
            <div className="px-6">
              <span className="text-2xl md:text-3xl font-extrabold leading-tight">You might like it</span>
            </div>
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4 p-4">
              {likeItData.songs.map((song, index) => (
                <Feature 
                  title={song.title}
                  author={song.author}
                  link={song.link}
                  cover={song.cover}
                />
              ))}
              {likeItData.albums.map((album) => (
                <Feature
                  title={album.title}
                  author={album.author}
                  link={album.link}
                  cover={album.cover}
                />
              ))}
            </div>
          </div>
        </div>
    );
}

export default Home;
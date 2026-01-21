import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import songData from "../constants/song.json";
import PlaylistTrack from "../components/PlaylistTrack";
import formatTime from "../utils/formatTime.js";

function Song() {
  const { id } = useParams();
  const [song, setSong] = useState(null);

  useEffect(() => {
    setSong(songData);

    fetch(`http://localhost:8080/api/v1/web/tracks/get?id=${id}`)
      .then(res => res.json())
      // .then(data => setSong(data));
  }, [id]);

  if (!song) {
    return <div className="text-white">Loading...</div>;
  }

  return (
    <div className="container mx-auto bg-linear-to-br from-[#141414] to-[#0a0a0a] flex flex-col w-full rounded-2xl text-white mt-25 mb-10">
      <div
        className="flex flex-row w-full bg-cover bg-center rounded-t-2xl p-6"
        style={{
          backgroundImage: `url(${song.author_data.profile_banner})`
        }}
      >
        <div className="w-1/2 px-4">
          <img
            src={song.author_data.profile_pic}
            alt={song.author}
            className="w-32 h-32 rounded-full object-cover"
          />
        </div>

        <div className="flex-1 flex items-center">
          <h1 className="text-3xl font-bold">
            {song.author}
          </h1>
        </div>
      </div>

      <div className="p-6 flex flex-row justify-between h-auto items-center">
        <div>
            <h2 className="text-white text-3xl">{song.title}</h2>
            <h4 className="text-gray-300 text-2xl">{song.author}</h4>
        </div>
        <div className="flex flex-row gap-5 h-10">
            <button className="border-2 px-3 rounded-2xl border-purple-700 cursor-pointer"> Like &#9829;</button>
            <button className="border-2 px-3 rounded-2xl border-purple-700 cursor-pointer">Follow Artist</button>
        </div>
      </div>
      <div className="w-full p-6 flex flex-row gap-5">
        <div className="w-1/2 max-h-64 bg-[#242323] flex flex-row p-4 gap-4 rounded-3xl">
            <div className="flex flex-col items-center gap-3 flex-1">
                <img 
            src={song.cover} alt={song.author}
            className="w-48 h-48 object-cover rounded-lg"
             />
             <span className="text-gray-300 font-mono text-xl">{song.likes} <span className="text-purple-300">&#9829;</span></span>
            </div>
            <div className="flex-2 flex flex-col justify-between items-center w-auto">
                <div className="flex flex-row gap-1 justify-between items-center w-full">
                    <span className="font-mono text-2xl">{song.title}</span>
                    <span className="italic">released in {song.release_year}</span>
                </div>
                <div>
                    <button className="bg-purple-700 text-white px-2 py-3 rounded-2xl cursor-pointer transform hover:bg-purple-900 hover:shadow-purple-800 hover:shadow-xl/30 transition-all duration-300">Listen in App</button>
                </div>
                <div className="w-full flex justify-center flex-col items-center">
                    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-play-circle" viewBox="0 0 16 16">
                        <path d="M8 15A7 7 0 1 1 8 1a7 7 0 0 1 0 14m0 1A8 8 0 1 0 8 0a8 8 0 0 0 0 16"/>
                        <path d="M6.271 5.055a.5.5 0 0 1 .52.038l3.5 2.5a.5.5 0 0 1 0 .814l-3.5 2.5A.5.5 0 0 1 6 10.5v-5a.5.5 0 0 1 .271-.445"/>
                    </svg>
                    <div className="flex flex-row gap-1 justify-center items-center w-full">
                        <span className="text-sm">0:00</span>
                        <div className="w-1/2 h-1 bg-gray-400"></div>
                        <span className="text-sm">{formatTime(song.length)}</span>
                    </div>
                </div>
            </div>
        </div>
        <div className="w-1/2 bg-[#242323] flex flex-col p-4 gap-4 rounded-3xl">
            <div className="p-3">
                <span className="text-white text-lg">Album: <span className="text-purple-300">{song.album_name}</span></span>
            </div>
            <div className="flex flex-col gap-4">
                {song.album_content.map((album_song, index) => (
                    <PlaylistTrack 
                        id={album_song.id}
                        title={album_song.title}
                        length={album_song.length}
                        listened={album_song.listened}
                    />
                ))}
            </div>
        </div>
      </div>
    </div>
  );
}

export default Song;

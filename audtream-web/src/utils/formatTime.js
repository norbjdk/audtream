const formatTime = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const secs = seconds % 60;

  return `${minutes}:${String(secs).padStart(2, '0')}`;
};

export default formatTime;